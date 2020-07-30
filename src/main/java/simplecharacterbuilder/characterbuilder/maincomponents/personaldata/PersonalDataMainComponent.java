package simplecharacterbuilder.characterbuilder.maincomponents.personaldata;

import static simplecharacterbuilder.characterbuilder.maincomponents.personaldata.PersonalDataPanel.ASIAN;
import static simplecharacterbuilder.characterbuilder.maincomponents.personaldata.PersonalDataPanel.FIRST_NAME;
import static simplecharacterbuilder.characterbuilder.maincomponents.personaldata.PersonalDataPanel.LAST_NAME;
import static simplecharacterbuilder.characterbuilder.maincomponents.personaldata.PersonalDataPanel.LIKES;
import static simplecharacterbuilder.characterbuilder.maincomponents.personaldata.PersonalDataPanel.MAINTENANCE;
import static simplecharacterbuilder.characterbuilder.maincomponents.personaldata.PersonalDataPanel.MIDDLE_NAME;
import static simplecharacterbuilder.characterbuilder.maincomponents.personaldata.PersonalDataPanel.NICKNAME;
import static simplecharacterbuilder.characterbuilder.maincomponents.personaldata.PersonalDataPanel.NICKNAME_PERCENTAGE;
import static simplecharacterbuilder.characterbuilder.maincomponents.personaldata.PersonalDataPanel.QUEST;
import static simplecharacterbuilder.characterbuilder.maincomponents.personaldata.PersonalDataPanel.RACE;
import static simplecharacterbuilder.characterbuilder.maincomponents.personaldata.PersonalDataPanel.SEX_WORK;
import static simplecharacterbuilder.characterbuilder.util.transform.ValueFormatter.formatBoolean;
import static simplecharacterbuilder.characterbuilder.util.transform.ValueFormatter.formatLikes;
import static simplecharacterbuilder.characterbuilder.util.transform.ValueFormatter.formatNickname;
import static simplecharacterbuilder.characterbuilder.util.transform.ValueFormatter.nullEmptyString;

import java.math.BigInteger;
import java.util.Map;

import javax.swing.JTextField;

import simplecharacterbuilder.characterbuilder.core.CharacterBuilderControlPanel;
import simplecharacterbuilder.characterbuilder.util.holder.ImageFileHolder;
import simplecharacterbuilder.characterbuilder.util.holder.ApplicationFrameHolder;
import simplecharacterbuilder.characterbuilder.util.holder.FranchiseCache;
import simplecharacterbuilder.characterbuilder.util.transform.ValueFormatter;
import simplecharacterbuilder.characterbuilder.util.ui.PictureLoader;
import simplecharacterbuilder.characterbuilder.util.ui.PictureLoader.FileProcessor;
import simplecharacterbuilder.common.generated.Actor;
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.CharacterBuilderMainComponent;

/**
 * This needs to be the first MainComponent!
 */
public class PersonalDataMainComponent extends CharacterBuilderMainComponent {
	private static final int WIDTH_SOURCE_PANEL = CONTROLPANEL_WIDTH;
	private static final int HEIGHT_SOURCE_PANEL = 115;
	private static final int XPOS_SOURCE_PANEL = MAINPANEL_WIDTH - CONTROLPANEL_WIDTH - GAP_WIDTH;
	private static final int YPOS_SOURCE_PANEL = MAINPANEL_HEIGHT - CONTROLPANEL_HEIGHT - HEIGHT_SOURCE_PANEL - 2 * GAP_WIDTH;
	private static final int WIDTH_PICTURE_LOADER = 105;
	private static final int XPOS_PICTURE_LOADER = MAINPANEL_WIDTH - GAP_WIDTH - (CONTROLPANEL_WIDTH + WIDTH_PICTURE_LOADER) / 2;
	
