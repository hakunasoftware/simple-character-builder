package simplecharacterbuilder.characterbuilder.maincomponents.various;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;

import simplecharacterbuilder.characterbuilder.core.CharacterBuilderControlPanel;
import simplecharacterbuilder.characterbuilder.util.ui.PreviewLabel;
import simplecharacterbuilder.characterbuilder.util.ui.UIComponentFactory;
import simplecharacterbuilder.characterbuilder.util.ui.UIComponentFactory.ListComponentDto;
import simplecharacterbuilder.common.generated.Actor;
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

	private final JList<String> createdEquipList;

	private final PreviewLabel previewLabel;

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

	private JPanel createSelectionPanel() {
		JPanel panel = UIComponentFactory.createBorderedPanel(GAP_WIDTH, GAP_WIDTH, PANEL_WIDTH, PANEL_HEIGHT);

		JButton addButton = UIComponentFactory.createButton("Add item", ADD_BUTTON_OFFSET,
				PANEL_HEIGHT - ADD_BUTTON_HEIGHT - ADD_BUTTON_OFFSET, PANEL_WIDTH - 2 * ADD_BUTTON_OFFSET,
				ADD_BUTTON_HEIGHT);
		panel.add(addButton);
		return panel;
	}

	private JButton createControlButton(String text, int xPos, int width) {
		return UIComponentFactory.createButton(text, xPos, CONTROLBUTTON_YPOS, width, CONTROLBUTTON_HEIGHT);
	}

}
