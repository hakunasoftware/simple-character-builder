package simplecharacterbuilder.characterbuilder.core;

import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.MAINPANEL_HEIGHT;
import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.MAINPANEL_WIDTH;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;

import simplecharacterbuilder.characterbuilder.maincomponents.bodysprites.SpriteLoaderMainComponent;
import simplecharacterbuilder.characterbuilder.maincomponents.personaldata.PersonalDataMainComponent;
import simplecharacterbuilder.characterbuilder.maincomponents.various.CombatClassSelectorMainComponent;
import simplecharacterbuilder.characterbuilder.maincomponents.various.SocialMainComponent;
import simplecharacterbuilder.characterbuilder.maincomponents.various.TraitSelectorMainComponent;
import simplecharacterbuilder.characterbuilder.util.holder.BodyPartRepository;
import simplecharacterbuilder.characterbuilder.util.holder.DrawIndexRepository;
import simplecharacterbuilder.characterbuilder.util.holder.JFileChooserPool;
import simplecharacterbuilder.characterbuilder.util.holder.TraitRepository;
import simplecharacterbuilder.common.resourceaccess.ConfigReaderRepository;
import simplecharacterbuilder.common.statgenerator.StatGenerator;
import simplecharacterbuilder.common.uicomponents.ApplicationFrame;
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent;

public class CharacterBuilderStart {
	private static final int WIDTH = MAINPANEL_WIDTH;
	private static final int HEIGHT = MAINPANEL_HEIGHT;

	private static final List<CharacterBuilderComponent> COMPONENTS = new ArrayList<>();
	static {
		ConfigReaderRepository.init();
		TraitRepository.init();
		
		COMPONENTS.add(new PersonalDataMainComponent());
		COMPONENTS.add(StatGenerator.createInstance(false));
		COMPONENTS.add(new SpriteLoaderMainComponent());
		COMPONENTS.add(new TraitSelectorMainComponent());
		COMPONENTS.add(new SocialMainComponent());
		if(CombatClassSelectorMainComponent.SELECTIONS_COUNT > 0) {
			COMPONENTS.add(new CombatClassSelectorMainComponent());
		}
		
		CharacterBuilderControlPanel controlPanel = CharacterBuilderControlPanel.getInstance();
		controlPanel.init(COMPONENTS);
		COMPONENTS.add(controlPanel);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		JFileChooserPool.init();
		DrawIndexRepository.init();
		BodyPartRepository.init();
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> new ApplicationFrame(WIDTH, HEIGHT, "SB2R CharacterBuilder", COMPONENTS));
	}
}