package simplecharacterbuilder.characterbuilder.maincomponents.bodysprites;

import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.CONTROLPANEL_WIDTH;
import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.GAP_WIDTH;
import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.MAINPANEL_WIDTH;

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
	
	SpriteTemplateInfoSelector (int yPos) {
		super(GAP_WIDTH, yPos, WIDTH, HEIGHT);
		this.add(UIComponentFactory.createFormattedLabel("Body Type:", 1, 8, 70, JLabel.CENTER));
		String[] bodyTypes = new String[] {"Flat-Chested", "Small Breasts", "Medium Breasts", "Large Breasts", "XL Breasts"};
		this.bodyTypeComboBox = UIComponentFactory.createComboBox(78, 10, 115, "<html>Select the body type, i.e. the template that was used for the sprite.<br/>(Flat-Chested uses the Small Breasts and XL Breasts the Large Breasts template)</html>", bodyTypes);
		this.add(bodyTypeComboBox);
		this.skinCheckBox = UIComponentFactory.createCheckBox("Dark Skin", 200, 10, 80, "Check this if you used the Dark Skin version of the body template. For other skin colors, contact the devs.");
		this.add(skinCheckBox);
	}
	
	String getBodyType() {
		return (String) bodyTypeComboBox.getSelectedItem();
	}
	
	String getSkinColor() {
		return skinCheckBox.isSelected() ? "Dark" : "Light";
	}


}
