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
	
	@SuppressWarnings("unused")
	private static final String CONFIG_PATH_USE = "config/statgenerator.config";
	private static final String CONFIG_PATH_DEV = "src/main/resources/statgenerator.config";
	
	private static final String CONFIG_PATH_CURRENT = CONFIG_PATH_DEV;
	
	private static final List<CharacterBuilderComponent> COMPONENTS = new ArrayList<>();
	static {
		StatGenerator statGenerator = StatGenerator.createInstance(0, 0, CONFIG_PATH_CURRENT);
		COMPONENTS.add(statGenerator);
		COMPONENTS.add(new StatGeneratorXmlReaderWriterView(0, CharacterBuilderComponent.MAINPANEL_HEIGHT, statGenerator, CONFIG_PATH_CURRENT));
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