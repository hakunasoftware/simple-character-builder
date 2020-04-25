package simplecharacterbuilder.characterbuilder.personaldata;

import static simplecharacterbuilder.util.CharacterBuilderComponent.CONTROLPANEL_WIDTH;
import static simplecharacterbuilder.util.CharacterBuilderComponent.GAP_WIDTH;
import static simplecharacterbuilder.util.CharacterBuilderComponent.MAINPANEL_HEIGHT;
import static simplecharacterbuilder.util.CharacterBuilderComponent.MAINPANEL_WIDTH;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import simplecharacterbuilder.util.CharacterBuilderComponent;
import simplecharacterbuilder.util.ConfigReader;
import simplecharacterbuilder.util.ContentPanel;
import simplecharacterbuilder.util.GameFileRepository;

@SuppressWarnings("serial")
class PersonalDataPanel extends ContentPanel {

	static final int WIDTH = MAINPANEL_WIDTH - CONTROLPANEL_WIDTH - 3 * GAP_WIDTH;
	static final int HEIGHT = MAINPANEL_HEIGHT - 2 * GAP_WIDTH;

	private static final String FIRST_NAME = "First Name";
	private static final String MIDDLE_NAME = "Middle Name";
	private static final String LAST_NAME = "Last Name";
	private static final String NICKNAME = "Nickname";
	private static final String NICKNAME_PERCENTAGE = "Percentage";
	private static final String ASIAN = "Asian";
	private static final String RACE = "Race";
	private static final String QUEST = "Quest";
	private static final String LIKES = "Likes";
	private static final String ACTOR_TYPE = "Actor Type";
	private static final String MAINTENANCE = "Maintenance";
	private static final String SEX_WORK = "Does Sex Work";

	private static final int YPOS_FIRST_NAME = 17;
	private static final int HORIZONTAL_GAP = 39;
	private static final int COMPONENT_HEIGHT = 22;

	private static final int YPOS_MIDDLE_NAME = YPOS_FIRST_NAME + HORIZONTAL_GAP;
	private static final int YPOS_LAST_NAME = YPOS_MIDDLE_NAME + HORIZONTAL_GAP;
	private static final int YPOS_NICKNAME = YPOS_LAST_NAME + HORIZONTAL_GAP;
	private static final int YPOS_ASIAN_CHECKBOX = YPOS_NICKNAME + HORIZONTAL_GAP;
	private static final int YPOS_LIKES_OPTIONS = YPOS_ASIAN_CHECKBOX + HORIZONTAL_GAP;
	private static final int YPOS_QUEST_CHECKBOX = YPOS_LIKES_OPTIONS + HORIZONTAL_GAP;
	private static final int YPOS_HIRED_STAFF_OPTIONS = YPOS_QUEST_CHECKBOX + HORIZONTAL_GAP;

	private final Map<String, JTextField> textFields = new HashMap<>();

	private final Map<String, JCheckBox> checkBoxes = new HashMap<>();
	private final Map<String, JComboBox<String>> comboBoxes = new HashMap<>();

	PersonalDataPanel() {
		super(GAP_WIDTH, GAP_WIDTH, WIDTH, HEIGHT);

		this.addTextField(FIRST_NAME, YPOS_FIRST_NAME, "Add the character's first name [required]");
		this.addTextField(MIDDLE_NAME, YPOS_MIDDLE_NAME, "Add the character's middle name [optional]");
		this.addTextField(LAST_NAME, YPOS_LAST_NAME,
				"Add the character's last name [optional, but should always be set if the character has one]");
		this.addNicknameOptions();

		this.addAsianCheckBox();
		this.addRaceOptions();
		this.addLikesOptions();

		this.addSlaveMercOptions();
		this.addQuestCheckBox();
		this.addHiredStaffOptions();
	}

	private JTextField addTextField(String name, int yPos, String tooltip) {
		return addTextField(name, yPos, WIDTH - 115, tooltip);
	}

	private JTextField addTextField(String name, int yPos, int width, String tooltip) {
		createFormattedLabel(name + ":", 0, yPos, 90, COMPONENT_HEIGHT);
		return createFormattedTextField(name, 100, yPos, width, COMPONENT_HEIGHT, tooltip);
	}

	private void addNicknameOptions() {
		createFormattedLabel(NICKNAME + ":", 0, YPOS_NICKNAME, 70, COMPONENT_HEIGHT);
		createFormattedTextField(NICKNAME, 75, YPOS_NICKNAME, 105, COMPONENT_HEIGHT,
				"Add a (canon) nickname [optional]");
		createFormattedLabel("(Usage: ", 180, YPOS_NICKNAME, 50, COMPONENT_HEIGHT);

		String percentageTooltip = "Determines how often the nickname is used instead of the real name.";
		JTextField percentageField = createFormattedTextField(NICKNAME_PERCENTAGE, 232, YPOS_NICKNAME, 27,
				COMPONENT_HEIGHT, percentageTooltip);
		percentageField.setText("0");
		percentageField.setHorizontalAlignment(JLabel.RIGHT);

		createFormattedLabel("%)", 260, YPOS_NICKNAME, 13, COMPONENT_HEIGHT).setHorizontalAlignment(JLabel.LEFT);
	}

