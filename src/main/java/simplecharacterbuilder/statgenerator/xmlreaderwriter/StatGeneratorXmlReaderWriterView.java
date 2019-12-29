package simplecharacterbuilder.statgenerator.xmlreaderwriter;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import simplecharacterbuilder.abstractview.CharacterBuilderComponent.CharacterBuilderControlComponent;
import simplecharacterbuilder.statgenerator.StatGenerator;

public class StatGeneratorXmlReaderWriterView  extends CharacterBuilderControlComponent {
	
	private final JPanel innerPanel;
	private final JTextField textField;
	
	private final StatGenerator statGenerator;
	
	private static final String ACTORS_DIR = "../Data/Battlers/Actors";
	
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
		chooser.setDialogTitle("Select Info.xml");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(new FileFilter(){
		    @Override
		    public boolean accept(File file){
		        return file.isDirectory() || file.getName().equals("Info.xml");
		    }
		    @Override
		    public String getDescription() {
		        return "Folders and Info.xml";
		    }
		});
		
		try{
			 chooser.setCurrentDirectory(new File(ACTORS_DIR));
		
			if (chooser.showOpenDialog(mainPanel.getParent()) == JFileChooser.APPROVE_OPTION) {
				String filePath = chooser.getSelectedFile().getCanonicalPath().replace("\\", "\\\\");
				if(filePath.endsWith("Info.xml")) {
					this.textField.setText(filePath);
				} else {
					displayErrorSelectInfoXml();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
