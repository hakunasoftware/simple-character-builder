package simplecharacterbuilder.characterbuilder.traits;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;

import simplecharacterbuilder.characterbuilder.util.holder.TraitRepository;
import simplecharacterbuilder.characterbuilder.util.transform.ValueFormatter;
import simplecharacterbuilder.characterbuilder.util.ui.CheckBoxList;
import simplecharacterbuilder.characterbuilder.util.ui.CheckBoxList.ItemPanelMouseListenerFactory;
import simplecharacterbuilder.characterbuilder.util.ui.UIComponentFactory;
import simplecharacterbuilder.characterbuilder.util.ui.UIComponentFactory.ListComponentDto;
import simplecharacterbuilder.common.generated.Actor;
import simplecharacterbuilder.common.generated.Actor.Skills;
import simplecharacterbuilder.common.generated.TraitType;
import simplecharacterbuilder.common.generated.TraitType.Incompatible;
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.CharacterBuilderMainComponent;

public class TraitSelectorMainComponent extends CharacterBuilderMainComponent {
	private static final int SELECTION_YPOS_OFFSET = 10;
	private static final int SELECTION_WIDTH = (MAINPANEL_WIDTH - 4 * GAP_WIDTH) / 3;
	private static final int SELECTION_OPTIONS_HEIGHT = MAINPANEL_HEIGHT - 3 * GAP_WIDTH - CONTROLPANEL_HEIGHT
			- SELECTION_YPOS_OFFSET;
	private static final int XPOS_SECOND_TABLE = SELECTION_WIDTH + 2 * GAP_WIDTH;
	private static final int XPOS_THIRD_TABLE = MAINPANEL_WIDTH - SELECTION_WIDTH - GAP_WIDTH;
	private static final int REMOVE_BUTTON_HEIGHT = 30;
	private static final Color HEADLINE_COLOR = new Color(120, 120, 120, 255);

	private Map<String, TraitType> traitMap = new HashMap<>();

	private CheckBoxList personalityCheckBoxes;
	private CheckBoxList backgroundCheckBoxes;
	private JList<String> selectedTraitJList;
	private JLabel descriptionPanel;

	public TraitSelectorMainComponent() {
		initializeTraitMap();
		this.personalityCheckBoxes = addTraitList(TraitRepository.getPersonalityTraits(), GAP_WIDTH);
		List<TraitType> backgroundAndPhysicalTraits = TraitRepository.getBackgroundTraits();
		backgroundAndPhysicalTraits.addAll(TraitRepository.getPhysicalTraits());
		this.backgroundCheckBoxes = addTraitList(backgroundAndPhysicalTraits, XPOS_SECOND_TABLE);

		ListComponentDto dto = UIComponentFactory.createList(XPOS_THIRD_TABLE, GAP_WIDTH + SELECTION_YPOS_OFFSET,
				SELECTION_WIDTH, SELECTION_OPTIONS_HEIGHT - REMOVE_BUTTON_HEIGHT + 1);
		this.mainPanel.add(dto.getContainer());
		this.selectedTraitJList = dto.getList();

		addDescriptionPanel();
		addTableLabels();
		addRemoveButton();
	}

	@Override
	public void setValues(Actor actor) {
		refresh();
		Skills skills = actor.getSkills();
		if (skills == null) {
			actor.setSkills(new Skills());
		}
		List<String> traits = actor.getSkills().getTrait();
		traits.addAll(personalityCheckBoxes.getSelectedItems());
		traits.addAll(backgroundCheckBoxes.getSelectedItems());
		if(traits.isEmpty()) {
			actor.setSkills(null);
		}
	}

	private void addTableLabels() {
		addTableLabel("Personality Traits", GAP_WIDTH,
				"Select all fitting personality traits. Note that some traits are incompatible and therefore can't be selected at the same time.");
		addTableLabel("Background & Physical", XPOS_SECOND_TABLE,
				"Select all fitting background and physical traits. Note that some traits are incompatible and therefore can't be selected at the same time.");
		addTableLabel("Selected Traits", XPOS_THIRD_TABLE, "This list displays all currently selected traits.");
	}

	private void addTableLabel(String text, int xPos, String tooltip) {
		JLabel label = UIComponentFactory.createFormattedLabel(text, xPos, 2, SELECTION_WIDTH);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setForeground(HEADLINE_COLOR);
		label.setToolTipText(tooltip);
		this.mainPanel.add(label);
	}

	private void addRemoveButton() {
		JButton button = UIComponentFactory.createButton("Remove selected", XPOS_THIRD_TABLE,
				GAP_WIDTH + SELECTION_YPOS_OFFSET + SELECTION_OPTIONS_HEIGHT - REMOVE_BUTTON_HEIGHT, 
				SELECTION_WIDTH, REMOVE_BUTTON_HEIGHT);
		this.mainPanel.add(button);
		button.addActionListener(e -> {
			List<String> itemsToBeRemoved = this.selectedTraitJList.getSelectedValuesList();
			this.personalityCheckBoxes.select(itemsToBeRemoved, false);
			this.backgroundCheckBoxes.select(itemsToBeRemoved, false);
			refresh();
		});
	}

