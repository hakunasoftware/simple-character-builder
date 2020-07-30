package simplecharacterbuilder.characterbuilder.util.holder;

import java.util.List;

import javax.swing.JFrame;

import simplecharacterbuilder.common.uicomponents.ApplicationFrame;
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent;

public class ApplicationFrameHolder {
	private static final ApplicationFrameHolder INSTANCE = new ApplicationFrameHolder();
	
	private JFrame frame;
	private String title;
	
	private ApplicationFrameHolder() {}
	
	public static void setApplicationFrameTitle(String subtitle) {
		INSTANCE.frame.setTitle(INSTANCE.title + " - " + subtitle);
	}
	
	public static JFrame createApplicationFrame(int width, int height, String title, String initialSubtitle, List<CharacterBuilderComponent> components) {
		INSTANCE.title = title;
		INSTANCE.frame = new ApplicationFrame(width, height, title, components);
		setApplicationFrameTitle(initialSubtitle);
		return INSTANCE.frame;
	}
	
}
