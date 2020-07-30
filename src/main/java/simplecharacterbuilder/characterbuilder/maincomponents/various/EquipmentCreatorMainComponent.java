package simplecharacterbuilder.characterbuilder.maincomponents.various;

import static simplecharacterbuilder.characterbuilder.maincomponents.bodysprites.SpriteLoaderMainComponent.FLAT_CHESTED;
import static simplecharacterbuilder.characterbuilder.maincomponents.bodysprites.SpriteLoaderMainComponent.LARGE_BREASTS;
import static simplecharacterbuilder.characterbuilder.maincomponents.bodysprites.SpriteLoaderMainComponent.MEDIUM_BREASTS;
import static simplecharacterbuilder.characterbuilder.maincomponents.bodysprites.SpriteLoaderMainComponent.SMALL_BREASTS;
import static simplecharacterbuilder.characterbuilder.maincomponents.bodysprites.SpriteLoaderMainComponent.XL_BREASTS;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Stream;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import simplecharacterbuilder.characterbuilder.core.CharacterBuilderControlPanel;
import simplecharacterbuilder.characterbuilder.util.holder.ApplicationFrameHolder;
import simplecharacterbuilder.characterbuilder.util.holder.EquipTypeRepository;
import simplecharacterbuilder.characterbuilder.util.holder.ImageFileHolder;
import simplecharacterbuilder.characterbuilder.util.holder.PostInfoXmlGenerationRunnableHolder;
import simplecharacterbuilder.characterbuilder.util.transform.ValueFormatter;
import simplecharacterbuilder.characterbuilder.util.ui.PictureLoader;
import simplecharacterbuilder.characterbuilder.util.ui.PreviewLabel;
import simplecharacterbuilder.characterbuilder.util.ui.UIComponentFactory;
import simplecharacterbuilder.characterbuilder.util.ui.UIComponentFactory.ListComponentDto;
import simplecharacterbuilder.common.generated.Actor;
import simplecharacterbuilder.common.generated.Actor.Equipment;
import simplecharacterbuilder.common.generated.Actor.Name;
import simplecharacterbuilder.common.generated.EquipTypeType;
import simplecharacterbuilder.common.generated.EquipTypeType.DrawIndices;
import simplecharacterbuilder.common.generated.EquipTypeType.Slots;
import simplecharacterbuilder.common.resourceaccess.ConfigReader;
import simplecharacterbuilder.common.resourceaccess.ConfigReaderRepository;
import simplecharacterbuilder.common.resourceaccess.GameFileAccessor;
import simplecharacterbuilder.common.resourceaccess.PropertyRepository;
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.CharacterBuilderMainComponent;
import simplecharacterbuilder.common.uicomponents.ControlPanel;

public class EquipmentCreatorMainComponent extends CharacterBuilderMainComponent {
	private static final int PANEL_WIDTH = MAINPANEL_WIDTH - CONTROLPANEL_WIDTH - 3 * GAP_WIDTH;
	private static final int PANEL_HEIGHT = MAINPANEL_HEIGHT - CONTROLPANEL_HEIGHT - 2 * GAP_WIDTH;
	private static final int CONTROLBUTTON_HEIGHT = 25;
	private static final int CONTROLBUTTON_WIDTH = PANEL_WIDTH / 3;
	private static final int CONTROLBUTTON_YPOS = MAINPANEL_HEIGHT - CONTROLBUTTON_HEIGHT - GAP_WIDTH;
	private static final int LIST_HEIGHT = MAINPANEL_HEIGHT - PANEL_HEIGHT - CONTROLBUTTON_HEIGHT - 2 * GAP_WIDTH + 2;
	private static final int ADD_BUTTON_HEIGHT = 25;
	private static final int ADD_BUTTON_OFFSET = 10;
	private static final int SELECTION_OFFSET = 8;
	private static final int SELECTION_XPOS = 85;
	private static final int SELECTION_WIDTH = PANEL_WIDTH - SELECTION_XPOS - SELECTION_OFFSET;
	private static final int PICLOADER_HEIGHT = PANEL_HEIGHT - ADD_BUTTON_HEIGHT - 6 * SELECTION_OFFSET
			- ADD_BUTTON_OFFSET - 84;
	private static final int PICLOADER_WIDTH = (int) ((PICLOADER_HEIGHT / 192d) * 128);
	private static final int PICLOADER_YPOS = 84 + 5 * SELECTION_OFFSET;
	private static final int MAIN_PICLOADER_ONLY_XPOS = (PANEL_WIDTH - PICLOADER_WIDTH) / 2;
	private static final int MAIN_PICLOADER_EXTRA_XPOS = 70;
	private static final int EXTRA_PICLOADER_XPOS = PANEL_WIDTH - MAIN_PICLOADER_EXTRA_XPOS - PICLOADER_WIDTH;
	
