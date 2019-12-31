package simplecharacterbuilder.statgenerator.xmlreaderwriter;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

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
	
	private final StatGenerator STAT_GENERATOR;
	private final String ACTORS_DIRECTORY;
	
	public StatGeneratorXmlReaderWriterView(int x, int y, StatGenerator statGenerator, String configPath) {
		super(x, y);
		this.STAT_GENERATOR = statGenerator;
		this.ACTORS_DIRECTORY = determineActorsDirectory(configPath);
		
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
			 chooser.setCurrentDirectory(new File(ACTORS_DIRECTORY));
		
			if (chooser.showOpenDialog(mainPanel.getParent()) == JFileChooser.APPROVE_OPTION) {
				String filePath = chooser.getSelectedFile().getCanonicalPath().replace("\\", "/");
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
			STAT_GENERATOR.setStats(createStatXmlReaderWriter().readStatsFromXml());
		} catch(Exception e) {
			displayErrorSelectInfoXml();
		}
	}
	
	private void saveToXml() {
		if(!STAT_GENERATOR.confirmIntentIfWarningsExist()) {
			return;
		}
		try {
			createStatXmlReaderWriter().updateXmlFromStats(STAT_GENERATOR.getStats());
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
	
	private String determineActorsDirectory(String configPath) {
		Properties prop   = new Properties();
		try (InputStream inputStream = new FileInputStream(configPath)) {
			prop.load(inputStream);
			return prop.getProperty("chooser_default_directory").trim();
		} catch (Exception e) {
			throw new IllegalArgumentException("Error loading config");
		}
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
