package simplecharacterbuilder.characterbuilder.util.ui;

import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.BORDER;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

import simplecharacterbuilder.characterbuilder.util.ui.CheckBoxList.ItemPanelMouseListenerFactory;
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.CharacterBuilderMainComponent;

public class UIComponentFactory {
	private static final Border HIGHLIGHTING_BORDER = createHighlightingBorder();
	
	private UIComponentFactory() {};
	
	public static JLabel createFormattedLabel(String text, int xPos, int yPos, int width, int horizontalAlignment) {
		return createFormattedLabel(text, xPos, yPos, width, 22, horizontalAlignment);
	}
	
	public static JLabel createFormattedLabel(String text, int xPos, int yPos, int width, int height, int horizontalAlignment) {
		JLabel label = new JLabel(text);
		label.setBounds(xPos, yPos, width, height);
		label.setHorizontalAlignment(horizontalAlignment);
		label.setVerticalAlignment(JLabel.CENTER);
		label.setFocusable(false);
		return label;
	}

	public static JTextField createFormattedTextField(int xPos, int yPos, int width, String tooltip) {
		JTextField textField = new JTextField();
		textField.setBounds(xPos, yPos, width, 22);
		textField.setBorder(BORDER);		
		textField.setBackground(new Color(255, 255, 255, 245));
		textField.setToolTipText(tooltip);
		return textField;
	}
	
	public static JComboBox<String> createComboBox(int xPos, int yPos, int width, String tooltip, String[] options) {
		JComboBox<String> comboBox = new JComboBox<>(options);
		comboBox.setSelectedIndex(0);
		comboBox.setBounds(xPos, yPos, width, 20);
		comboBox.setToolTipText(tooltip);
		return comboBox;
	}
	
	public static JCheckBox createCheckBox(String name, int xPos, int yPos, int width, String tooltip) {
		JCheckBox checkBox = new JCheckBox(name);
		checkBox.setLocation(xPos, yPos);
		checkBox.setBounds(xPos, yPos, width, 20);
		checkBox.setToolTipText(tooltip);
		return checkBox;
	}
	
	public static JPanel createBorderedPanel(int xPos, int yPos, int width, int height) {
		JPanel panel = new JPanel();
		panel.setBounds(xPos, yPos, width, height);
		panel.setBorder(BORDER);
		panel.setLayout(null);
		return panel;
	}
	
	public static CheckBoxList createCheckBoxList(List<String> options, int xPos, int yPos, int width, int height, ItemPanelMouseListenerFactory listenerFactory) {
		return new CheckBoxList(options, xPos, yPos, width, height, listenerFactory);
	}
	
	public static JButton createButton(String text, int xPos, int yPos, int width, int height) {
		JButton button = new JButton(text);
		button.setBounds(xPos, yPos, width, height);
		button.setBorder(CharacterBuilderMainComponent.BORDER);
		button.setHorizontalAlignment(JLabel.CENTER);
		button.setVerticalAlignment(JLabel.CENTER);
		button.setFocusable(false);
		button.addMouseListener(new HighlightingMouseListener(button));
		return button;
	}
	
	public static ListComponentDto createList(int xPos, int yPos, int width, int height) {
		JPanel container = new JPanel();
		container.setBounds(xPos, yPos, width, height);
		container.setBorder(CharacterBuilderMainComponent.BORDER);
		container.setLayout(new GridLayout(1, 1));

		JList<String> list = new JList<>(new DefaultListModel<>());
		list.setBorder(null);
		list.setBackground(null);
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(12);
		container.add(scrollPane);

		return new ListComponentDto(container, list);
	}
	
	private static Border createHighlightingBorder() {
		Border outerBorder = BorderFactory.createLineBorder(new Color(122, 138, 153, 255));
		Border innerBorder = BorderFactory.createLineBorder(new Color(184, 207, 229, 255));
		Border compound = BorderFactory.createCompoundBorder(innerBorder, innerBorder);
		return BorderFactory.createCompoundBorder(outerBorder, compound);
	}

	public static class ListComponentDto {
		private final JPanel container;
		private final JList<String> list;
		
		private ListComponentDto(JPanel container, JList<String> list) {
			this.container = container;
			this.list = list;
		}
		
		public JPanel getContainer() {
			return this.container;
		}
		
		public JList<String> getList(){
			return this.list;
		}
	}
	
	public static class HighlightingMouseListener extends MouseAdapter{
		private final JComponent component;
		
		HighlightingMouseListener(JComponent component) {
			this.component = component;
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			if(this.component.isEnabled()) {
				this.component.setBorder(HIGHLIGHTING_BORDER);
			}
			super.mouseEntered(e);
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			this.component.setBorder(CharacterBuilderMainComponent.BORDER);
			super.mouseExited(e);
		}
	}
	
}
