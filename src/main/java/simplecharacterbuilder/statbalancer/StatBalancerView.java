package simplecharacterbuilder.statbalancer;

import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import simplecharacterbuilder.abstractview.CharacterBuilderComponent.CharacterBuilderControlComponent;
import simplecharacterbuilder.statgenerator.Stat;
import simplecharacterbuilder.statgenerator.StatGenerator;

public class StatBalancerView  extends CharacterBuilderControlComponent {
	
	private static final int WIDTH = CONTROLPANEL_WIDTH + StatGenerator.COMPARISON_WIDTH;
	
	private final StatGenerator statGenerator;
	private final String ACTORS_DIRECTORY;
	
	private final JLabel portrait;
//	private final JLabel nameLabel;
//	private final JLabel franchiseLabel;
	
	private String selectedInfoXmlURI;
	
	public StatBalancerView(int x, int y, StatGenerator statGenerator, String configPath) {
		super(x, y, true);
		this.statGenerator = statGenerator;
		this.ACTORS_DIRECTORY = determineActorsDirectory(configPath);
		
		mainPanel.setBorder(BORDER);
		
		mainPanel.add(new ControlButton("Load", 0, (e -> load())));
		mainPanel.add(new ControlButton("Save", ControlButton.WIDTH, (e -> saveToXml())));
		
		portrait = new JLabel();
		portrait.setBounds(WIDTH - CONTROLPANEL_HEIGHT, 0, CONTROLPANEL_HEIGHT, CONTROLPANEL_HEIGHT);
		portrait.setBorder(BORDER);
		portrait.setText("<html><center>Portrait not found</center></html>");
		mainPanel.add(portrait);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		 load();
	}

	private void selectPath() {
		Boolean old = UIManager.getBoolean("FileChooser.readOnly");  
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);  
		JFileChooser chooser = new JFileChooser();
		UIManager.put("FileChooser.readOnly", old);  
		
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
					this.selectedInfoXmlURI = filePath;
				} else {
					displayErrorSelectInfoXml();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setPortrait(String portraitURI) {
			Image image = new ImageIcon(portraitURI).getImage(); 
			portrait.setIcon(new ImageIcon(image.getScaledInstance(portrait.getWidth(), portrait.getHeight(),  Image.SCALE_SMOOTH)));
	}

	private void load() {
		selectPath();
		try {
			Map<Stat, Integer> stats = createStatXmlReaderWriter().readStatsFromXml();
			statGenerator.setComparisonValues(stats);
			statGenerator.setStats(stats);
		} catch(Exception e) {
			displayErrorSelectInfoXml();
		}
		setPortrait(selectedInfoXmlURI.replace("Info.xml", "Portrait.png"));
	}
	
	private void saveToXml() {
		if(!statGenerator.confirmIntentIfWarningsExist()) {
			return;
		}
		try {
			createStatXmlReaderWriter().updateXmlFromStats(statGenerator.getStats());
		} catch(Exception e) {
			displayErrorSelectInfoXml();
		}
	}
	
	private StatXmlReaderWriter createStatXmlReaderWriter() {
		return new StatXmlReaderWriter(selectedInfoXmlURI);
	}
	
	private void displayErrorSelectInfoXml() {
		JOptionPane.showMessageDialog(mainPanel.getParent(), "Load a valid Info.xml", "Error", JOptionPane.ERROR_MESSAGE);
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
	private static class ControlButton extends JButton {
		static final int WIDTH  = (StatBalancerView.WIDTH - CONTROLPANEL_HEIGHT) / 2;
		static final int HEIGHT = 35;
		
		ControlButton(String buttonText, int xPos, ActionListener actionListener){
			super(buttonText);
			this.setBounds(xPos, CONTROLPANEL_HEIGHT - HEIGHT, WIDTH, HEIGHT);
			this.setHorizontalAlignment(CENTER);
			this.addActionListener(actionListener);
		}
	}
}