	private void initializeTraitMap() {
		addTraitListToMap(TraitRepository.getPersonalityTraits());
		addTraitListToMap(TraitRepository.getBackgroundTraits());
		addTraitListToMap(TraitRepository.getPhysicalTraits());
	}

	private void addTraitListToMap(List<TraitType> traits) {
		traits.stream().forEach(t -> {
			traitMap.put(t.getName(), t);
		});
	}

	private CheckBoxList addTraitList(List<TraitType> traits, int xPos) {
		List<String> options = traits.stream().map(t -> t.getName()).collect(Collectors.toList());
		options.sort((a, b) -> a.compareTo(b));
		CheckBoxList checkBoxList = UIComponentFactory.createCheckBoxList(options, xPos,
				GAP_WIDTH + SELECTION_YPOS_OFFSET, SELECTION_WIDTH, SELECTION_OPTIONS_HEIGHT,
				new DescriptionUpdatingMouseListenerFactory());
		this.mainPanel.add(checkBoxList);
		return checkBoxList;
	}

	private void addDescriptionPanel() {
		this.descriptionPanel = UIComponentFactory.createFormattedLabel(null, GAP_WIDTH,
				MAINPANEL_HEIGHT - GAP_WIDTH - CONTROLPANEL_HEIGHT,
				MAINPANEL_WIDTH - 3 * GAP_WIDTH - CONTROLPANEL_WIDTH, CONTROLPANEL_HEIGHT);
		this.descriptionPanel.setBorder(CharacterBuilderMainComponent.BORDER);
		this.descriptionPanel.setHorizontalAlignment(JLabel.CENTER);
		this.mainPanel.add(this.descriptionPanel);
		setDescription(null, null);
	}

	private void setDescription(String description, List<String> incompatibleTraits) {
		StringBuilder descriptionBuilder = new StringBuilder("<html><center>");
		if (!ValueFormatter.isEmpty(description) && !description.equals("To be written.")) {
			descriptionBuilder.append("<p>").append(description).append("</p>");
		}
		if (incompatibleTraits != null && !incompatibleTraits.isEmpty()) {
			descriptionBuilder.append("</b><p><i><b>Incompatible with: </b>");
			for (int i = 0; i < incompatibleTraits.size(); i++) {
				descriptionBuilder.append(incompatibleTraits.get(i));
				if (i < incompatibleTraits.size() - 1) {
					descriptionBuilder.append(", ");
				}
			}
			descriptionBuilder.append("</i></p>");
		}
		this.descriptionPanel.setText(descriptionBuilder.append("<b></center></html>").toString());
	}

	private synchronized void refresh() {
		List<String> personalityTraits = personalityCheckBoxes.getSelectedItems();
		List<String> backgroundTraits = backgroundCheckBoxes.getSelectedItems();

		DefaultListModel<String> model = new DefaultListModel<>();
		List<String> selectedTraits = new ArrayList<>(personalityTraits);
		selectedTraits.addAll(backgroundTraits);
		selectedTraits.sort((a, b) -> a.compareTo(b));
		selectedTraits.stream().forEach(t -> model.addElement(t));
		TraitSelectorMainComponent.this.selectedTraitJList.setModel(model);

		personalityCheckBoxes.setDisabledItems(determineIncompatibleTraits(personalityTraits));
		backgroundCheckBoxes.setDisabledItems(determineIncompatibleTraits(backgroundTraits));
	}

	private Set<String> determineIncompatibleTraits(List<String> traits) {
		Set<String> incompatiblePersonalityTraits = new HashSet<>();
		traits.stream().forEach(t -> {
			Incompatible incompatible = traitMap.get(t).getIncompatible();
			if (incompatible != null) {
				incompatiblePersonalityTraits.addAll(incompatible.getWith());
			}
		});
		return incompatiblePersonalityTraits;
	}

	private class DescriptionUpdatingMouseListenerFactory implements ItemPanelMouseListenerFactory {
		@Override
		public MouseListener createListener(JCheckBox checkBox) {
			return new AbstractMouseListener() {
				@Override
				public void mouseEntered(MouseEvent arg0) {
					TraitType trait = TraitSelectorMainComponent.this.traitMap.get(checkBox.getText());
					Incompatible incompatible = trait.getIncompatible();
					TraitSelectorMainComponent.this.setDescription(trait.getDescription(),
							incompatible != null ? incompatible.getWith() : null);
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					TraitSelectorMainComponent.this.setDescription(null, null);
					refresh();
				}

				@Override
				public void mouseClicked(MouseEvent arg0) {
					refresh();
				}
			};
		}
	}
}
