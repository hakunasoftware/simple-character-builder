package simplecharacterbuilder.characterbuilder.maincomponents.various;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;

import simplecharacterbuilder.characterbuilder.util.holder.ApplicationFrameHolder;
import simplecharacterbuilder.characterbuilder.util.holder.JAXBContextHolder;
import simplecharacterbuilder.characterbuilder.util.transform.ValueFormatter;
import simplecharacterbuilder.characterbuilder.util.ui.UIComponentFactory;
import simplecharacterbuilder.common.generated.Actor;
import simplecharacterbuilder.common.generated.Actor.Skills;
import simplecharacterbuilder.common.generated.CombatClassType;
import simplecharacterbuilder.common.generated.CombatClasses;
import simplecharacterbuilder.common.resourceaccess.ConfigReaderRepository;
import simplecharacterbuilder.common.resourceaccess.GameFileAccessor;
import simplecharacterbuilder.common.resourceaccess.PropertyRepository;
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.CharacterBuilderMainComponent;

public class CombatClassSelectorMainComponent extends CharacterBuilderMainComponent {
	public static final int SELECTIONS_COUNT = ConfigReaderRepository.getCharacterbuilderConfigReader().readInt(PropertyRepository.MAX_COMBATCLASSES);
	private static final int SELECTION_PANEL_WIDTH = CharacterBuilderMainComponent.MAINPANEL_WIDTH - 2 * GAP_WIDTH;
	
	private static final String WEAPONS = "Weapons";
	private static final String SKILL_GROUPS = "Skill Groups";
	private static final String GEAR = "Gear";
	
	private static final Map<String, CombatClassType> COMBAT_CLASSES = new HashMap<>();
	static {
		if(SELECTIONS_COUNT < 0 || SELECTIONS_COUNT > 3) {
			throw new IllegalArgumentException("The amount of choosable regular combat classes has to be between 0 and 3.");
		}
	}
	private final List<JComboBox<String>> comboBoxes = new ArrayList<>();
	

	public CombatClassSelectorMainComponent() {
		readRegularCombatClasses();
		addCombatClassSelections();
	}

	@Override
	public void setValues(Actor actor) {
		Set<String> selectedCombatClasses = comboBoxes.stream().map(c -> ValueFormatter.checkStringForNoneOption((String) c.getSelectedItem()))
				.filter(c -> c != null).collect(Collectors.toSet());
		if(selectedCombatClasses.isEmpty()) {
			return;
		}
		if(actor.getSkills() == null) {
			actor.setSkills(new Skills());
		}
		actor.getSkills().getCombatClass().addAll(selectedCombatClasses);
	}
	
	@Override
	public void enable() {
		super.enable();
		ApplicationFrameHolder.setApplicationFrameTitle("Combat Classes");
	}

	private void readRegularCombatClasses() {
		COMBAT_CLASSES.put(ValueFormatter.NONE_OPTION, new CombatClassType());
		File regularCombatClassesDir = GameFileAccessor.getFileFromProperty(PropertyRepository.REGULAR_COMBAT_CLASSES);
		Unmarshaller unmarshaller = JAXBContextHolder.createUnmarshaller();
		for (File file : FileUtils.listFiles(regularCombatClassesDir, new String[] { "xml" }, false)) {
			Object unmarshalledObject;
			try {
				unmarshalledObject = unmarshaller.unmarshal(file);
				if (unmarshalledObject instanceof CombatClasses) {
					((CombatClasses) unmarshalledObject).getCombatClass().forEach(c -> {
						COMBAT_CLASSES.put(c.getName(), c);
					});
				} else {
					@SuppressWarnings("unchecked")
					CombatClassType combatClass = ((JAXBElement<CombatClassType>) unmarshalledObject).getValue();
					COMBAT_CLASSES.put(combatClass.getName(), combatClass);
				}
			} catch (Exception e) {
				throw new IllegalArgumentException("Failed to unmarshal CombatClasses from " + file.getAbsolutePath(), e);
			}
		}
	}
	
