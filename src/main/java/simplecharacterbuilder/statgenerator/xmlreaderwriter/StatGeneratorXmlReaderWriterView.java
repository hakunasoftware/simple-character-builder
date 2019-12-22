package simplecharacterbuilder.statgenerator.xmlreaderwriter;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import simplecharacterbuilder.abstractview.CharacterBuilderComponent;
import simplecharacterbuilder.abstractview.CharacterBuilderComponent.CharacterBuilderControlComponent;
import simplecharacterbuilder.statgenerator.StatGenerator;

public class StatGeneratorXmlReaderWriterView  extends CharacterBuilderControlComponent {
	
	private final JPanel innerPanel;
	
	public StatGeneratorXmlReaderWriterView(int x, int y, StatGenerator statGenerator) {
		super(x, y);
		
		innerPanel = new JPanel();
		innerPanel.setLayout(null);
		innerPanel.setBorder(BORDER);
		innerPanel.setBounds(GAP_WIDTH, 0, CONTROLPANEL_WIDTH - 2 * GAP_WIDTH, CONTROLPANEL_HEIGHT - GAP_WIDTH);
		mainPanel.add(innerPanel);
		
		addPathTextField();
		addLoadButton();
		addSaveButton();
	}

	private void addPathTextField() {
		int offset = (innerPanel.getHeight() - 25) / 2;
		JTextField textField = new JTextField();
		textField.setBounds(offset, offset, 270, 25);
		textField.setHorizontalAlignment(JLabel.LEFT);
		textField.setFont(new Font(innerPanel.getFont().getName(), innerPanel.getFont().getStyle(), 13));
		textField.setForeground(Color.BLACK);
		textField.setBorder(BORDER);
		innerPanel.add(textField);
	}

	private void addLoadButton() {
		// TODO Auto-generated method stub
		
	}

	private void addSaveButton() {
		// TODO Auto-generated method stub
		
	}
	
}
