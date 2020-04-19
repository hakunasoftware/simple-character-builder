package simplecharacterbuilder.characterbuilder.core;

import static simplecharacterbuilder.util.CharacterBuilderComponent.MAINPANEL_HEIGHT;
import static simplecharacterbuilder.util.CharacterBuilderComponent.MAINPANEL_WIDTH;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;

import simplecharacterbuilder.characterbuilder.util.JFileChooserPool;
import simplecharacterbuilder.personaldata.PersonalDataMainComponent;
import simplecharacterbuilder.statgenerator.StatGenerator;
import simplecharacterbuilder.util.ApplicationFrame;
import simplecharacterbuilder.util.CharacterBuilderComponent;

public class CharacterBuilderStart {
	private static final int WIDTH = MAINPANEL_WIDTH;
	private static final int HEIGHT = MAINPANEL_HEIGHT;

	@SuppressWarnings("unused")
	private static final String CONFIG_PATH_USE = "config/statgenerator.config";
	private static final String CONFIG_PATH_DEV = "src/main/resources/statgenerator.config";

	private static final String CONFIG_PATH_CURRENT = CONFIG_PATH_DEV;

	private static final List<CharacterBuilderComponent> COMPONENTS = new ArrayList<>();
	static {
		COMPONENTS.add(new PersonalDataMainComponent(0, 0));

		StatGenerator statGenerator = StatGenerator.createInstance(0, 0, CONFIG_PATH_CURRENT, false);
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