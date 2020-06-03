package simplecharacterbuilder.characterbuilder.social;

import simplecharacterbuilder.common.generated.Actor;
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.CharacterBuilderMainComponent;

public class SocialMainComponent extends CharacterBuilderMainComponent {

	private final SocialSelectionPanel selectionPanel = new SocialSelectionPanel(GAP_WIDTH, GAP_WIDTH);

	public SocialMainComponent() {
		this.mainPanel.add(selectionPanel);
	}

	@Override
	public void setValues(Actor actor) {
		// TODO
	}

	@Override
	public void enable() {
		super.enable();
		// TODO add refresh mechanic here (cache relevant parameters to check if necessary)
	}

	private void createList() {

	}

}
