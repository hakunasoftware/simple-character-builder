package simplecharacterbuilder.characterbuilder.util.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.CharacterBuilderMainComponent;

@SuppressWarnings("serial")
public class CheckBoxList extends JPanel {
	private static final int ITEM_HEIGHT = 21;

	private final List<JCheckBox> checkBoxes = new ArrayList<>();

	private int itemCounter = 0;

	public CheckBoxList(List<String> options, int xPos, int yPos, int width, int height,
			ItemPanelMouseListenerFactory listenerFactory) {
		this.setBounds(xPos, yPos, width, height);
		this.setBorder(CharacterBuilderMainComponent.BORDER);
		this.setLayout(new GridLayout(1, 1));

		JPanel container = new JPanel();
		container.setLayout(null);
		container.setPreferredSize(new Dimension(width - 20, ITEM_HEIGHT * options.size() + 4));
		options.stream().forEach(o -> container.add(createItemPanel(o, width, listenerFactory)));

		JScrollPane scrollPane = new JScrollPane(container);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(12);
		this.add(scrollPane);
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
	
	private JPanel createItemPanel(String item, int width, ItemPanelMouseListenerFactory listenerFactory) {
		JPanel itemContainer = new JPanel();
		itemContainer.setBounds(0, itemCounter++ * ITEM_HEIGHT + 2, width - 20, ITEM_HEIGHT);
		itemContainer.setLayout(null);

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
