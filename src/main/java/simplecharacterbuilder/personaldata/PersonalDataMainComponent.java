package simplecharacterbuilder.personaldata;

import simplecharacterbuilder.util.CharacterBuilderComponent.CharacterBuilderMainComponent;

public class PersonalDataMainComponent extends CharacterBuilderMainComponent {

	public PersonalDataMainComponent(int x, int y) {
		super(x, y);
		this.mainPanel.add(new PersonalDataPanel());
		this.mainPanel.add(new PictureLoader());
	}
	
}
