package simplecharacterbuilder.characterbuilder.util.ui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent;
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.CharacterBuilderMainComponent;

public class UIComponentCreator {
	private UIComponentCreator() {};
	
	public static JLabel createFormattedLabel(String text, int xPos, int yPos, int width) {
		JLabel label = new JLabel(text);
		label.setBounds(xPos, yPos, width, 22);
		label.setHorizontalAlignment(JLabel.RIGHT);
		label.setVerticalAlignment(JLabel.CENTER);
		return label;
	}

	public static JTextField createFormattedTextField(int xPos, int yPos, int width, String tooltip) {
		JTextField textField = new JTextField();
		textField.setBounds(xPos, yPos, width, 22);
		textField.setBorder(CharacterBuilderComponent.BORDER);
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
		container.add(scrollPane);

		return new ListComponentDto(container, list);
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
	
}
