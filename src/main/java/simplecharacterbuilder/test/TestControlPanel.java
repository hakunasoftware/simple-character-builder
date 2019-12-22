package simplecharacterbuilder.test;

import java.awt.Color;

import simplecharacterbuilder.abstractview.CharacterBuilderComponent.CharacterBuilderControlComponent;

public class TestControlPanel extends CharacterBuilderControlComponent {
	
	public TestControlPanel(int x, int y) {
		super(x, y);
		
		mainPanel.setBackground(Color.RED);
	}

}