	private void addCombatClassSelections() {
		String tooltip = "This tool only allows up to " + SELECTIONS_COUNT + " (regular) Combat Classes. Anything beyond that should probably be a custom Combat Class and be created manually.";
		String[] options = COMBAT_CLASSES.keySet().stream().sorted((c1, c2) -> c1.compareTo(c2)).collect(Collectors.toList()).toArray(new String[0]);
		for(int i = 0; i < SELECTIONS_COUNT; i++) {
			addCombatClassSelection(i, tooltip, options);
		}
	}
	
	private void addCombatClassSelection(int i, String tooltip, String[] options) {
		JPanel optionsPanel = UIComponentFactory.createBorderedPanel(GAP_WIDTH, (i + 1) * GAP_WIDTH + i * 70, SELECTION_PANEL_WIDTH, 70);
		optionsPanel.add(UIComponentFactory.createFormattedLabel("Combat Class " + (i + 1) + ": ", 15, 8, 95, JLabel.LEFT));
		
		JComboBox<String> comboBox = UIComponentFactory.createComboBox(115, 10, 130, tooltip, options);
		comboBoxes.add(comboBox);
		optionsPanel.add(comboBox);
		
		JLabel descriptionLabel = UIComponentFactory.createFormattedLabel(null, 15, 32, 230, 38, JLabel.LEFT);
		descriptionLabel.setForeground(new Color(150, 150, 150, 255));
		descriptionLabel.setHorizontalAlignment(JLabel.CENTER);
		optionsPanel.add(descriptionLabel);
		
		JLabel weaponsLabel = createAdditionalInfoLabel(optionsPanel, WEAPONS, 1);
		JLabel gearLabel = createAdditionalInfoLabel(optionsPanel, GEAR, 24);
		JLabel skillGroupsLabel = createAdditionalInfoLabel(optionsPanel, SKILL_GROUPS, 47);
		
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				@SuppressWarnings("unchecked")
				String selection = ((JComboBox<String>) event.getSource()).getSelectedItem().toString();
				CombatClassType combatClass = COMBAT_CLASSES.get(selection);
				String description = combatClass.getDescription() != null ? combatClass.getDescription() : "";
				descriptionLabel.setText("<html><center><i>" + description + "</i></center></html>");
				
				if(combatClass.getUsableEquipment() != null) {
					List<String> weapons = new ArrayList<>(combatClass.getUsableEquipment().getMainHand());
					weapons.addAll(combatClass.getUsableEquipment().getTwoHands());
					addStringsToTextAndToolTip(weaponsLabel, weapons);
					addStringsToTextAndToolTip(gearLabel, combatClass.getUsableEquipment().getAllow());
				} else {
					clearTextAndTooltip(weaponsLabel);
					clearTextAndTooltip(gearLabel);
				}
				if(combatClass.getSkillGroups() != null) {
					List<String> skillGroups = new ArrayList<>(combatClass.getSkillGroups().getStarting());
					skillGroups.addAll(combatClass.getSkillGroups().getLearnable());
					addStringsToTextAndToolTip(skillGroupsLabel, skillGroups);
				} else {
					clearTextAndTooltip(skillGroupsLabel);
				}
			}
		});
		this.mainPanel.add(optionsPanel);
	}
	
	private JLabel createAdditionalInfoLabel(JPanel parent, String name, int yPos) {
		parent.add(UIComponentFactory.createFormattedLabel(name + ":", 250, yPos, 75, JLabel.RIGHT));
		JLabel dataLabel = UIComponentFactory.createFormattedLabel(null, 332, yPos, SELECTION_PANEL_WIDTH - 335, JLabel.LEFT);
		parent.add(dataLabel);
		return dataLabel;
	}
	
	private void addStringsToTextAndToolTip(JLabel label, List<String> items) {
		String text = ValueFormatter.formatListWithCommas(items);
		label.setText(text);
		label.setToolTipText("<html>" + text + "</html>");
	}
	
	private void clearTextAndTooltip(JLabel label) {
		label.setText("");
		label.setToolTipText("");
	}
	
}
