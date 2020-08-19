package simplecharacterbuilder.characterbuilder.maincomponents.bodysprites;

import static simplecharacterbuilder.common.resourceaccess.PropertyRepository.BODY_TEMPLATE_BIG;
import static simplecharacterbuilder.common.resourceaccess.PropertyRepository.BODY_TEMPLATE_DARK_BIG;
import static simplecharacterbuilder.common.resourceaccess.PropertyRepository.BODY_TEMPLATE_DARK_MEDIUM;
import static simplecharacterbuilder.common.resourceaccess.PropertyRepository.BODY_TEMPLATE_DARK_SMALL;
import static simplecharacterbuilder.common.resourceaccess.PropertyRepository.BODY_TEMPLATE_MEDIUM;
import static simplecharacterbuilder.common.resourceaccess.PropertyRepository.BODY_TEMPLATE_SMALL;

import java.io.File;

import javax.swing.JOptionPane;

import simplecharacterbuilder.characterbuilder.core.CharacterBuilderControlPanel;
import simplecharacterbuilder.characterbuilder.util.holder.ApplicationFrameHolder;
import simplecharacterbuilder.characterbuilder.util.holder.ImageFileHolder;
import simplecharacterbuilder.characterbuilder.util.ui.PictureLoader;
import simplecharacterbuilder.characterbuilder.util.ui.PictureLoader.FileProcessor;
import simplecharacterbuilder.characterbuilder.util.ui.PreviewLabel;
import simplecharacterbuilder.common.generated.Actor;
import simplecharacterbuilder.common.resourceaccess.GameFileAccessor;
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.CharacterBuilderMainComponent;
import simplecharacterbuilder.common.uicomponents.ControlPanel;

public class SpriteLoaderMainComponent extends CharacterBuilderMainComponent {
	public static final String FLAT_CHESTED = "Flat-Chested";
	public static final String SMALL_BREASTS = "Small Breasts";
	public static final String MEDIUM_BREASTS = "Medium Breasts";
	public static final String LARGE_BREASTS = "Large Breasts";
	public static final String XL_BREASTS = "XL Breasts";

	private static final double SCALE = 0.65;
	private static final int MAIN_SPRITE_LOADER_X_OFFSET = 42;

	private final SpriteTemplateInfoSelector spriteTemplateInfoSelector;
	private final PreviewLabel previewLabel;

	private PictureLoader bodySpriteLoader;
	private boolean usingTemplates = false;

	public SpriteLoaderMainComponent() {
		this.spriteTemplateInfoSelector = new SpriteTemplateInfoSelector(GAP_WIDTH, e -> {
			if (usingTemplates) {
				loadBodyTemplate();
			}
		});
		this.mainPanel.add(this.spriteTemplateInfoSelector);
		this.mainPanel.add(createMainSpritePictureLoader(MAIN_SPRITE_LOADER_X_OFFSET, ImageFileHolder.BODY));
		this.mainPanel.add(createMainSpritePictureLoader(
				AdditionalBodySpriteLoader.WIDTH - (int) (SCALE * 128) - MAIN_SPRITE_LOADER_X_OFFSET,
				ImageFileHolder.HAIR));

		this.previewLabel = new PreviewLabel(CharacterBuilderControlPanel.X_POS + (ControlPanel.WIDTH_BASIC - 128) / 2,
				30, false);
		this.mainPanel.add(this.previewLabel);

		this.mainPanel.add(new AdditionalBodySpriteLoader(previewLabel));
	}

	@Override
	public void enable() {
		super.enable();
		ApplicationFrameHolder.setApplicationFrameTitle("Body Sprites");
	}

	@Override
	public void setValues(Actor actor) {
		Actor.Body body = actor.getBody();
		if (body == null) {
			actor.setBody(new Actor.Body());
			body = actor.getBody();
		}
		body.setType(this.spriteTemplateInfoSelector.getBodyType());
		body.setSkin(this.spriteTemplateInfoSelector.isDarkSkinned() ? "Dark" : "Light");
	}

	private PictureLoader createMainSpritePictureLoader(int x, String spriteName) {
		FileProcessor fileProcessor = f -> {
			ImageFileHolder.putBodySprite(spriteName, f);
			this.previewLabel.update();
		};

		int xPos = GAP_WIDTH + x;
		int yPos = GAP_WIDTH + 50;
		String text = "Select " + spriteName + " sprite (128x192px and png-format)";

		PictureLoader picLoader;
		if (ImageFileHolder.BODY.equals(spriteName)) {
			picLoader = new BodySpriteLoader(xPos, yPos, 128, 192, SCALE, fileProcessor, text);
			this.bodySpriteLoader = picLoader;
		} else {
			picLoader = new PictureLoader(xPos, yPos, 128, 192, SCALE, fileProcessor, text);
		}
		picLoader.setText("Load " + spriteName);
		picLoader.setToolTipText("Click to load a sprite for the character's " + spriteName.toLowerCase()
				+ " (128x192px in png-format).");
		return picLoader;
	}

	@SuppressWarnings("serial")
	private class BodySpriteLoader extends PictureLoader {

		public BodySpriteLoader(int x, int y, int imgWidth, int imgHeight, double scale, FileProcessor fileProcessor,
				String dialogTitle) {
			super(x, y, imgWidth, imgHeight, scale, fileProcessor, dialogTitle);
		}

		@Override
		protected void selectPath() {
			Object[] options = { "Use templates", "Load custom body" };
			int n = JOptionPane.showOptionDialog(null,
					"<html>Do you want to use Body templates or your own Body sprite?<br/><br/><i>(Note: If you're a dev and want to commit the character to the game, a custom body is required since the eyes must be customized!)</i></html>",
					"Body Sprite Loading Method", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
					options, options[1]);
			if (n == 1) {
				usingTemplates = false;
				super.selectPath();
			} else if (n == 0) {
				usingTemplates = true;
				loadBodyTemplate();
			}
		}
	}

	private void loadBodyTemplate() {
		String spriteTemplate = determineSpriteTemplateFromBodyType(this.spriteTemplateInfoSelector.getBodyType());
		this.bodySpriteLoader.setImage(
				getBodyTemplateFromTemplateName(spriteTemplate, this.spriteTemplateInfoSelector.isDarkSkinned()));
	}

	public static String determineSpriteTemplateFromBodyType(String bodyType) {
		switch (bodyType) {
		case FLAT_CHESTED:
		case SMALL_BREASTS:
			return SMALL_BREASTS;
		case MEDIUM_BREASTS:
			return MEDIUM_BREASTS;
		case LARGE_BREASTS:
		case XL_BREASTS:
			return LARGE_BREASTS;
		}
		throw new IllegalArgumentException("Invalid BodyType: " + bodyType);
	}

	private File getBodyTemplateFromTemplateName(String template, boolean darkSkinned) {
		String property;
		switch (template) {
		case SMALL_BREASTS:
			property = darkSkinned ? BODY_TEMPLATE_DARK_SMALL : BODY_TEMPLATE_SMALL;
			break;
		case MEDIUM_BREASTS:
			property = darkSkinned ? BODY_TEMPLATE_DARK_MEDIUM : BODY_TEMPLATE_MEDIUM;
			break;
		case LARGE_BREASTS:
			property = darkSkinned ? BODY_TEMPLATE_DARK_BIG : BODY_TEMPLATE_BIG;
			break;
		default:
			throw new IllegalArgumentException("Invalid sprite template: " + template);
		}
		return GameFileAccessor.getFileFromProperty(property);
	}

}