	private final PersonalDataPanel personalDataPanel = new PersonalDataPanel();
	private final PictureLoader pictureLoader;
	{
		FileProcessor fileProcessor = f -> {
			CharacterBuilderControlPanel.getInstance().setPortrait(f.getAbsolutePath());
			ImageFileHolder.putPortrait(f);
		};
		pictureLoader = new PictureLoader(XPOS_PICTURE_LOADER, GAP_WIDTH, 200, 200, WIDTH_PICTURE_LOADER / 200.0, fileProcessor, "Select Portrait (200x200px and png-format)");
	}
	private final SourcePanel sourcePanel = new SourcePanel(XPOS_SOURCE_PANEL, YPOS_SOURCE_PANEL, WIDTH_SOURCE_PANEL, HEIGHT_SOURCE_PANEL);
	
	public PersonalDataMainComponent() {
		this.mainPanel.add(personalDataPanel);
		this.mainPanel.add(pictureLoader);
		this.mainPanel.add(sourcePanel);

		pictureLoader.setText("Load Portrait");
		pictureLoader.setToolTipText("Click to load a portrait for the character (required). Portraits must be 200x200 pixel and in png-format.");
	}

	@Override
	public void setValues(Actor actor) {
		actor.setName(getSelectedNames());
		actor.setQuest(formatBoolean(personalDataPanel.getCheckBoxes().get(QUEST).isSelected()));
		actor.setLikes(formatLikes((String) personalDataPanel.getComboBoxes().get(LIKES).getSelectedItem()));
		setTypeSettings(actor);
		setRaceSelection(actor);
		setSourceSelection(actor);
	}
	
	@Override
	public void enable() {
		super.enable();
		ApplicationFrameHolder.setApplicationFrameTitle("Personal Data");
	}
	
	@Override
	public void disable() {
		super.disable();
		FranchiseCache.setFranchise(sourcePanel.getSelectedFranchise());
	}

	private void setTypeSettings(Actor actor) {
		String actorType = (String) personalDataPanel.getComboBoxes().get(PersonalDataPanel.ACTOR_TYPE).getSelectedItem();
		actor.setType(actorType);
		if(!"Slave".equals(actorType)) {
			actor.setMaintenance(BigInteger.valueOf(Long.valueOf(personalDataPanel.getTextFields().get(MAINTENANCE).getText())));
			if(!personalDataPanel.getCheckBoxes().get(SEX_WORK).isSelected()) {
				Actor.Jobs jobs = new Actor.Jobs();
				jobs.setReject("Sex");
				actor.setJobs(jobs);
			}
		}
	}

	private Actor.Name getSelectedNames(){
		Map<String, JTextField> personalDataTextfields = personalDataPanel.getTextFields();
		Actor.Name name = new Actor.Name();
		name.setFirst(nullEmptyString(personalDataTextfields.get(FIRST_NAME).getText()));
		name.setMiddle(nullEmptyString(personalDataTextfields.get(MIDDLE_NAME).getText()));
		name.setLast(nullEmptyString(personalDataTextfields.get(LAST_NAME).getText()));
		name.setAsian(formatBoolean(personalDataPanel.getCheckBoxes().get(ASIAN).isSelected()));
		name.setNick(formatNickname(personalDataTextfields.get(NICKNAME).getText(), personalDataTextfields.get(NICKNAME_PERCENTAGE).getText()));
		return name;
	}
	
	private void setRaceSelection(Actor actor) {
		if(actor.getBody() == null) {
			actor.setBody(new Actor.Body());
		}
		actor.getBody().setRace((String) personalDataPanel.getComboBoxes().get(RACE).getSelectedItem());
	}
	
	private void setSourceSelection(Actor actor) {
		String selectedFranchise = sourcePanel.getSelectedFranchise();
		if(ValueFormatter.isEmpty(selectedFranchise)) {
			return;
		}
		Actor.Source source = new Actor.Source();
		source.setFranchise(ValueFormatter.nullEmptyString(selectedFranchise));
		source.setInstallment(ValueFormatter.nullEmptyString(sourcePanel.getSelectedInstallment()));
		source.setType(ValueFormatter.formatSourceType(sourcePanel.getPrimaryType(), sourcePanel.getSecondaryType()));
		actor.setSource(source);
	}
	
}
