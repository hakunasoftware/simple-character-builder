package simplecharacterbuilder.personaldata;

import static simplecharacterbuilder.util.CharacterBuilderComponent.CONTROLPANEL_WIDTH;
import static simplecharacterbuilder.util.CharacterBuilderComponent.GAP_WIDTH;
import static simplecharacterbuilder.util.CharacterBuilderComponent.MAINPANEL_HEIGHT;
import static simplecharacterbuilder.util.CharacterBuilderComponent.MAINPANEL_WIDTH;

import javax.swing.JTextField;

import simplecharacterbuilder.util.ContentPanel;

@SuppressWarnings("serial")
class PersonalDataPanel extends ContentPanel {

	static final int WIDTH = MAINPANEL_WIDTH - CONTROLPANEL_WIDTH - 3 * GAP_WIDTH;
	static final int HEIGHT = MAINPANEL_HEIGHT - 2 * GAP_WIDTH;

	PersonalDataPanel() {
		super(GAP_WIDTH, GAP_WIDTH, WIDTH, HEIGHT);
	}

}
