package simplecharacterbuilder.characterbuilder.bodysprites;

import simplecharacterbuilder.characterbuilder.core.CharacterBuilderControlPanel;
import simplecharacterbuilder.characterbuilder.util.holder.BodyImageFileHolder;
import simplecharacterbuilder.characterbuilder.util.ui.PictureLoader;
import simplecharacterbuilder.characterbuilder.util.ui.PictureLoader.FileProcessor;
import simplecharacterbuilder.common.generated.Actor;
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.CharacterBuilderMainComponent;
import simplecharacterbuilder.common.uicomponents.ControlPanel;

public class SpriteLoaderMainComponent extends CharacterBuilderMainComponent {
	private static final double SCALE = 0.65;
	private static final int MAIN_SPRITE_LOADER_X_OFFSET = 42;
	
	private final SpriteTemplateInfoSelector spriteTemplateInfoSelector;
	private final PreviewLabel previewLabel;

	public SpriteLoaderMainComponent() {
		this.spriteTemplateInfoSelector = new SpriteTemplateInfoSelector(GAP_WIDTH);
		this.mainPanel.add(this.spriteTemplateInfoSelector);
		this.mainPanel.add(createMainSpritePictureLoader(MAIN_SPRITE_LOADER_X_OFFSET, BodyImageFileHolder.BODY));
		this.mainPanel.add(createMainSpritePictureLoader(AdditionalBodySpriteLoader.WIDTH - (int) (SCALE * 128) - MAIN_SPRITE_LOADER_X_OFFSET, BodyImageFileHolder.HAIR));
		
		this.previewLabel = new PreviewLabel(CharacterBuilderControlPanel.X_POS + (ControlPanel.WIDTH_BASIC - 128) / 2, 30);
		this.mainPanel.add(this.previewLabel);

		this.mainPanel.add(new AdditionalBodySpriteLoader(previewLabel));
	}

	@Override
	public void setValues(Actor actor) {
		Actor.Body body = actor.getBody();
		if(body == null) {
			actor.setBody(new Actor.Body());
			body = actor.getBody();
		}
		body.setType(this.spriteTemplateInfoSelector.getBodyType());
		body.setSkin(this.spriteTemplateInfoSelector.getSkinColor());
	}
	
	private PictureLoader createMainSpritePictureLoader(int x, String spriteName) {
		FileProcessor fileProcessor = f -> {
			BodyImageFileHolder.put(spriteName, f);
			this.previewLabel.update();
		};
		PictureLoader picLoader = new PictureLoader(GAP_WIDTH + x, GAP_WIDTH + 50, 128, 192, SCALE, fileProcessor, "Select " + spriteName + " sprite (128x192px and png-format)");
		picLoader.setText("Load " + spriteName);
		picLoader.setToolTipText("Click to load a sprite for the character's " + spriteName.toLowerCase() + " (128x192px in png-format).");
		return picLoader;
	}

}