package simplecharacterbuilder.characterbuilder.core;

import static simplecharacterbuilder.util.CharacterBuilderComponent.MAINPANEL_HEIGHT;
import static simplecharacterbuilder.util.CharacterBuilderComponent.MAINPANEL_WIDTH;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;

import simplecharacterbuilder.characterbuilder.personaldata.PersonalDataMainComponent;
import simplecharacterbuilder.characterbuilder.util.JFileChooserPool;
import simplecharacterbuilder.statgenerator.StatGenerator;
import simplecharacterbuilder.util.ApplicationFrame;
import simplecharacterbuilder.util.CharacterBuilderComponent;
import simplecharacterbuilder.util.GameFileRepository;

public class CharacterBuilderStart {
	private static final int WIDTH = MAINPANEL_WIDTH;
	private static final int HEIGHT = MAINPANEL_HEIGHT;

	private static final String CONFIG_PATH = "config/";
	
	private static final String STATGENERATOR_CONFIG = CONFIG_PATH  + "statgenerator.config";
	private static final String PATH_VARIABLES_CONFIG = CONFIG_PATH  + "path_variables.config";

	private static final List<CharacterBuilderComponent> COMPONENTS = new ArrayList<>();
	static {
		GameFileRepository.init(PATH_VARIABLES_CONFIG);

		COMPONENTS.add(new PersonalDataMainComponent(0, 0));

		StatGenerator statGenerator = StatGenerator.createInstance(0, 0, STATGENERATOR_CONFIG, false);
		statGenerator.disable();
		COMPONENTS.add(statGenerator);

		CharacterBuilderControlPanel controlPanel = CharacterBuilderControlPanel.getInstance();
		controlPanel.init(COMPONENTS);
		COMPONENTS.add(controlPanel);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		JFileChooserPool.init();
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> new ApplicationFrame(WIDTH, HEIGHT, "SB2R CharacterBuilder", COMPONENTS));
	}
}