package simplecharacterbuilder.characterbuilder.util.ui;

import java.awt.Color;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent;

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
	
}
