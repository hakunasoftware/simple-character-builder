package simplecharacterbuilder.statbalancer;

import java.io.File;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import simplecharacterbuilder.common.resourceaccess.ConfigReaderRepository;
import simplecharacterbuilder.common.resourceaccess.GameFileAccessor;
import simplecharacterbuilder.common.resourceaccess.InfoXmlReaderWriter;
import simplecharacterbuilder.common.resourceaccess.PropertyRepository;
import simplecharacterbuilder.common.statgenerator.Stat;
import simplecharacterbuilder.common.statgenerator.StatGenerator;
import simplecharacterbuilder.common.uicomponents.ControlPanel;

public class StatBalancerView extends ControlPanel {

	private final StatGenerator statGenerator;
	private final File loadingStartDirectory;

	private String selectedInfoXmlURI;

	private boolean firstLoad = true;

	public StatBalancerView(int x, int y, StatGenerator statGenerator) {
		super(x, y, true, "Load", "Save", "<html><center>Portrait not found</center></html>");
		this.statGenerator = statGenerator;
		this.loadingStartDirectory = determineLoadingStartDirectory();

		this.button1.addActionListener(e -> loadXml());
		this.button2.addActionListener(e -> saveToXml());
	}

	private void selectPath() {
		Boolean old = UIManager.getBoolean("FileChooser.readOnly");
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		JFileChooser chooser = new JFileChooser();
		UIManager.put("FileChooser.readOnly", old);

		chooser.setDialogTitle("Select Info.xml");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory() || file.getName().equals("Info.xml");
			}

			@Override
			public String getDescription() {
				return "Folders and Info.xml";
			}
		});

		try {
			chooser.setCurrentDirectory(this.loadingStartDirectory);

			if (chooser.showOpenDialog(mainPanel.getParent()) == JFileChooser.APPROVE_OPTION) {
				firstLoad = false;
				String filePath = chooser.getSelectedFile().getCanonicalPath().replace("\\", "/");
				if (filePath.endsWith("Info.xml")) {
					this.selectedInfoXmlURI = filePath;
				} else {
					displayErrorSelectInfoXml();
				}
			} else {
				if (firstLoad) {
					System.exit(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadXml() {
		selectPath();
		loadFromPath();
		setPortrait(selectedInfoXmlURI.replace("Info.xml", "Portrait.png"));
		loadNameAndFranchise();
	}

	private void loadNameAndFranchise() {
		InfoXmlReaderWriter reader = new InfoXmlReaderWriter(selectedInfoXmlURI);
		String firstName = reader.readStringFromUniqueTagPath("Name/First");
		String lastName;
		try {
			lastName = " " + reader.readStringFromUniqueTagPath("Name/Last");
		} catch (Exception e) {
			lastName = "";
		}
		setName(firstName + lastName);
		setFranchise(reader.readStringFromUniqueTagPath("Source/Franchise"));
	}

	private void loadFromPath() {
		try {
			Map<Stat, Integer> stats = createStatXmlReaderWriter().readStatsFromXml();
			statGenerator.setComparisonValues(stats);
			statGenerator.setStatSuggestions(stats);
		} catch (Exception e) {
			displayErrorSelectInfoXml();
		}
	}

	private void saveToXml() {
		if (!statGenerator.confirmIntentIfWarningsExist()) {
			return;
		}
		try {
			createStatXmlReaderWriter().updateXmlFromStats(statGenerator.getStats());
			loadFromPath();
		} catch (Exception e) {
			displayErrorSelectInfoXml();
		}
	}

	private StatXmlReaderWriter createStatXmlReaderWriter() {
		return new StatXmlReaderWriter(selectedInfoXmlURI);
	}

	private void displayErrorSelectInfoXml() {
		JOptionPane.showMessageDialog(mainPanel.getParent(), "Load a valid Info.xml", "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	private File determineLoadingStartDirectory() {
		String directory = ConfigReaderRepository.getCharacterbuilderConfigReader()
				.readString(PropertyRepository.BALANCER_CHOOSER_DEFAULT);
		return directory.toLowerCase().equals("default")
				? GameFileAccessor.getFileFromProperty(PropertyRepository.ACTORS_FOLDER)
				: new File(directory);
	}

}
