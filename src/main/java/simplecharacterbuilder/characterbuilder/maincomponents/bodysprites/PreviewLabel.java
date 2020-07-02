package simplecharacterbuilder.characterbuilder.maincomponents.bodysprites;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import simplecharacterbuilder.characterbuilder.util.holder.ImageFileHolder;
import simplecharacterbuilder.characterbuilder.util.holder.BodyPartRepository;
import simplecharacterbuilder.characterbuilder.util.ui.UIComponentFactory;
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent;

@SuppressWarnings("serial")
class PreviewLabel extends JPanel {

	private final JLabel preview;

	PreviewLabel(int x, int y) {
		this.setBounds(x, y, 128, 212);
		this.setLayout(null);

		this.preview = new JLabel();
		this.preview.setBounds(0, 0, 128, 192);
		this.preview.setBorder(CharacterBuilderComponent.BORDER);
		this.preview.setHorizontalAlignment(JLabel.RIGHT);
		this.preview.setVerticalAlignment(JLabel.CENTER);
		this.add(this.preview);

		JLabel label = UIComponentFactory.createFormattedLabel("Preview", 0, 192, 128, JLabel.CENTER);
		label.setForeground(new Color(120, 120, 120, 255));
		this.add(label);
	}

	void update() {
		Collection<BufferedImage> sprites = ImageFileHolder.getBodySprites().keySet().stream()
				.sorted((k1, k2) -> BodyPartRepository.getDrawIndex(k1).compareTo(BodyPartRepository.getDrawIndex(k2)))
				.map(k -> readImage(k)).collect(Collectors.toList());
		this.preview.setIcon(new ImageIcon(overlay(sprites)));
	}

	private BufferedImage readImage(String imageName) {
		try {
			return ImageIO.read(ImageFileHolder.getBodySprite(imageName));
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}

	private BufferedImage overlay(Collection<BufferedImage> images) {
		BufferedImage combined = new BufferedImage(128, 192, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = combined.getGraphics();
		for (BufferedImage image : images) {
			graphics.drawImage(image, 0, 0, null);
		}
		graphics.dispose();
		return combined;
	}

}
