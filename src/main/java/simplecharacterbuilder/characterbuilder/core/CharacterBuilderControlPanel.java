package simplecharacterbuilder.characterbuilder.core;

import java.util.List;
import java.util.stream.Collectors;

import simplecharacterbuilder.util.CharacterBuilderComponent;
import simplecharacterbuilder.util.ControlPanel;

public class CharacterBuilderControlPanel extends ControlPanel {
	private static final int X_POS = MAINPANEL_WIDTH - CONTROLPANEL_WIDTH - GAP_WIDTH;
	private static final int Y_POS = MAINPANEL_HEIGHT - CONTROLPANEL_HEIGHT - GAP_WIDTH;
	
	private static final CharacterBuilderControlPanel INSTANCE = new CharacterBuilderControlPanel(X_POS, Y_POS);

	private List<CharacterBuilderMainComponent> mainComponents;

	private int pageIndex = 0;

	private CharacterBuilderControlPanel(int x, int y) {
		super(x, y, false, "Back", "Next", null);

		this.button1.addActionListener(e -> previous());
		this.button2.addActionListener(e -> next());

		this.button1.setVisible(false);
	}

	public static CharacterBuilderControlPanel getInstance() {
		return INSTANCE;
	}

	public void next() {
		if (!this.button1.isVisible()) {
			this.button1.setVisible(true);
		}

		mainComponents.get(pageIndex).disable();
		mainComponents.get(++pageIndex).enable();

		if (pageIndex == mainComponents.size() - 1) {
			this.button2.setVisible(false);
		}
	}

	public void previous() {
		if (!this.button2.isVisible()) {
			this.button2.setVisible(true);
		}

		mainComponents.get(pageIndex).disable();
		mainComponents.get(--pageIndex).enable();

		if (pageIndex == 0) {
			this.button1.setVisible(false);
		}
	}

	public void init(List<CharacterBuilderComponent> components) {
		mainComponents = components.stream().filter(c -> c instanceof CharacterBuilderMainComponent)
				.map(c -> (CharacterBuilderMainComponent) c).collect(Collectors.toList());
	}

}
