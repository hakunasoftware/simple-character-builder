package simplecharacterbuilder.characterbuilder.util.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.CharacterBuilderMainComponent;

@SuppressWarnings("serial")
public abstract class PanelList extends JPanel {
	private final int width;
	private final int itemHeight;

	private final JPanel mainContainer = new JPanel();

	private int counter = 0;

	public PanelList(int xPos, int yPos, int width, int height, int itemHeight) {
		this.width = width;
		this.itemHeight = itemHeight;

		this.setBounds(xPos, yPos, width, height);
		this.setBorder(CharacterBuilderMainComponent.BORDER);
		this.setLayout(new GridLayout(1, 1));

		this.mainContainer.setLayout(null);
		JScrollPane scrollPane = new JScrollPane(this.mainContainer);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(12);
		this.add(scrollPane);
	}

	public synchronized void setItemContainers(List<ItemContainer> itemContainers) {
		this.counter = 0;
		this.mainContainer.removeAll();
		this.mainContainer.setPreferredSize(new Dimension(width - 20, itemHeight * itemContainers.size()));
		itemContainers.stream().forEach(c -> this.mainContainer.add(c));
		this.mainContainer.revalidate();
	}

	protected class ItemContainer extends JPanel {
		public ItemContainer() {
			this.setBounds(0, counter++ * itemHeight, width, itemHeight);
			this.setLayout(null);
		}
	}
}