	private void addRaceOptions() {
		createFormattedLabel(RACE + ":", 0, YPOS_ASIAN_CHECKBOX, 70, COMPONENT_HEIGHT);

		String tooltip = "Select the race that best fits the character. If you can't find a fitting one, contact the devs.";
		List<String> bodyTypeList =  new ConfigReader(GameFileRepository.getRacesList()).readAllValues();
		bodyTypeList.sort((a, b) -> a.equalsIgnoreCase("Human") ? -1 : b.equalsIgnoreCase("Human") ? 1 : a.compareTo(b));
		String[] bodyTypes = Arrays.copyOf(bodyTypeList.toArray(), bodyTypeList.size(), String[].class);
		createComboBox(RACE, bodyTypes, 78, YPOS_ASIAN_CHECKBOX, 110, tooltip);
	}

	private void addAsianCheckBox() {
		String tooltip = "For Asian characters the last name will be displayed in front of the first name.";
		createCheckBox(ASIAN, WIDTH - 90, YPOS_ASIAN_CHECKBOX, 80, tooltip);
	}

	private void addLikesOptions() {
		createFormattedLabel(LIKES + ":", 0, YPOS_LIKES_OPTIONS, 70, COMPONENT_HEIGHT);

		String tooltip = "Select the sexual preference of the character";
		String[] bodyTypes = new String[] { "Males", "Females", "Both", "Neither" };
		createComboBox(LIKES, bodyTypes, 78, YPOS_LIKES_OPTIONS, 110, tooltip);
	}

	private void addQuestCheckBox() {
		String tooltip = "Check this box if the character is a quest character. She will not appear on the market.";
		createCheckBox(QUEST, WIDTH - 70, YPOS_QUEST_CHECKBOX, 60, tooltip);
	}

	private void addSlaveMercOptions() {
		createFormattedLabel(ACTOR_TYPE + ":", 0, YPOS_QUEST_CHECKBOX, 70, COMPONENT_HEIGHT);

		String tooltip = "Select whether the character is a slave or hired staff. This can also be dependant on the outcome of a quest.";
		String[] bodyTypes = new String[] { "Slave", "Hired Staff", "Decided On Quest" };
		createComboBox(ACTOR_TYPE, bodyTypes, 78, YPOS_QUEST_CHECKBOX, 130, tooltip);
	}

	private void addHiredStaffOptions() {
		createFormattedLabel(MAINTENANCE + ":", 13, YPOS_HIRED_STAFF_OPTIONS, 80, COMPONENT_HEIGHT);

		JTextField textField = createFormattedTextField(MAINTENANCE, 100, YPOS_HIRED_STAFF_OPTIONS, 40,
				COMPONENT_HEIGHT, "Add the maintenance fee/salary of this character.");
		textField.setText("1000");
		textField.setHorizontalAlignment(JLabel.RIGHT);

		String tooltip = "Check this box if the character accepts tasks that involve sex.";
		createCheckBox(SEX_WORK, WIDTH - 130, YPOS_HIRED_STAFF_OPTIONS, 115, tooltip);
	}

	private JLabel createFormattedLabel(String text, int xPos, int yPos, int width, int height) {
		JLabel label = new JLabel(text);
		label.setBounds(xPos, yPos, width, height);
		label.setHorizontalAlignment(JLabel.RIGHT);
		label.setVerticalAlignment(JLabel.CENTER);
		this.add(label);
		return label;
	}

	private JTextField createFormattedTextField(String name, int xPos, int yPos, int width, int height,
			String tooltip) {
		JTextField textField = new JTextField();
		textField.setBounds(xPos, yPos, width, height);
		textField.setBorder(CharacterBuilderComponent.BORDER);
		textField.setBackground(new Color(255, 255, 255, 245));
		textField.setToolTipText(tooltip);
		this.textFields.put(name, textField);
		this.add(textField);
		return textField;
	}

	private JCheckBox createCheckBox(String name, int xPos, int yPos, int width, String tooltip) {
		JCheckBox checkBox = new JCheckBox(name);
		checkBox.setLocation(xPos, yPos);
		checkBox.setBounds(xPos, yPos, width, 20);
		checkBox.setToolTipText(tooltip);
		this.checkBoxes.put(name, checkBox);
		this.add(checkBox);
		return checkBox;
	}

	private JComboBox<String> createComboBox(String name, String[] options, int xPos, int yPos, int width,
			String tooltip) {
		JComboBox<String> comboBox = new JComboBox<>(options);
		comboBox.setSelectedIndex(0);
		comboBox.setBounds(xPos, yPos, width, 20);
		comboBox.setToolTipText(tooltip);
		this.comboBoxes.put(name, comboBox);
		this.add(comboBox);
		return comboBox;
	}

	public Map<String, JTextField> getTextFields() {
		return textFields;
	}

	public Map<String, JCheckBox> getCheckBoxes() {
		return checkBoxes;
	}

	public Map<String, JComboBox<String>> getComboBoxes() {
		return comboBoxes;
	}

}