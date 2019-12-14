package simplecharacterbuilder.start;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import simplecharacterbuilder.start.ApplicationFrame.CharacterBuilderComponent;
import simplecharacterbuilder.statpicker.StatPicker;

public class StatPickerStart {

	private static final int WIDTH 	= StatPicker.WIDTH;
	private static final int HEIGHT = StatPicker.HEIGHT;
	
	private static final List<CharacterBuilderComponent> COMPONENTS = new ArrayList<>();
	static {
		COMPONENTS.add(StatPicker.createInstance().location(0, 0));
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> new ApplicationFrame(WIDTH, HEIGHT, COMPONENTS));
	}
}