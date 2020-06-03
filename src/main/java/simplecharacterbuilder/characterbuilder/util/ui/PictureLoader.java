package simplecharacterbuilder.characterbuilder.util.ui;

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

import simplecharacterbuilder.characterbuilder.util.holder.JFileChooserPool;
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent;

@SuppressWarnings("serial")
public class PictureLoader extends JLabel {
	private final int width;
	private final int height;

	private Component applicationFrame;
	private int imgWidth;
	private int imgHeight;
	private FileProcessor fileProcessor;
	private String dialogTitle;

	public PictureLoader(int x, int y, int imgWidth, int imgHeight, double scale, FileProcessor fileProcessor, String dialogTitle) {
		this.width = (int) (scale * imgWidth);
		this.height = (int) (scale * imgHeight);
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
		this.fileProcessor = fileProcessor;
		this.dialogTitle = dialogTitle;
		this.setHorizontalAlignment(SwingConstants.CENTER);
		this.setBounds(x, y, this.width, this.height);
		this.setBorder(CharacterBuilderComponent.BORDER);
		this.addMouseListener(new LoadPictureMouseListener());
	}

	private class LoadPictureMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			selectPath();
		}
	}

	private void selectPath() {
		JFileChooser fileChooser = JFileChooserPool.getPngFileChooser(this.dialogTitle);
		try {
			fileChooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());

			if (fileChooser.showOpenDialog(getApplicationFrame()) == JFileChooser.APPROVE_OPTION) {
				setImage(fileChooser.getSelectedFile());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setImage(File imgFile) {
		Image image = new ImageIcon(imgFile.getAbsolutePath()).getImage();
		if (!imgFile.getName().endsWith(".png") || image.getWidth(null) != imgWidth || image.getHeight(null) != imgHeight) {
			JOptionPane.showMessageDialog(getApplicationFrame(), "The image must be " + imgWidth + "x" + imgHeight + "px and in png-format.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Image scaledImage = image.getScaledInstance(this.width, this.height, Image.SCALE_SMOOTH);
		this.setHorizontalAlignment(SwingConstants.LEFT);
		this.setIcon(new ImageIcon(scaledImage));
		
		if(this.fileProcessor != null) {
			this.fileProcessor.process(imgFile);
		}
	}

	private Component getApplicationFrame() {
		return applicationFrame == null ? applicationFrame = (JFrame) SwingUtilities.getWindowAncestor(this)
				: applicationFrame;
	}
	
	public static interface FileProcessor{
		void process(File file);
	}

}