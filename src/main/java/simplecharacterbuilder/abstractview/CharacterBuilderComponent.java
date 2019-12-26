package simplecharacterbuilder.abstractview;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

public abstract class CharacterBuilderComponent {
	
	public static final int MAINPANEL_WIDTH 	= 555;
	public static final int MAINPANEL_HEIGHT 	= 357;
	
	public static final int CONTROLPANEL_WIDTH 	= MAINPANEL_WIDTH;
	public static final int CONTROLPANEL_HEIGHT = 75;

	public static final int GAP_WIDTH = 15;
	
	public static final Border BORDER = BorderFactory.createLineBorder(new Color(200, 200, 200, 255));

	protected JPanel mainPanel;

	public CharacterBuilderComponent(int x, int y, int width, int height) {
		mainPanel = new JPanel();
		mainPanel.setBounds(x, y, width, height);
		mainPanel.setLayout(null);
	}

	public void addTo(JComponent panel) {
		panel.add(mainPanel);
	}
	

	public static abstract class CharacterBuilderMainComponent extends CharacterBuilderComponent {
		protected CharacterBuilderMainComponent(int x, int y) {
			super(x, y, MAINPANEL_WIDTH, MAINPANEL_HEIGHT);
		}
	}

	public static abstract class CharacterBuilderControlComponent extends CharacterBuilderComponent {
		protected CharacterBuilderControlComponent(int x, int y) {
			super(x, y, CONTROLPANEL_WIDTH, CONTROLPANEL_HEIGHT);
		}
	}
}
