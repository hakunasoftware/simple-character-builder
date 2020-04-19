package simplecharacterbuilder.util;

import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.UIManager;

import simplecharacterbuilder.statgenerator.StatGenerator;
import simplecharacterbuilder.util.CharacterBuilderComponent.CharacterBuilderControlComponent;

public class ControlPanel extends CharacterBuilderControlComponent {
	public static final int WIDTH_BASIC = CONTROLPANEL_WIDTH;
	public static final int WIDTH_EXTENDED = CONTROLPANEL_WIDTH + StatGenerator.COMPARISON_WIDTH;
	
	protected static final int LABEL_OFFSET = 8;

	protected final JLabel portrait;
	protected final JLabel nameLabel;
	protected final JLabel franchiseLabel;

	protected final ControlButton button1;
	protected final ControlButton button2;

	protected final boolean extended;

	public ControlPanel(int x, int y, boolean extended, String nameButton1, String nameButton2, String defaultPicTest) {
		super(x, y, extended);

		this.extended = extended;

		mainPanel.setBorder(BORDER);

		this.button1 = new ControlButton(nameButton1, 0, extended);
		mainPanel.add(button1);
		this.button2 = new ControlButton(nameButton2, extended ? ControlButton.WIDTH_EXTENDED : ControlButton.WIDTH_BASIC, extended);
		mainPanel.add(button2);

		nameLabel = createTextLabel(0);
		franchiseLabel = createTextLabel(15);
		franchiseLabel.setForeground(new Color(150, 150, 150, 255));
		mainPanel.add(nameLabel);
		mainPanel.add(franchiseLabel);

		portrait = new JLabel();
		portrait.setBounds((extended ? WIDTH_EXTENDED : WIDTH_BASIC) - CONTROLPANEL_HEIGHT, 0, CONTROLPANEL_HEIGHT,
				CONTROLPANEL_HEIGHT);
		portrait.setBorder(BORDER);
		portrait.setText(defaultPicTest);
		mainPanel.add(portrait);
	}

	private JLabel createTextLabel(int additionalVerticalOffset) {
		JLabel label = new JLabel();
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setBounds(LABEL_OFFSET, LABEL_OFFSET - 2 + additionalVerticalOffset,
				(extended ? WIDTH_EXTENDED : WIDTH_BASIC) - CONTROLPANEL_HEIGHT - 2 * LABEL_OFFSET, 15);
		return label;
	}

	public void setName(String name) {
		nameLabel.setText(name);
	}

	public void setFranchise(String franchise) {
		franchiseLabel.setText("<html><i>" + franchise + "</i></html>");
	}

	public void setPortrait(String portraitURI) {
		Image image = new ImageIcon(portraitURI).getImage();
		Image scaledImage = image.getScaledInstance(portrait.getWidth(), portrait.getHeight(), Image.SCALE_SMOOTH);
		portrait.setIcon(new ImageIcon(scaledImage));
	}

	@SuppressWarnings("serial")
	protected static class ControlButton extends JButton {
		static final int WIDTH_BASIC = (ControlPanel.WIDTH_BASIC - CONTROLPANEL_HEIGHT) / 2;
		static final int WIDTH_EXTENDED = (ControlPanel.WIDTH_EXTENDED - CONTROLPANEL_HEIGHT) / 2;

		static final int HEIGHT = 36;

		ControlButton(String buttonText, int xPos, boolean extended) {
			super(buttonText);
			this.setBounds(xPos, CONTROLPANEL_HEIGHT - HEIGHT, extended ? WIDTH_EXTENDED : WIDTH_BASIC, HEIGHT);
			this.setHorizontalAlignment(CENTER);
		}
	}
}
