package simplecharacterbuilder.characterbuilder.personaldata;

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
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent;

@SuppressWarnings("serial")
class PictureLoader extends JLabel {
	private final int width;

	private Component applicationFrame;

	PictureLoader(int x, int y, int width) {
		this.width = width;
		this.setText("Load Portrait");
		this.setHorizontalAlignment(SwingConstants.CENTER);
		this.setBounds(x, y, width, width);
		this.setBorder(CharacterBuilderComponent.BORDER);
		this.addMouseListener(new LoadPictureMouseListener());
		this.setToolTipText("Click to load a portrait for the character (required). Portraits must be 200x200 pixel and in png-format.");
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
			JOptionPane.showMessageDialog(getApplicationFrame(), "Portraits must be 200x200px and in png-format");
			return;
		}
		Image scaledImage = image.getScaledInstance(this.width, this.width, Image.SCALE_SMOOTH);
		this.setHorizontalAlignment(SwingConstants.LEFT);
		this.setIcon(new ImageIcon(scaledImage));
		
		CharacterBuilderControlPanel.getInstance().setPortrait(portrait.getAbsolutePath());
	}

	private Component getApplicationFrame() {
		return applicationFrame == null ? applicationFrame = (JFrame) SwingUtilities.getWindowAncestor(this)
				: applicationFrame;
	}

}
