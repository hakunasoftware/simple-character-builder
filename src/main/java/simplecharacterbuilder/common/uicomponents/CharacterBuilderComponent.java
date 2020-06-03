package simplecharacterbuilder.common.uicomponents;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import simplecharacterbuilder.common.generated.Actor;
import simplecharacterbuilder.common.statgenerator.StatGenerator;

public abstract class CharacterBuilderComponent {

	public static final int MAINPANEL_WIDTH = StatGenerator.WIDTH;
	public static final int MAINPANEL_HEIGHT = StatGenerator.HEIGHT;

	public static final int SHORTENED_CONTENT_HEIGHT = 235;

	public static final int GAP_WIDTH = 15;

	public static final int CONTROLPANEL_WIDTH = 205;
	public static final int CONTROLPANEL_HEIGHT = 77;

	public static final Border BORDER = BorderFactory.createLineBorder(new Color(200, 200, 200, 255));

	protected JPanel mainPanel;

	public CharacterBuilderComponent(int x, int y, int width, int height) {
		mainPanel = new JPanel();
		mainPanel.setBounds(x, y, width, height);
		mainPanel.setLayout(null);
	}

	public void addTo(JLayeredPane panel, int layer) {
		panel.add(mainPanel, layer);
	}

	public void enable() {
		this.mainPanel.setVisible(true);
	}

	public void disable() {
		this.mainPanel.setVisible(false);
	}

	public static abstract class CharacterBuilderMainComponent extends CharacterBuilderComponent {
		protected CharacterBuilderMainComponent() {
			super(0, 0, MAINPANEL_WIDTH, MAINPANEL_HEIGHT);
		}
		
		public abstract void setValues(Actor actor);
	}

	public static abstract class CharacterBuilderControlComponent extends CharacterBuilderComponent {
		protected CharacterBuilderControlComponent(int x, int y, boolean extended) {
			super(x, y, CONTROLPANEL_WIDTH + (extended ? StatGenerator.COMPARISON_WIDTH : 0), CONTROLPANEL_HEIGHT);
		}
	}
}
