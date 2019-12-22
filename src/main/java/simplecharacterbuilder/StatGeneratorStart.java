package simplecharacterbuilder;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import simplecharacterbuilder.abstractview.ApplicationFrame;
import simplecharacterbuilder.abstractview.CharacterBuilderComponent;
import simplecharacterbuilder.statgenerator.StatGenerator;
import simplecharacterbuilder.statgenerator.xmlreaderwriter.StatGeneratorXmlReaderWriterView;
import simplecharacterbuilder.test.TestMainPanel;

public class StatGeneratorStart {

	private static final int WIDTH 	= CharacterBuilderComponent.MAINPANEL_WIDTH;
	private static final int HEIGHT = CharacterBuilderComponent.MAINPANEL_HEIGHT + CharacterBuilderComponent.CONTROLPANEL_HEIGHT;
	
	private static final String CONFIG_PATH_DEV = "src/main/resources/config";
	@SuppressWarnings("unused")
	private static final String CONFIG_PATH_USE = "config_test";
	
	private static final List<CharacterBuilderComponent> COMPONENTS = new ArrayList<>();
	static {
		StatGenerator statGenerator = StatGenerator.createInstance(0, 0, CONFIG_PATH_DEV);
//		COMPONENTS.add(statGenerator);
		COMPONENTS.add(new TestMainPanel(0, 0));
		COMPONENTS.add(new StatGeneratorXmlReaderWriterView(0, CharacterBuilderComponent.MAINPANEL_HEIGHT, statGenerator));
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> new ApplicationFrame(WIDTH, HEIGHT, COMPONENTS));
	}
	
	//TODO only allow numbers (up to 3) for textfield
}