package simplecharacterbuilder.characterbuilder.maincomponents.various;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import simplecharacterbuilder.characterbuilder.util.holder.EquipTypeRepository;
import simplecharacterbuilder.characterbuilder.util.transform.ValueFormatter;
import simplecharacterbuilder.characterbuilder.util.ui.PictureLoader;
import simplecharacterbuilder.characterbuilder.util.ui.PreviewLabel;
import simplecharacterbuilder.characterbuilder.util.ui.UIComponentFactory;
import simplecharacterbuilder.characterbuilder.util.ui.UIComponentFactory.ListComponentDto;
import simplecharacterbuilder.common.generated.Actor;
import simplecharacterbuilder.common.generated.EquipTypeType;
import simplecharacterbuilder.common.generated.EquipTypeType.DrawIndices;
import simplecharacterbuilder.common.resourceaccess.ConfigReader;
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

	private JComboBox<String> categoryComboBox;
	private JComboBox<String> equipTypeComboBox;
	private JTextField nameTextField;
	private JTextField descriptionTextField;
	private PictureLoader mainSpriteLoader;
	private PictureLoader extraSpriteLoader;
	private PreviewLabel previewLabel;
	
	private List<String> alreadyExistingItems;

	private final Map<String, ItemDto> createdEquipment = new HashMap<>();
	private final JList<String> createdEquipList;

	private EquipTypeType currentEquipType;

	public EquipmentCreatorMainComponent() {
		this.mainPanel.add(createSelectionPanel());

		ListComponentDto listDto = UIComponentFactory.createList(GAP_WIDTH, PANEL_HEIGHT + GAP_WIDTH - 1, PANEL_WIDTH,
				LIST_HEIGHT);
		this.createdEquipList = listDto.getList();
		this.mainPanel.add(listDto.getContainer());

		JButton hideButton = createControlButton("Hide", GAP_WIDTH, CONTROLBUTTON_WIDTH);
		this.mainPanel.add(hideButton);
		JButton editButton = createControlButton("Edit", GAP_WIDTH + CONTROLBUTTON_WIDTH - 1, CONTROLBUTTON_WIDTH + 2);
		this.mainPanel.add(editButton);
		JButton deleteButton = createControlButton("Delete", GAP_WIDTH + 2 * CONTROLBUTTON_WIDTH,
				PANEL_WIDTH - 2 * CONTROLBUTTON_WIDTH);
		this.mainPanel.add(deleteButton);

		this.previewLabel = new PreviewLabel(CharacterBuilderControlPanel.X_POS + (ControlPanel.WIDTH_BASIC - 128) / 2,
				30);
		this.mainPanel.add(this.previewLabel);
	}

	@Override
	public void setValues(Actor actor) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void enable() {
		readAlreadyExistingItems();
		super.enable();
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
		this.extraSpriteLoader.setToolTipText("Load an additional sprite for the item (usually optional). Talk to an experienced spriter before using this option!");

		this.categoryComboBox.addItemListener(e -> updateEquipTypes());
		this.equipTypeComboBox.addItemListener(e -> updateCurrentEquipType());
		updateEquipTypes();
		updateCurrentEquipType();

		this.nameTextField = createTextField(panel, "Item Name:", 40 + 3 * SELECTION_OFFSET, "Add a name for the item. [Tip: Include the character's (first) name to make it unique.]");
		this.descriptionTextField = createTextField(panel, "Description:", 62 + 4 * SELECTION_OFFSET, "Add a short description of the item that will appear ingame. (Try not to make it generic.)");

		JButton addButton = UIComponentFactory.createButton("Add item", ADD_BUTTON_OFFSET,
				PANEL_HEIGHT - ADD_BUTTON_HEIGHT - ADD_BUTTON_OFFSET, PANEL_WIDTH - 2 * ADD_BUTTON_OFFSET,
				ADD_BUTTON_HEIGHT);
		addButton.addActionListener(e -> addItem());
		panel.add(addButton);

		return panel;
	}

	private void addItem() {
		if (!verifySelections()) {
			return;
		}
		String name = this.nameTextField.getText();
		this.createdEquipment.put(name,
				new ItemDto((String) this.equipTypeComboBox.getSelectedItem(), name,
						this.descriptionTextField.getText(), this.mainSpriteLoader.getSelectedPicture(),
						this.extraSpriteLoader.getSelectedPicture()));
		refreshCreatedItemList();
	}

	private boolean verifySelections() {
		String name = this.nameTextField.getText();
		if (ValueFormatter.isEmpty(name)) {
			JOptionPane.showMessageDialog(null, "The name of an item can't be empty.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (ValueFormatter.isEmpty(this.descriptionTextField.getText())) {
			JOptionPane.showMessageDialog(null, "The description of an item can't be empty.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (this.mainSpriteLoader.getSelectedPicture() == null) {
			JOptionPane.showMessageDialog(null, "A (main) sprite needs to be selected.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (this.createdEquipment.containsKey(name)) {
			JOptionPane.showMessageDialog(null, "You already created an item with this name.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (this.alreadyExistingItems.contains(name)) {
			JOptionPane.showMessageDialog(null, "An item with this name already exists.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
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

	private void refreshCreatedItemList() {
		DefaultListModel<String> model = new DefaultListModel<>();
		this.createdEquipment.keySet().stream().sorted((a, b) -> a.compareTo(b)).forEach(n -> model.addElement(n));
		this.createdEquipList.setModel(model);
	}

	private JButton createControlButton(String text, int xPos, int width) {
		return UIComponentFactory.createButton(text, xPos, CONTROLBUTTON_YPOS, width, CONTROLBUTTON_HEIGHT);
	}

	private String[] sortToArray(Collection<String> collection) {
		return collection.stream().sorted((a, b) -> a.compareTo(b)).toArray(String[]::new);
	}
	
	private void readAlreadyExistingItems() {
		this.alreadyExistingItems = new ConfigReader(GameFileAccessor.getFileFromProperty(PropertyRepository.EQUIPMENT_LIST)).readAllValues();
	}

	private static class ItemDto {
		private final String equipType;
		private final String name;
		private final String description;
		private final File mainSprite;
		private final File extraSprite;

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
	}

}
