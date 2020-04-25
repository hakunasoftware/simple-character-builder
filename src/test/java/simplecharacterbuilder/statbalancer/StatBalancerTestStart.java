package simplecharacterbuilder.statbalancer;

import static simplecharacterbuilder.util.CharacterBuilderComponent.CONTROLPANEL_HEIGHT;
import static simplecharacterbuilder.util.CharacterBuilderComponent.CONTROLPANEL_WIDTH;
import static simplecharacterbuilder.util.CharacterBuilderComponent.GAP_WIDTH;
import static simplecharacterbuilder.util.CharacterBuilderComponent.MAINPANEL_HEIGHT;
import static simplecharacterbuilder.util.CharacterBuilderComponent.MAINPANEL_WIDTH;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;

import simplecharacterbuilder.statgenerator.StatGenerator;
import simplecharacterbuilder.util.ApplicationFrame;
import simplecharacterbuilder.util.CharacterBuilderComponent;
import simplecharacterbuilder.util.GameFileRepository;

public class StatBalancerTestStart {
	private static final int WIDTH = MAINPANEL_WIDTH + StatGenerator.COMPARISON_WIDTH;
	private static final int HEIGHT = MAINPANEL_HEIGHT;

	private static final String CONFIG_PATH = "src/test/resources/";
	
	private static final String STATGENERATOR_CONFIG = CONFIG_PATH  + "statgenerator.config";
	private static final String PATH_VARIABLES_CONFIG = CONFIG_PATH  + "path_variables.config";

	private static final List<CharacterBuilderComponent> COMPONENTS = new ArrayList<>();
	static {
		GameFileRepository.init(PATH_VARIABLES_CONFIG);

		StatGenerator statGenerator = StatGenerator.createInstance(0, 0, STATGENERATOR_CONFIG, true);
		COMPONENTS.add(statGenerator);

		int controlPanelX = MAINPANEL_WIDTH - CONTROLPANEL_WIDTH - GAP_WIDTH;
		int controlPanelY = MAINPANEL_HEIGHT - CONTROLPANEL_HEIGHT - GAP_WIDTH;
		StatBalancerView controlPanel = new StatBalancerView(controlPanelX, controlPanelY, statGenerator, STATGENERATOR_CONFIG);
		COMPONENTS.add(controlPanel);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		controlPanel.loadXml();
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> new ApplicationFrame(WIDTH, HEIGHT, "SB2R StatBalancer Test", COMPONENTS));
	}
}