	private static final String ADD_BUTTON_TEXT = "Add Item";
	
	private static final String HIDDEN_SUFFIX = " [hidden]";

	private JComboBox<String> categoryComboBox;
	private JComboBox<String> equipTypeComboBox;
	private JTextField nameTextField;
	private JTextField descriptionTextField;
	private PictureLoader mainSpriteLoader;
	private PictureLoader extraSpriteLoader;
	private PreviewLabel previewLabel;
	private JButton saveButton;

	private List<String> alreadyExistingItems;

	private final Map<String, ItemDto> createdEquipment = new HashMap<>();
	private final JList<String> createdEquipList;
	
	private final Queue<ItemDto> itemsToBeEdited = new LinkedList<>();
	private boolean editingModeEnabled = false;
	
	private EquipTypeType currentEquipType;

	public EquipmentCreatorMainComponent() {
		this.mainPanel.add(createSelectionPanel());

		ListComponentDto listDto = UIComponentFactory.createList(GAP_WIDTH, PANEL_HEIGHT + GAP_WIDTH - 1, PANEL_WIDTH,
				LIST_HEIGHT);
		this.createdEquipList = listDto.getList();
		this.mainPanel.add(listDto.getContainer());

		JButton hideButton = createControlButton("Hide", GAP_WIDTH, CONTROLBUTTON_WIDTH);
		hideButton.setToolTipText("Hides/Unhides the selected items in the preview to try how different combinations of items look. The items will still be saved normally.");
		hideButton.addActionListener(e -> hideSelectedItems());
		this.mainPanel.add(hideButton);
		JButton editButton = createControlButton("Edit", GAP_WIDTH + CONTROLBUTTON_WIDTH - 1, CONTROLBUTTON_WIDTH + 2);
		editButton.setToolTipText("Edit the selected items one by one.");
		editButton.addActionListener(e -> editSelectedItems());
		this.mainPanel.add(editButton);
		JButton deleteButton = createControlButton("Delete", GAP_WIDTH + 2 * CONTROLBUTTON_WIDTH,
				PANEL_WIDTH - 2 * CONTROLBUTTON_WIDTH);
		deleteButton.setToolTipText("Deletes the selected items.");
		deleteButton.addActionListener(e -> removeSelectedItems());
		this.mainPanel.add(deleteButton);

		this.previewLabel = new PreviewLabel(CharacterBuilderControlPanel.X_POS + (ControlPanel.WIDTH_BASIC - 128) / 2, 30, true);
		this.mainPanel.add(this.previewLabel);
	}

	@Override
	public void setValues(Actor actor) {
		if(this.createdEquipment.isEmpty()) {
			actor.setEquipment(null);
			return;
		}
		
		if(actor.getEquipment() == null) {
			actor.setEquipment(new Equipment());
		}
		this.createdEquipment.values().stream().forEach(e -> actor.getEquipment().getEquip().add(e.getName()));
		
		if(actor.getEquipment().getEquip().isEmpty()) {
			actor.setEquipment(null);
		}
		
		String installment = actor.getSource().getInstallment();
		String franchiseFolder = ValueFormatter.isEmpty(installment) ? actor.getSource().getFranchise(): installment;
		Name name = actor.getName();
		String fullName = ValueFormatter.formatFullName(name.getFirst(), name.getMiddle(), name.getLast(), false);
		String folderName = "Characters/" + franchiseFolder + "/" + fullName;

		PostInfoXmlGenerationRunnableHolder.add(() -> copySpritesToGameFolder(actor, folderName));
		PostInfoXmlGenerationRunnableHolder.add(() -> writeEquipmentToXml(actor, this.createdEquipment.values(), folderName));
	}

