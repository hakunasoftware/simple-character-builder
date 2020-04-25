package simplecharacterbuilder.characterbuilder.personaldata;

import simplecharacterbuilder.util.CharacterBuilderComponent.CharacterBuilderMainComponent;

public class PersonalDataMainComponent extends CharacterBuilderMainComponent {
	private static final int WIDTH_SOURCE_PANEL = CONTROLPANEL_WIDTH;
	private static final int HEIGHT_SOURCE_PANEL = 115;
	private static final int XPOS_SOURCE_PANEL = MAINPANEL_WIDTH - CONTROLPANEL_WIDTH - GAP_WIDTH;
	private static final int YPOS_SOURCE_PANEL = MAINPANEL_HEIGHT - CONTROLPANEL_HEIGHT - HEIGHT_SOURCE_PANEL - 2 * GAP_WIDTH;
	private static final int WIDTH_PICTURE_LOADER = 105;
	private static final int XPOS_PICTURE_LOADER = MAINPANEL_WIDTH - GAP_WIDTH - (CONTROLPANEL_WIDTH + WIDTH_PICTURE_LOADER) / 2;
	
	public PersonalDataMainComponent(int x, int y) {
		super(x, y);
		this.mainPanel.add(new PersonalDataPanel());
		this.mainPanel.add(new PictureLoader(XPOS_PICTURE_LOADER, GAP_WIDTH, WIDTH_PICTURE_LOADER));
		this.mainPanel.add(new SourcePanel(XPOS_SOURCE_PANEL, YPOS_SOURCE_PANEL, WIDTH_SOURCE_PANEL, HEIGHT_SOURCE_PANEL));
	}
	
}
