package simplecharacterbuilder.personaldata;

import static simplecharacterbuilder.util.CharacterBuilderComponent.CONTROLPANEL_WIDTH;
import static simplecharacterbuilder.util.CharacterBuilderComponent.GAP_WIDTH;
import static simplecharacterbuilder.util.CharacterBuilderComponent.MAINPANEL_HEIGHT;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;

import simplecharacterbuilder.characterbuilder.core.CharacterBuilderControlPanel;
import simplecharacterbuilder.characterbuilder.util.JFileChooserPool;
import simplecharacterbuilder.util.CharacterBuilderComponent;
import simplecharacterbuilder.util.ContentPanel;
import simplecharacterbuilder.util.ControlPanel;

@SuppressWarnings("serial")
class PictureLoader extends ContentPanel {
	static final int WIDTH = CONTROLPANEL_WIDTH;
	static final int HEIGHT = MAINPANEL_HEIGHT - ControlPanel.CONTROLPANEL_HEIGHT - 3 * GAP_WIDTH;

	private final JLabel portraitLabel;

	private Component applicationFrame;
	
	PictureLoader() {
		super(PersonalDataPanel.WIDTH + 2 * GAP_WIDTH, GAP_WIDTH, WIDTH, HEIGHT);
		this.setBorder(null);

		this.portraitLabel = new JLabel("Load Portrait", SwingConstants.CENTER);
		this.portraitLabel.setBounds(2, 0, 200, 200);
		this.portraitLabel.setBorder(CharacterBuilderComponent.BORDER);
		this.portraitLabel.addMouseListener(new LoadPictureMouseListener());
		this.add(this.portraitLabel);
	}

	private class LoadPictureMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			selectPath();
		}
	}

	private void selectPath() {
		JFileChooser portraitChooser = JFileChooserPool.getPortraitChooser();
		try {
			portraitChooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());

			if (portraitChooser.showOpenDialog(getApplicationFrame()) == JFileChooser.APPROVE_OPTION) {
				setPortrait(portraitChooser.getSelectedFile());
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setPortrait(File portrait) {
		Image image = new ImageIcon(portrait.getAbsolutePath()).getImage();
		if (!portrait.getName().endsWith(".png") || image.getWidth(null) != 200 || image.getHeight(null) != 200) {
			JOptionPane.showMessageDialog(getApplicationFrame(), "Portraits must be 200x200 pixels and in png-format");
			return;
		}
		this.portraitLabel.setHorizontalAlignment(SwingConstants.LEFT);
		this.portraitLabel.setIcon(new ImageIcon(image));

		CharacterBuilderControlPanel.getInstance().setPortrait(portrait.getAbsolutePath());
	}

	private Component getApplicationFrame() {
		return applicationFrame == null ? applicationFrame = (JFrame) SwingUtilities.getWindowAncestor(this)
				: applicationFrame;
	}

}
