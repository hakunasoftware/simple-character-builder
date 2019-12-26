package simplecharacterbuilder.statgenerator.xmlreaderwriter;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import simplecharacterbuilder.abstractview.CharacterBuilderComponent.CharacterBuilderControlComponent;
import simplecharacterbuilder.statgenerator.StatGenerator;

public class StatGeneratorXmlReaderWriterView  extends CharacterBuilderControlComponent {
	
	private final JPanel innerPanel;
	private final JTextField textField;
	
	private final StatGenerator statGenerator;
	
	public StatGeneratorXmlReaderWriterView(int x, int y, StatGenerator statGenerator) {
		super(x, y);
		
		this.statGenerator = statGenerator;
		
		innerPanel = new JPanel();
		innerPanel.setLayout(null);
		innerPanel.setBorder(BORDER);
		innerPanel.setBounds(GAP_WIDTH, 0, CONTROLPANEL_WIDTH - 2 * GAP_WIDTH, CONTROLPANEL_HEIGHT - GAP_WIDTH);
		mainPanel.add(innerPanel);
		
		this.textField = new JTextField("src/main/resources/Info.xml");
		formatPathTextField();
		
		String selectXml = "<html>Select<br>Info.xml</html>";
		innerPanel.add(new ControlButton(selectXml, 285, (e -> System.out.println(1))));
		innerPanel.add(new ControlButton("Load", 365, (e -> loadFromXml())));
		innerPanel.add(new ControlButton("Save", 445, (e -> saveToXml())));
	}

	private void formatPathTextField() {
		int offset = (innerPanel.getHeight() - 25) / 2;
		textField.setBounds(10, offset, 265, 25);
		textField.setHorizontalAlignment(JLabel.LEFT);
		textField.setFont(new Font(innerPanel.getFont().getName(), innerPanel.getFont().getStyle(), 13));
		textField.setForeground(Color.BLACK);
		textField.setBorder(BORDER);
		innerPanel.add(textField);
	}

	private void loadFromXml() {
		statGenerator.setStats(createStatXmlReaderWriter().readDTOFromXml());
	}
	
	private void saveToXml() {
		createStatXmlReaderWriter().updateXmlFromDTO(statGenerator.getStats());
	}
	
	private StatXmlReaderWriter createStatXmlReaderWriter() {
		return new StatXmlReaderWriter(textField.getText());
	}

	@SuppressWarnings("serial")
	private class ControlButton extends JButton {
		ControlButton(String buttonText, int xPos, ActionListener actionListener){
			super(buttonText);
			this.setBounds(xPos, 10, 70, 40);
			this.setHorizontalAlignment(CENTER);
			this.addActionListener(actionListener);
		}
	}
	
}