	private void copySpritesToGameFolder(Actor actor, String folderName) {
		File equipSpriteFolder = getEquipSpriteFolderForActor(actor, folderName);
		if(!equipSpriteFolder.mkdirs()) {
			JOptionPane.showMessageDialog(null, "EquipSprite folder could not be created - it may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
			PostInfoXmlGenerationRunnableHolder.clear();
			throw new IllegalArgumentException("EquipSprite folder could not be created");
		}
		ImageFileHolder.copyEquipSpritesToTargetDirectory(equipSpriteFolder);
	}
	
	private File getEquipSpriteFolderForActor(Actor actor, String folderName) {
		return new File(GameFileAccessor.getFileFromProperty(PropertyRepository.EQUIPSPRITES_FOLDER).getAbsolutePath() + "/" + folderName);
	}

	@Override
	public void enable() {
		readAlreadyExistingItems();
		super.enable();
		this.previewLabel.update();
		ApplicationFrameHolder.setApplicationFrameTitle("Equipment");
	}

	private JPanel createSelectionPanel() {
		JPanel panel = UIComponentFactory.createBorderedPanel(GAP_WIDTH, GAP_WIDTH, PANEL_WIDTH, PANEL_HEIGHT);
		this.categoryComboBox = createComboBox(panel, "Category:", SELECTION_OFFSET,
				"Select the fitting category for the item you want to add.",
				sortToArray(EquipTypeRepository.getCategories()));
		this.equipTypeComboBox = createComboBox(panel, "EquipType:", 20 + 2 * SELECTION_OFFSET,
				"Select the type of equipment you want to add.", sortToArray(EquipTypeRepository.getCategories()));

		this.mainSpriteLoader = createPictureLoader(panel, "Select a sprite for the item (128x192px and png-format)");
		this.extraSpriteLoader = createPictureLoader(panel, "Select an additional sprite (128x192px and png-format)");
		this.extraSpriteLoader.setText("<html><center>Load<br/>Extra<br/>Sprite</center></html>");
		this.mainSpriteLoader.setToolTipText("Load a sprite for the item.");
		this.extraSpriteLoader.setToolTipText(
				"Load an additional sprite for the item (usually optional). Talk to an experienced spriter before using this option!");

		this.categoryComboBox.addItemListener(e -> updateEquipTypes());
		this.equipTypeComboBox.addItemListener(e -> updateCurrentEquipType());
		updateEquipTypes();

		this.nameTextField = createTextField(panel, "Item Name:", 40 + 3 * SELECTION_OFFSET,
				"Add a name for the item. [Tip: Include the character's (first) name to make it unique.]");
		this.descriptionTextField = createTextField(panel, "Description:", 62 + 4 * SELECTION_OFFSET,
				"Add a short description of the item that will appear ingame. (Try not to make it generic.)");

		this.saveButton = UIComponentFactory.createButton(ADD_BUTTON_TEXT, ADD_BUTTON_OFFSET,
				PANEL_HEIGHT - ADD_BUTTON_HEIGHT - ADD_BUTTON_OFFSET, PANEL_WIDTH - 2 * ADD_BUTTON_OFFSET,
				ADD_BUTTON_HEIGHT);
		this.saveButton.addActionListener(e -> saveInput());
		panel.add(this.saveButton);

		return panel;
	}
	
	private void saveInput() {
		addItem();

		if(this.editingModeEnabled) {
			if(this.itemsToBeEdited.isEmpty()) {
				this.editingModeEnabled = false;
				this.saveButton.setText(ADD_BUTTON_TEXT);
			} else {
				loadNextItemToBeEdited();
			}
		}
	}

	private void addItem() {
		String verificationError = checkForVerificationError();
		if (verificationError != null) {
			JOptionPane.showMessageDialog(null, verificationError, "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		String equipType = (String) this.equipTypeComboBox.getSelectedItem();
		String name = this.nameTextField.getText();
		this.createdEquipment.put(name,
				new ItemDto(equipType, name,
						this.descriptionTextField.getText(), this.mainSpriteLoader.getSelectedPicture(),
						this.extraSpriteLoader.getSelectedPicture()));
		
		ImageFileHolder.putEquipSprite(equipType, this.mainSpriteLoader.getSelectedPicture());
		File extraSprite = this.extraSpriteLoader.getSelectedPicture();
		if(extraSprite != null) {
			ImageFileHolder.putEquipSprite(equipType + ImageFileHolder.EXTRA_LAYER_SUFFIX, extraSprite);
		}
		refresh();
		clearSelectionComponents();
	}

	private void clearSelectionComponents() {
		this.categoryComboBox.setSelectedIndex(0);
		this.nameTextField.setText("");
		this.descriptionTextField.setText("");
		this.mainSpriteLoader.clear();
		this.extraSpriteLoader.clear();
	}

	private String checkForVerificationError() {
		String name = this.nameTextField.getText();
		if (ValueFormatter.isEmpty(name)) {
			return "The name of an item can't be empty.";
		}
		if (ValueFormatter.isEmpty(this.descriptionTextField.getText())) {
			return "The description of an item can't be empty.";
		}
		if (this.mainSpriteLoader.getSelectedPicture() == null) {
			return "A (main) sprite needs to be selected.";
		}
		if (this.createdEquipment.containsKey(name)) {
			return "You already created an item with this name.";
		}
		if (this.alreadyExistingItems.contains(name)) {
			return "An item with this name already exists.";
		}

		for (String slot : getSlots(this.currentEquipType)) {
			String conflictingItem = searchItemWithSlot(slot);
			if (conflictingItem != null) {
				return "The item you are trying to create would use the slot " + slot + ", but the item "
						+ conflictingItem + " already occupies this slot.";
			}
		}

		return null;
	}

	private JComboBox<String> createComboBox(JComponent parent, String labelText, int yPos, String tooltip,
			String[] options) {
		parent.add(UIComponentFactory.createFormattedLabel(labelText, SELECTION_OFFSET, yPos,
				SELECTION_XPOS - 2 * SELECTION_OFFSET, 20, JLabel.RIGHT));
		JComboBox<String> comboBox = UIComponentFactory.createComboBox(SELECTION_XPOS, yPos, SELECTION_WIDTH, tooltip,
				options);
		parent.add(comboBox);
		return comboBox;
	}

	private JTextField createTextField(JComponent parent, String labelText, int yPos, String tooltip) {
		parent.add(UIComponentFactory.createFormattedLabel(labelText, SELECTION_OFFSET, yPos,
				SELECTION_XPOS - 2 * SELECTION_OFFSET, 22, JLabel.RIGHT));
		JTextField textField = UIComponentFactory.createFormattedTextField(SELECTION_XPOS, yPos, SELECTION_WIDTH,
				tooltip);
		parent.add(textField);
		return textField;
	}

	private PictureLoader createPictureLoader(JComponent parent, String dialogText) {
		PictureLoader picLoader = new PictureLoader(EXTRA_PICLOADER_XPOS, PICLOADER_YPOS, 128, 192,
				PICLOADER_HEIGHT / 192d, null, dialogText);
		parent.add(picLoader);
		return picLoader;
	}

	private void updateEquipTypes() {
		List<String> types = EquipTypeRepository
				.getEquipTypesFromCategory((String) this.categoryComboBox.getSelectedItem());
		this.equipTypeComboBox.setModel(new DefaultComboBoxModel<>(sortToArray(types)));
		updateCurrentEquipType();
	}

	private void updateCurrentEquipType() {
		this.currentEquipType = EquipTypeRepository.getEquipType((String) this.equipTypeComboBox.getSelectedItem());
		resetPictureLoaders();
	}

	private void resetPictureLoaders() {
		this.mainSpriteLoader.clear();
		this.extraSpriteLoader.clear();

		DrawIndices drawIndices = this.currentEquipType.getDrawIndices();
		boolean hasExtraIndex = drawIndices != null && drawIndices.getExtraIndex() != null;
		this.extraSpriteLoader.setVisible(hasExtraIndex);
		this.mainSpriteLoader.setLocation(hasExtraIndex ? MAIN_PICLOADER_EXTRA_XPOS : MAIN_PICLOADER_ONLY_XPOS,
				PICLOADER_YPOS);
		this.mainSpriteLoader.setText(hasExtraIndex ? "<html><center>Load<br/>Main<br/>Sprite</center></html>"
				: "<html><center>Load<br/>Sprite</center></html>");
	}

	private void refresh() {
		DefaultListModel<String> model = new DefaultListModel<>();
		this.createdEquipment.keySet().stream().sorted((a, b) -> a.compareTo(b)).forEach(n -> model.addElement(checkHidden(n)));
		this.createdEquipList.setModel(model);
		this.previewLabel.update();
	}
	
	private String checkHidden(String itemName) {
		return this.createdEquipment.get(itemName).isHidden() ? itemName + HIDDEN_SUFFIX : itemName; 
	}
	
	private JButton createControlButton(String text, int xPos, int width) {
		return UIComponentFactory.createButton(text, xPos, CONTROLBUTTON_YPOS, width, CONTROLBUTTON_HEIGHT);
	}

	private String[] sortToArray(Collection<String> collection) {
		return collection.stream().sorted((a, b) -> a.compareTo(b)).toArray(String[]::new);
	}

	private void readAlreadyExistingItems() {
		this.alreadyExistingItems = new ConfigReader(
				GameFileAccessor.getFileFromProperty(PropertyRepository.EQUIPMENT_LIST)).readAllValues();
	}

	private String searchItemWithSlot(String slot) {
		for (ItemDto item : this.createdEquipment.values()) {
			if (getSlots(EquipTypeRepository.getEquipType(item.getEquipType())).contains(slot)) {
				return item.getName();
			}
		}
		return null;
	}

	private List<String> getSlots(EquipTypeType equipType) {
		List<String> slotList = new ArrayList<>();
		addToCollectionIfNotNull(slotList, equipType.getSlot());
		Slots slots = equipType.getSlots();
		if (slots != null) {
			addToCollectionIfNotNull(slotList, slots.getMain());
			addToCollectionIfNotNull(slotList, slots.getExtra());
		}
		return slotList;
	}

	private void addToCollectionIfNotNull(Collection<String> collection, String item) {
		if (item != null) {
			collection.add(item);
		}
	}
	
	private Stream<String> getStreamOfSelectedItems() {
		return this.createdEquipList.getSelectedValuesList().stream().map(n -> n.replace(HIDDEN_SUFFIX, ""));
	}

	private void hideSelectedItems() {
		getStreamOfSelectedItems().forEach(e -> hideItem(e));
		refresh();
	}
	
	private void hideItem(String itemName) {
		ItemDto item = this.createdEquipment.get(itemName);
		boolean newHidden = !item.isHidden();
		item.setHidden(newHidden);
		this.previewLabel.hideEquipType(item.getEquipType(), newHidden);
	}
	
	private void removeSelectedItems() {
		getStreamOfSelectedItems().forEach(e -> removeItem(e));
		refresh();
	}
	
	private void removeItem(String itemName) {
		String equipType = this.createdEquipment.remove(itemName).getEquipType();
		ImageFileHolder.removeEquipSprite(equipType);
		ImageFileHolder.removeEquipSprite(equipType + ImageFileHolder.EXTRA_LAYER_SUFFIX);
		this.previewLabel.hideEquipType(equipType, false);
	}
	
	private void editSelectedItems() {
		getStreamOfSelectedItems().forEach(e -> this.itemsToBeEdited.add(this.createdEquipment.get(e)));
		if(this.itemsToBeEdited.isEmpty()) {
			return;
		}
		enableEditingMode();
		removeSelectedItems();
	}

	private void enableEditingMode() {
		if(!editingModeEnabled) {
			this.editingModeEnabled = true;
			loadNextItemToBeEdited();
			this.saveButton.setText("Save Item");
		}
	}
	
	private void loadNextItemToBeEdited() {
		ItemDto item = this.itemsToBeEdited.remove();
		this.categoryComboBox.setSelectedItem(EquipTypeRepository.getCategoryOfEquipType(item.getEquipType()));
		this.equipTypeComboBox.setSelectedItem(item.getEquipType());
		this.nameTextField.setText(item.getName());
		this.descriptionTextField.setText(item.getDescription());
		this.mainSpriteLoader.setSelectedPicture(item.getMainSprite());
		this.extraSpriteLoader.setSelectedPicture(item.getExtraSprite());
	}
	
	private void writeEquipmentToXml(Actor actor, Collection<ItemDto> items, String folderName) {
		if(items.isEmpty()) {
			return;
		}
		try {
			File targetXml = GameFileAccessor.getFileFromCharacterbuilderProperty(PropertyRepository.EQUIPMENT_TARGET_XML);
			File tempFile = new File(targetXml.getParentFile(), targetXml.getName() + ".tmp");

			try (BufferedReader reader = new BufferedReader(new FileReader(targetXml));
					PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tempFile)))) {
				String line;
				while ((line = reader.readLine()) != null) {
					if(line.contains("</EquipItems>")) {
						String bodyType = determineBodyType(actor);
						items.stream().forEach(i -> writeItemToWriter(writer, i, bodyType, folderName));
					}
					writer.println(line);
				}
			}
			targetXml.delete();
			tempFile.renameTo(targetXml);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void writeItemToWriter(PrintWriter writer, ItemDto item, String bodyType, String folderName) {
		writer.println();
		writer.println("\t<Equipment><!--" + item.getName() + "-->");
		writer.println("\t\t<Name>" + item.getName() + "</Name>");
		writer.println("\t\t<Description>" + item.getDescription() + "</Description>");
		writer.println("\t\t<Type>" + item.getEquipType() + "</Type>");
		writer.println("\t\t<Price>" + ConfigReaderRepository.getCharacterbuilderConfigReader().readInt(PropertyRepository.EQUIPMENT_PRICE) + "</Price>");
		writer.println("\t\t<Availability>Character Unique</Availability>");
		writer.println("\t\t<DynamicSprite>");
		writer.println("\t\t\t<BodyModel>");
		writer.println("\t\t\t\t<BodyType>" + bodyType + "</BodyType>");
		
		String spritePath = folderName + "/" + item.getEquipType();
		writer.println("\t\t\t\t<Bitmap>" + spritePath + "</Bitmap>");
		if(item.getExtraSprite() != null) {
			writer.println("\t\t\t\t<Bitmap>" + spritePath + ImageFileHolder.EXTRA_LAYER_SUFFIX + "</Bitmap>");
		}

		writer.println("\t\t\t</BodyModel>");
		writer.println("\t\t</DynamicSprite>");
		writer.println("\t</Equipment>");
	}
	
	private String determineBodyType(Actor actor) {
		switch(actor.getBody().getType()) {
		case FLAT_CHESTED:
		case SMALL_BREASTS:
			return SMALL_BREASTS;
		case MEDIUM_BREASTS:
			return MEDIUM_BREASTS;
		case LARGE_BREASTS:
		case XL_BREASTS:
			return LARGE_BREASTS;
		}
		throw new IllegalArgumentException("Invalid BodyType: " + actor.getBody().getType());
	}
	

	public boolean isEditingModeEnabled() {
		return editingModeEnabled;
	}
	
	private static class ItemDto {
		private final String equipType;
		private final String name;
		private final String description;
		private final File mainSprite;
		private final File extraSprite;
		
		private boolean hidden = false;

		ItemDto(String equipType, String name, String description, File mainSprite, File extraSprite) {
			this.equipType = equipType;
			this.name = name;
			this.description = description;
			this.mainSprite = mainSprite;
			this.extraSprite = extraSprite;
		}

		public String getEquipType() {
			return equipType;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public File getMainSprite() {
			return mainSprite;
		}

		public File getExtraSprite() {
			return extraSprite;
		}

		public boolean isHidden() {
			return hidden;
		}

		public void setHidden(boolean hidden) {
			this.hidden = hidden;
		}
	}

}
