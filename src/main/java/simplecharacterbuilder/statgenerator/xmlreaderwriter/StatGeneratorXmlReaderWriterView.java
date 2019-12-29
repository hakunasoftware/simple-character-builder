package simplecharacterbuilder.statgenerator.xmlreaderwriter;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import simplecharacterbuilder.abstractview.CharacterBuilderComponent.CharacterBuilderControlComponent;
import simplecharacterbuilder.statgenerator.StatGenerator;

public class StatGeneratorXmlReaderWriterView  extends CharacterBuilderControlComponent {
	
	private final JPanel innerPanel;
	private final JTextField textField;
	
	private final StatGenerator statGenerator;
	
	private static final String PARENT_DIR = ".";
	private static final String PARENT_DIR_ABS;
	static {
		try {
			PARENT_DIR_ABS = new File(PARENT_DIR).getCanonicalPath();
		} catch (IOException e) {
			throw new IllegalArgumentException(); 
		}
	}
	
	public StatGeneratorXmlReaderWriterView(int x, int y, StatGenerator statGenerator) {
		super(x, y);
		
		this.statGenerator = statGenerator;
		
		innerPanel = new JPanel();
		innerPanel.setLayout(null);
		innerPanel.setBorder(BORDER);
		innerPanel.setBounds(GAP_WIDTH, 0, CONTROLPANEL_WIDTH - 2 * GAP_WIDTH, CONTROLPANEL_HEIGHT - GAP_WIDTH);
		mainPanel.add(innerPanel);
		
		this.textField = new JTextField();
		formatPathTextField();
		
		String selectXml = "<html>Select<br>Info.xml</html>";
		innerPanel.add(new ControlButton(selectXml, 285, (e -> selectPath())));
		innerPanel.add(new ControlButton("Load", 365, (e -> loadFromXml())));
		innerPanel.add(new ControlButton("Save", 445, (e -> saveToXml())));
	}

	private void selectPath() {
		JFileChooser chooser = new JFileChooser();
		try{
			 chooser.setCurrentDirectory(new File(PARENT_DIR));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (chooser.showOpenDialog(mainPanel.getParent()) == JFileChooser.APPROVE_OPTION) {
			String relativePath = determineRelativePath(chooser.getSelectedFile());
			if(relativePath.endsWith("Info.xml")) {
				this.textField.setText(relativePath);
			} else {
				displayErrorSelectInfoXml();
			}
		}
	}

	private String determineRelativePath(File file) {
		try {
			String path = file.getCanonicalPath();
			if(path.startsWith(PARENT_DIR_ABS + "\\")) {
				path = path.replace(PARENT_DIR_ABS + "\\", "");
				path = path.replace("\\", "/");
				return path;
			}
			throw new IllegalArgumentException(path);
		} catch (IOException e) {
			throw new IllegalArgumentException(file.getAbsolutePath());
		}
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
		try {
			statGenerator.setStats(createStatXmlReaderWriter().readDTOFromXml());
		} catch(Exception e) {
			displayErrorSelectInfoXml();
		}
	}
	
	private void saveToXml() {
		if(!statGenerator.confirmIntentIfWarningsExist()) {
			return;
		}
		try {
			createStatXmlReaderWriter().updateXmlFromDTO(statGenerator.getStats());
		} catch(Exception e) {
			displayErrorSelectInfoXml();
		}
	}
	
	private StatXmlReaderWriter createStatXmlReaderWriter() {
		return new StatXmlReaderWriter(textField.getText());
	}
	
	private void displayErrorSelectInfoXml() {
		JOptionPane.showMessageDialog(mainPanel.getParent(), "Select a valid Info.xml", "Error", JOptionPane.ERROR_MESSAGE);
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
