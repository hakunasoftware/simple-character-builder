package simplecharacterbuilder.statbalancer;

import static simplecharacterbuilder.abstractview.CharacterBuilderComponent.*;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import simplecharacterbuilder.abstractview.ApplicationFrame;
import simplecharacterbuilder.abstractview.CharacterBuilderComponent;
import simplecharacterbuilder.statgenerator.StatGenerator;

public class StatBalancerStart {
	private static final int WIDTH 	= MAINPANEL_WIDTH + StatGenerator.COMPARISON_WIDTH;
	private static final int HEIGHT = MAINPANEL_HEIGHT;
	
	@SuppressWarnings("unused")
	private static final String CONFIG_PATH_USE = "config/statgenerator.config";
	private static final String CONFIG_PATH_DEV = "src/main/resources/statgenerator.config";
	
	private static final String CONFIG_PATH_CURRENT = CONFIG_PATH_DEV;
	
	private static final List<CharacterBuilderComponent> COMPONENTS = new ArrayList<>();
	static {
		StatGenerator statGenerator = StatGenerator.createInstance(0, 0, CONFIG_PATH_CURRENT, true);
		COMPONENTS.add(statGenerator);
		
		int controlPanelX = MAINPANEL_WIDTH  - CONTROLPANEL_WIDTH  - GAP_WIDTH;
		int controlPanelY = MAINPANEL_HEIGHT - CONTROLPANEL_HEIGHT - GAP_WIDTH;
		COMPONENTS.add(new StatBalancerView(controlPanelX, controlPanelY, statGenerator, CONFIG_PATH_CURRENT));
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> new ApplicationFrame(WIDTH, HEIGHT, "SB2R StatBalancer", COMPONENTS));
	}
}