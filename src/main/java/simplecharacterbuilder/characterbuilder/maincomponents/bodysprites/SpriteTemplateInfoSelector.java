package simplecharacterbuilder.characterbuilder.maincomponents.bodysprites;

import static simplecharacterbuilder.characterbuilder.maincomponents.bodysprites.SpriteLoaderMainComponent.FLAT_CHESTED;
import static simplecharacterbuilder.characterbuilder.maincomponents.bodysprites.SpriteLoaderMainComponent.LARGE_BREASTS;
import static simplecharacterbuilder.characterbuilder.maincomponents.bodysprites.SpriteLoaderMainComponent.MEDIUM_BREASTS;
import static simplecharacterbuilder.characterbuilder.maincomponents.bodysprites.SpriteLoaderMainComponent.SMALL_BREASTS;
import static simplecharacterbuilder.characterbuilder.maincomponents.bodysprites.SpriteLoaderMainComponent.XL_BREASTS;
import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.CONTROLPANEL_WIDTH;
import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.GAP_WIDTH;
import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.MAINPANEL_WIDTH;

import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import simplecharacterbuilder.characterbuilder.util.ui.UIComponentFactory;
import simplecharacterbuilder.common.uicomponents.ContentPanel;

@SuppressWarnings("serial")
class SpriteTemplateInfoSelector extends ContentPanel {
	static final int WIDTH = MAINPANEL_WIDTH - CONTROLPANEL_WIDTH - 3 * GAP_WIDTH;
	static final int HEIGHT = 40;
	
	private final JComboBox<String> bodyTypeComboBox;
	private final JCheckBox skinCheckBox;
	
	SpriteTemplateInfoSelector (int yPos, ActionListener changeListener) {
		super(GAP_WIDTH, yPos, WIDTH, HEIGHT);
		this.add(UIComponentFactory.createFormattedLabel("Body Type:", 1, 8, 70, JLabel.CENTER));
		String[] bodyTypes = new String[] {FLAT_CHESTED, SMALL_BREASTS, MEDIUM_BREASTS, LARGE_BREASTS, XL_BREASTS};
		this.bodyTypeComboBox = UIComponentFactory.createComboBox(78, 10, 115, "<html>Select the body type, i.e. the template that was used for the sprite.<br/>(Flat-Chested uses the Small Breasts and XL Breasts the Large Breasts template)</html>", bodyTypes);
		this.bodyTypeComboBox.addActionListener(changeListener);
		this.add(bodyTypeComboBox);
		this.skinCheckBox = UIComponentFactory.createCheckBox("Dark Skin", 200, 10, 80, "Check this if you used the Dark Skin version of the body template. For other skin colors, contact the devs.");
		this.skinCheckBox.addActionListener(changeListener);
		this.add(skinCheckBox);
	}
	
	String getBodyType() {
		return (String) bodyTypeComboBox.getSelectedItem();
	}
	
	boolean isDarkSkinned() {
		return skinCheckBox.isSelected();
	}

}
