package simplecharacterbuilder.characterbuilder.util.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JCheckBox;

@SuppressWarnings("serial")
public class CheckBoxList extends PanelList {
	private static final int ITEM_HEIGHT = 21;

	private final List<JCheckBox> checkBoxes = new ArrayList<>();

	public CheckBoxList(List<String> options, int xPos, int yPos, int width, int height,
			ItemPanelMouseListenerFactory listenerFactory) {
		super(xPos, yPos, width, height, ITEM_HEIGHT);
		List<ItemContainer> itemContainers = new ArrayList<>();
		options.stream().forEach(o -> itemContainers.add(createItemPanel(o, width, listenerFactory)));
		setItemContainers(itemContainers);
	}

	public List<String> getSelectedItems() {
		return checkBoxes.stream().filter(c -> c.isSelected()).map(c -> c.getText()).collect(Collectors.toList());
	}
	
	public void setDisabledItems(Collection<String> items) {
		checkBoxes.stream().forEach(c -> c.setEnabled(!items.contains(c.getText())));
	}
	
	public void select(Collection<String> items, boolean selected) {
		checkBoxes.stream().filter(c -> items.contains(c.getText())).forEach(c -> c.setSelected(selected));
	}
	
	private ItemContainer createItemPanel(String item, int width, ItemPanelMouseListenerFactory listenerFactory) {
		ItemContainer itemContainer = new ItemContainer();
		JCheckBox checkBox = new JCheckBox(item);
		checkBox.setBounds(3, (ITEM_HEIGHT - 20) / 2, width - 20, 20);
		checkBox.addMouseListener(listenerFactory.createListener(checkBox));
		checkBox.setFocusable(false);
		itemContainer.add(checkBox);
		this.checkBoxes.add(checkBox);
		return itemContainer;
	}

	public static interface ItemPanelMouseListenerFactory {
		MouseListener createListener(JCheckBox checkBox);

		public static abstract class AbstractMouseListener implements MouseListener {
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		}
	}

}
