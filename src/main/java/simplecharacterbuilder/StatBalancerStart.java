package simplecharacterbuilder;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;

import simplecharacterbuilder.abstractview.ApplicationFrame;
import simplecharacterbuilder.abstractview.CharacterBuilderComponent;
import simplecharacterbuilder.statgenerator.StatGenerator;
import simplecharacterbuilder.statgenerator.xmlreaderwriter.StatGeneratorXmlReaderWriterView;

public class StatBalancerStart {

	private static final int WIDTH 	= CharacterBuilderComponent.MAINPANEL_WIDTH;
	private static final int HEIGHT = CharacterBuilderComponent.MAINPANEL_HEIGHT + CharacterBuilderComponent.CONTROLPANEL_HEIGHT;
	
	private static final String CONFIG_PATH_DEV = "src/main/resources/config";
	@SuppressWarnings("unused")
	private static final String CONFIG_PATH_USE = "config_test";
	
	private static final List<CharacterBuilderComponent> COMPONENTS = new ArrayList<>();
	static {
		StatGenerator statGenerator = StatGenerator.createInstance(0, 0, CONFIG_PATH_DEV);
		COMPONENTS.add(statGenerator);
		COMPONENTS.add(new StatGeneratorXmlReaderWriterView(0, CharacterBuilderComponent.MAINPANEL_HEIGHT, statGenerator));
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(() -> new ApplicationFrame(WIDTH, HEIGHT, "SB2R StatBalancer", COMPONENTS));
	}
}