package simplecharacterbuilder.characterbuilder.social;

import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.CONTROLPANEL_HEIGHT;
import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.GAP_WIDTH;
import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.MAINPANEL_HEIGHT;
import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.MAINPANEL_WIDTH;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import simplecharacterbuilder.characterbuilder.util.ui.PanelList;

@SuppressWarnings("serial")
public class SocialSelectionPanel extends PanelList {
	private static final int WIDTH = MAINPANEL_WIDTH - 2 * GAP_WIDTH;
	private static final int HEIGHT = MAINPANEL_HEIGHT - CONTROLPANEL_HEIGHT - 3 * GAP_WIDTH;
	private static final int ITEM_HEIGHT = 100;
	
	public SocialSelectionPanel(int xPos, int yPos) {
		super(xPos, yPos, WIDTH, HEIGHT, ITEM_HEIGHT);
		List<ItemContainer> itemContainers = new ArrayList<>();
		itemContainers.add(createCharacterContainer());
		itemContainers.add(createCharacterContainer());
		itemContainers.add(createCharacterContainer());
		itemContainers.add(createCharacterContainer());
		itemContainers.add(createCharacterContainer());
		itemContainers.add(createCharacterContainer());
		itemContainers.add(createCharacterContainer());
		itemContainers.add(createCharacterContainer());
		itemContainers.add(createCharacterContainer());
		itemContainers.add(createCharacterContainer());
		itemContainers.add(createCharacterContainer());
		itemContainers.add(createCharacterContainer());
		setItemContainers(itemContainers);
	}
	
	int counter = 0;
	private ItemContainer createCharacterContainer() {
		ItemContainer itemContainer = new ItemContainer();
		itemContainer.setBackground(counter++%2==0?Color.RED:Color.GREEN);
		return itemContainer;
	}
	
}
