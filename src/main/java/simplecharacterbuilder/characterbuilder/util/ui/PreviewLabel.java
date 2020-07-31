package simplecharacterbuilder.characterbuilder.util.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import simplecharacterbuilder.characterbuilder.util.holder.DrawIndexRepository;
import simplecharacterbuilder.characterbuilder.util.holder.EquipTypeRepository;
import simplecharacterbuilder.characterbuilder.util.holder.ImageFileHolder;
import simplecharacterbuilder.common.ErrorLogfileWriter;
import simplecharacterbuilder.common.generated.EquipTypeType;
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent;

@SuppressWarnings("serial")
public class PreviewLabel extends JPanel {
	private final JLabel preview;
	private final boolean showEquipment;
	
	private final List<String> hiddenEquipTypes = new ArrayList<>();

	public PreviewLabel(int x, int y, boolean showEquipment) {
		this.showEquipment = showEquipment;
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

	public void update() {
		Map<File, Long> sprites = new HashMap<>();
		
		Map<String, File> bodySprites = ImageFileHolder.getBodySprites();
		bodySprites.keySet().stream().forEach(k -> {sprites.put(bodySprites.get(k), DrawIndexRepository.parse(k));});

		if(showEquipment) {
			Map<String, File> equipSprites = ImageFileHolder.getEquipSprites();
			equipSprites.keySet().stream().filter(k -> !isHidden(k))
				.forEach(k -> addEquipLayerToSprites(sprites, equipSprites.get(k), k));
		}
		
		Collection<BufferedImage> images = sprites.keySet().stream().sorted((a, b) -> sprites.get(a).compareTo(sprites.get(b)))
				.map(k -> readImage(k)).collect(Collectors.toList());
		this.preview.setIcon(new ImageIcon(overlay(images)));
	}

	private BufferedImage readImage(File imageFile) {
		try {
			return ImageIO.read(imageFile);
		} catch (IOException e) {
			ErrorLogfileWriter.logException(e);
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

	public void addEquipLayerToSprites(Map<File, Long> sprites, File sprite, String layerType) {
		String drawIndex;
		if(layerType.endsWith(ImageFileHolder.EXTRA_LAYER_SUFFIX)) {
			EquipTypeType equipType = EquipTypeRepository.getEquipType(layerType.replace(ImageFileHolder.EXTRA_LAYER_SUFFIX, ""));
			drawIndex = equipType.getDrawIndices().getExtraIndex();
		} else {
			EquipTypeType equipType = EquipTypeRepository.getEquipType(layerType);
			drawIndex = equipType.getDrawIndex() != null ? equipType.getDrawIndex() : equipType.getDrawIndices().getDrawIndex();
		}
		sprites.put(sprite, DrawIndexRepository.parse(drawIndex));
	}
	
	public void hideEquipType(String equipType, boolean hidden) {
		if(hidden) {
			this.hiddenEquipTypes.add(equipType);
		} else {
			this.hiddenEquipTypes.remove(equipType);
		}
	}
	
	private boolean isHidden(String equipType) {
		return this.hiddenEquipTypes.contains(equipType.replace(ImageFileHolder.EXTRA_LAYER_SUFFIX, ""));
	}
	
}
