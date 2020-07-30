package simplecharacterbuilder.characterbuilder.maincomponents.various;

import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;

import simplecharacterbuilder.characterbuilder.util.holder.ApplicationFrameHolder;
import simplecharacterbuilder.characterbuilder.util.holder.FranchiseCache;
import simplecharacterbuilder.characterbuilder.util.holder.JAXBContextHolder;
import simplecharacterbuilder.characterbuilder.util.holder.PostInfoXmlGenerationRunnableHolder;
import simplecharacterbuilder.characterbuilder.util.transform.ValueFormatter;
import simplecharacterbuilder.characterbuilder.util.ui.PanelList;
import simplecharacterbuilder.characterbuilder.util.ui.UIComponentFactory;
import simplecharacterbuilder.common.generated.Actor;
import simplecharacterbuilder.common.generated.Actor.Name;
import simplecharacterbuilder.common.generated.Actor.Social;
import simplecharacterbuilder.common.generated.Behaviors;
import simplecharacterbuilder.common.resourceaccess.GameFileAccessor;
import simplecharacterbuilder.common.resourceaccess.PropertyRepository;
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent;
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.CharacterBuilderMainComponent;

public class SocialMainComponent extends CharacterBuilderMainComponent {
	private static final int SELECTIONPANEL_WIDTH = MAINPANEL_WIDTH - 2 * GAP_WIDTH;
	private static final int SELECTIONPANEL_HEIGHT = MAINPANEL_HEIGHT - CONTROLPANEL_HEIGHT - 3 * GAP_WIDTH;
	private static final int ITEM_HEIGHT = 55;
	private static final int PORTRAIT_OFFSET = 0;
	private static final int PORTRAIT_SIZE = ITEM_HEIGHT - 2 * PORTRAIT_OFFSET;
	private static final int VERT_COMPONENT_GAP = (ITEM_HEIGHT - 2 * 22) / 3;
	private static final int LEFT_VERT_GAP = 8;
	private static final int YPOS_SECOND_ROW = 22 + 2 * VERT_COMPONENT_GAP;
	private static final int OPTIONS_WIDTH = 95;

	private static final String[] RELATIONSHIPS = readRelationshipOptions();

	private static final Font NAME_FONT = UIComponentFactory.createFormattedLabel("123", 0, 0, 0, 0).getFont();

	private SocialSelectionPanel selectionPanel = new SocialSelectionPanel();

	public SocialMainComponent() {
		this.mainPanel.add(selectionPanel);
	}

