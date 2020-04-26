package simplecharacterbuilder.statbalancer;

import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.CONTROLPANEL_HEIGHT;
import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.CONTROLPANEL_WIDTH;
import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.GAP_WIDTH;
import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.MAINPANEL_HEIGHT;
import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.MAINPANEL_WIDTH;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;

import simplecharacterbuilder.common.resourceaccess.ConfigReaderRepository;
import simplecharacterbuilder.common.statgenerator.StatGenerator;
import simplecharacterbuilder.common.uicomponents.ApplicationFrame;
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent;

public class StatBalancerStart {
	private static final int WIDTH = MAINPANEL_WIDTH + StatGenerator.COMPARISON_WIDTH;
	private static final int HEIGHT = MAINPANEL_HEIGHT;

	private static final List<CharacterBuilderComponent> COMPONENTS = new ArrayList<>();
	static {
		ConfigReaderRepository.init();
		
		StatGenerator statGenerator = StatGenerator.createInstance(0, 0, true);
		COMPONENTS.add(statGenerator);

		int controlPanelX = MAINPANEL_WIDTH - CONTROLPANEL_WIDTH - GAP_WIDTH;
		int controlPanelY = MAINPANEL_HEIGHT - CONTROLPANEL_HEIGHT - GAP_WIDTH;
		StatBalancerView controlPanel = new StatBalancerView(controlPanelX, controlPanelY, statGenerator);
		COMPONENTS.add(controlPanel);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		controlPanel.loadXml();
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> new ApplicationFrame(WIDTH, HEIGHT, "SB2R StatBalancer", COMPONENTS));
	}
}