	@Override
	public void setValues(Actor actor) {
		if (actor.getSocial() == null) {
			actor.setSocial(new Social());
		}
		List<String> behaviors = actor.getSocial().getBehavior();
		for (FranchiseCharacterComponentHolder holder : this.selectionPanel.displayedCharacters) {
			if (holder.getSelectedRelToOption() != null) {
				behaviors.add(ValueFormatter.formatRelationship(holder.getSelectedRelToOption(), holder.getFullName()));
			}
			if (holder.getSelectedRelFromOption() != null) {
				Name actorName = actor.getName();
				PostInfoXmlGenerationRunnableHolder.add(() -> {
					try {
						writeRelationshipToExistingInfoXml(holder.getInfoXml(),
								ValueFormatter.formatRelationship(holder.getSelectedRelFromOption(),
										ValueFormatter.formatFullName(actorName.getFirst(), actorName.getMiddle(),
												actorName.getLast(), false)));
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}
		}
		if (actor.getSocial().getBehavior().isEmpty()) {
			actor.setSocial(null);
		}
	}
	
	@Override
	public void enable() {
		super.enable();
		ApplicationFrameHolder.setApplicationFrameTitle("Relationships");
	}

	public boolean isEmpty() {
		return selectionPanel.displayedCharacters.size() == 0;
	}

	private static String[] readRelationshipOptions() {
		try {
			List<String> relationships = new ArrayList<>();
			relationships.add(ValueFormatter.NONE_OPTION);
			((Behaviors) JAXBContextHolder.createUnmarshaller()
					.unmarshal(GameFileAccessor.getFileFromProperty(PropertyRepository.COMMON_BEHAVIORS))).getBehavior()
							.stream().forEach(b -> relationships.add(b.getName()));
			return relationships.toArray(new String[0]);
		} catch (JAXBException e) {
			throw new IllegalArgumentException("Unmarshalling Common Behavior file failed", e);
		}
	}

	private void writeRelationshipToExistingInfoXml(File infoXml, String relationshipString) throws IOException {
		String xmlString = new String(Files.readAllBytes(infoXml.toPath()), StandardCharsets.UTF_8);
		xmlString = xmlString.contains("</Social>")
				? xmlString.replace("</Social>", "\t<Behavior>" + relationshipString + "</Behavior>\n\t</Social>")
				: xmlString.replace("</Actor>",
						"\n\t<Social>\n\t\t<Behavior>" + relationshipString + "</Behavior>\n\t</Social>\n</Actor>");
		FileUtils.writeStringToFile(infoXml, xmlString, StandardCharsets.UTF_8);
	}

	@SuppressWarnings("serial")
	private class SocialSelectionPanel extends PanelList {
		private final List<FranchiseCharacterComponentHolder> displayedCharacters = new ArrayList<>();

		public SocialSelectionPanel() {
			super(GAP_WIDTH, GAP_WIDTH, SELECTIONPANEL_WIDTH, SELECTIONPANEL_HEIGHT, ITEM_HEIGHT);
			FranchiseCache.addRefreshListener(() -> this.refresh());
		}

		public void refresh() {
			displayedCharacters.clear();
			List<ItemContainer> itemContainers = new ArrayList<>();
			Map<Actor, File> actorPortraitMap = FranchiseCache.getActorsAndPortraits();
			actorPortraitMap.keySet().stream().sorted(createInstallmentComparator())
					.forEach(a -> itemContainers.add(createCharacterContainer(a, actorPortraitMap.get(a),
							FranchiseCache.getActorsAndInfoXmls().get(a))));
			setItemContainers(itemContainers);
		}

		private ItemContainer createCharacterContainer(Actor actor, File portrait, File infoXml) {
			Name name = actor.getName();
			String fullName = ValueFormatter.formatFullName(name.getFirst(), name.getMiddle(), name.getLast(), false);

			ItemContainer itemContainer = new ItemContainer();
			itemContainer.setBorder(CharacterBuilderComponent.BORDER);
			itemContainer.add(createPortraitLabel(portrait));
			itemContainer.add(createNameLabel(fullName, actor.getSource().getInstallment()));
			itemContainer.add(createRelationshipLabel("Relationship to:", ITEM_HEIGHT + LEFT_VERT_GAP));
			itemContainer.add(
					createRelationshipLabel("Relationship back:", SELECTIONPANEL_WIDTH - OPTIONS_WIDTH - 105 - 30));
			JComboBox<String> relToOptionList = createOptionsList(157,
					"Select how your new character feels about this character.");
			itemContainer.add(relToOptionList);
			JComboBox<String> relFromOptionList = createOptionsList(SELECTIONPANEL_WIDTH - OPTIONS_WIDTH - 25,
					"Select how this character feels about your character.");
			itemContainer.add(relFromOptionList);
			displayedCharacters
					.add(new FranchiseCharacterComponentHolder(fullName, infoXml, relToOptionList, relFromOptionList));
			return itemContainer;
		}

		private JComboBox<String> createOptionsList(int xPos, String tooltip) {
			JComboBox<String> comboBox = UIComponentFactory.createComboBox(xPos, YPOS_SECOND_ROW, OPTIONS_WIDTH,
					tooltip, RELATIONSHIPS);
			comboBox.setFont(NAME_FONT);
			return comboBox;
		}

		private JLabel createPortraitLabel(File portrait) {
			JLabel portraitLabel = new JLabel("No Portrait");
			portraitLabel.setBounds(PORTRAIT_OFFSET, PORTRAIT_OFFSET, PORTRAIT_SIZE, PORTRAIT_SIZE);
			portraitLabel.setBorder(CharacterBuilderComponent.BORDER);
			portraitLabel.setHorizontalAlignment(SwingConstants.LEFT);
			try {
				Image scaledImage = new ImageIcon(portrait.getAbsolutePath()).getImage()
						.getScaledInstance(PORTRAIT_SIZE, PORTRAIT_SIZE, Image.SCALE_SMOOTH);
				portraitLabel.setIcon(new ImageIcon(scaledImage));
			} catch (Exception e) {
			}
			return portraitLabel;
		}

		private Component createNameLabel(String fullName, String installment) {
			StringBuilder formattedName = new StringBuilder(fullName);
			if (!ValueFormatter.isEmpty(installment)) {
				formattedName.append(" (").append(installment).append(")");
			}
			JLabel label = UIComponentFactory.createFormattedLabel(formattedName.toString(),
					ITEM_HEIGHT + LEFT_VERT_GAP, VERT_COMPONENT_GAP, SELECTIONPANEL_WIDTH - ITEM_HEIGHT - 13,
					JLabel.LEFT);
			label.setFont(NAME_FONT);
			return label;
		}

		private Comparator<Actor> createInstallmentComparator() {
			return new Comparator<Actor>() {
				@Override
				public int compare(Actor actor1, Actor actor2) {
					return actor1.getSource() == null || actor1.getSource().getInstallment() == null ? -1
							: actor2.getSource() == null ? 1
									: actor1.getSource().getInstallment()
											.compareTo(actor2.getSource().getInstallment());
				}
			};
		}

		private JLabel createRelationshipLabel(String text, int xPos) {
			JLabel label = UIComponentFactory.createFormattedLabel(text, xPos, YPOS_SECOND_ROW, 105, JLabel.LEFT);
			label.setFont(NAME_FONT);
			return label;
		}
	}

	private static class FranchiseCharacterComponentHolder {
		private final String fullName;
		private final File infoXml;
		private final JComboBox<String> relToOptionList;
		private final JComboBox<String> relFromOptionList;

		FranchiseCharacterComponentHolder(String fullName, File infoXml, JComboBox<String> relToOptionList,
				JComboBox<String> relFromOptionList) {
			this.fullName = fullName;
			this.infoXml = infoXml;
			this.relToOptionList = relToOptionList;
			this.relFromOptionList = relFromOptionList;
		}

		public String getFullName() {
			return this.fullName;
		}

		public File getInfoXml() {
			return this.infoXml;
		}

		public String getSelectedRelToOption() {
			return ValueFormatter.checkStringForNoneOption((String) relToOptionList.getSelectedItem());
		}

		public String getSelectedRelFromOption() {
			return ValueFormatter.checkStringForNoneOption((String) relFromOptionList.getSelectedItem());
		}
	}

}
