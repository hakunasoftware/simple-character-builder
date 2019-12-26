package simplecharacterbuilder.statgenerator;

import java.awt.Color;
import java.awt.Font;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import lombok.Builder;
import lombok.Data;
import simplecharacterbuilder.abstractview.CharacterBuilderComponent;

@SuppressWarnings("serial")
class RegularStatSelectionPanel extends JPanel {
	
	private static final int CONTENT_XPOS = 5;
	private static final int CONTENT_YPOS = 13;

	private static final int BUTTON_COUNT = 5;
	private static final int DEFAULT_BUTTON_SELECTION = 3;
	
	private static final int HORIZONTAL_BUTTON_DISTANCE = 15;
	private static final int VERTICAL_BUTTON_DISTANCE = 40;
	private static final int VERTICAL_VIRGIN_CHECKBOX_DISTANCE = 25;
	
	private static final int STAT_NAME_LABEL_LENGTH = 80;
	
	private static final int LABELS_HORIZONTAL_OFFSET = 78;
	private static final int LABELS_FIRST_DISTANCE = 74;
	private static final int LABELS_SECOND_DISTANCE = 65;
	
	static final int HEADLINE_OFFSET = 16;
	
	static final int WIDTH = 285;
	static final int HEIGHT = 2 * CONTENT_YPOS + HEADLINE_OFFSET + 6 * VERTICAL_BUTTON_DISTANCE + VERTICAL_VIRGIN_CHECKBOX_DISTANCE + 20;
	
	static final int HEADLINE_LENGTH = 80;
	static final Color HEADLINE_COLOR = new Color(120, 120, 120, 255);
	static final Font HEADLINE_FONT;
	static {
		JLabel label = new JLabel();
		HEADLINE_FONT = new Font(label.getFont().getName(), label.getFont().getStyle(), 10);
	}
	
	private ButtonGroup constitutionButtons;
	private ButtonGroup agilityButtons;
	private ButtonGroup strengthButtons;	
	private ButtonGroup intelligenceButtons;
	private ButtonGroup charismaButtons;
	private ButtonGroup obedienceButtons;
	private ButtonGroup sexButtons;
	
	private JCheckBox virginCheckBox;
	
	RegularStatSelectionPanel(int xPos, int yPos) {
		this.setBounds(xPos, yPos, WIDTH, HEIGHT);
		this.setBorder(CharacterBuilderComponent.BORDER);
		this.setLayout(null);
		
		this.addOptionLabels();
		this.addRegStatButtons();
		this.addVirginCheckbox();
	}
	
	/**
	 * Reads and returns the indices (from 1) of the selected buttons.
	 * @return the indices of the selected buttons. If the Virgin checkBox is selected, returns -1 for sex.
	 */
	RegularStatSelectionDTO getSelections() {
		int sexValue = virginCheckBox.isSelected() ? -1 : getValueFromButtonGroup(sexButtons);
		
		return RegularStatSelectionDTO.builder()
				.constitutionSelection( getValueFromButtonGroup(constitutionButtons))
				.agilitySelection(      getValueFromButtonGroup(agilityButtons))
				.strengthSelection(     getValueFromButtonGroup(strengthButtons))
				.intelligenceSelection( getValueFromButtonGroup(intelligenceButtons))
				.charismaSelection(     getValueFromButtonGroup(charismaButtons))
				.obedienceSelection(    getValueFromButtonGroup(obedienceButtons))
				.sexSelection(sexValue)
				.build();
	}
	
	void setSelection(RegularStatSelectionDTO regularStatSelectionDTO) {
		setSelectionForButtonGroup(constitutionButtons, regularStatSelectionDTO.getConstitutionSelection());
		setSelectionForButtonGroup(agilityButtons,      regularStatSelectionDTO.getAgilitySelection());
		setSelectionForButtonGroup(strengthButtons,     regularStatSelectionDTO.getStrengthSelection());
		setSelectionForButtonGroup(intelligenceButtons, regularStatSelectionDTO.getIntelligenceSelection());
		setSelectionForButtonGroup(charismaButtons,     regularStatSelectionDTO.getCharismaSelection());
		setSelectionForButtonGroup(obedienceButtons,    regularStatSelectionDTO.getObedienceSelection());
		
		int sexSelection = regularStatSelectionDTO.getSexSelection();
		if(sexSelection == -1) {
			setSelectionForButtonGroup(sexButtons, 0);
			if(!virginCheckBox.isSelected()) {
				virginCheckBox.setSelected(true);
			}
		} else {
			setSelectionForButtonGroup(sexButtons, regularStatSelectionDTO.getSexSelection());
		}
	}
	
	static void setSelectionForButtonGroup(ButtonGroup buttonGroup, int selectionIndex) {
		Enumeration<AbstractButton> buttons = buttonGroup.getElements();
		while(buttons.hasMoreElements()) {
			AbstractButton currentButton = buttons.nextElement();
			if(Integer.parseInt(currentButton.getActionCommand()) == selectionIndex) {
				currentButton.setSelected(true);
				return;
			}
		}
	}
	
	@Data
	@Builder
	static class RegularStatSelectionDTO {
		private int constitutionSelection;
		private int agilitySelection;
		private int strengthSelection;
		private int intelligenceSelection;
		private int charismaSelection;
		private int obedienceSelection;
		private int sexSelection;
	}
	
	private int getValueFromButtonGroup(ButtonGroup buttonGroup) {
		return Integer.parseInt(buttonGroup.getSelection().getActionCommand());
	}
	
	private void addOptionLabels() {
		int firstLabelX = CONTENT_XPOS + LABELS_HORIZONTAL_OFFSET;
		int secondLabelX = firstLabelX + LABELS_FIRST_DISTANCE;
		int thirdLabelX = secondLabelX + LABELS_SECOND_DISTANCE;
		
		this.add(new HeadlineLabel("Very Low", firstLabelX, CONTENT_YPOS));
		this.add(new HeadlineLabel("Average", secondLabelX, CONTENT_YPOS));
		this.add(new HeadlineLabel("Very High", thirdLabelX, CONTENT_YPOS));
	}
	
	private static class HeadlineLabel extends JLabel {
		private static final long serialVersionUID = -7313465132666155986L;
		public HeadlineLabel(String text, int x, int y) {
			this.setText(text);
			this.setBounds(x, y, HEADLINE_LENGTH, 15);
			this.setForeground(HEADLINE_COLOR);
			this.setVerticalAlignment(JLabel.CENTER);
			this.setFont(HEADLINE_FONT);
		}
	}


	private void addRegStatButtons() {
		constitutionButtons = buildButtonGroup("Constitution", 0);
		agilityButtons      = buildButtonGroup("Agility", 1);
		strengthButtons     = buildButtonGroup("Strength", 2);
		intelligenceButtons = buildButtonGroup("Intelligence", 3);
		charismaButtons     = buildButtonGroup("Charisma", 4);
		obedienceButtons    = buildButtonGroup("Obedience", 5);
		sexButtons          = buildButtonGroup("Sex", 6);
	}
	
	private ButtonGroup buildButtonGroup(String label, int count) {
		return new ButtonGroupBuilder(this, label)
				.location(CONTENT_XPOS, CONTENT_YPOS + HEADLINE_OFFSET + count * VERTICAL_BUTTON_DISTANCE)
				.buildAndAdd();
	}
	
	private void addVirginCheckbox() {
		virginCheckBox = new JCheckBox("Virgin");
		virginCheckBox.setForeground(HEADLINE_COLOR);
		virginCheckBox.setFont(new Font(HEADLINE_FONT.getName(), HEADLINE_FONT.getStyle(), 11));
		
		int x = CONTENT_XPOS + STAT_NAME_LABEL_LENGTH + 12;
		int y = CONTENT_YPOS + HEADLINE_OFFSET + 6 * VERTICAL_BUTTON_DISTANCE + VERTICAL_VIRGIN_CHECKBOX_DISTANCE;
		virginCheckBox.setBounds(x, y, 70, 20);
		
		virginCheckBox.addItemListener(e -> disableSexButtons());
		this.add(virginCheckBox);
	}
	
	private void disableSexButtons() {
		Enumeration<AbstractButton> sexButtonsElements = sexButtons.getElements();
		while(sexButtonsElements.hasMoreElements()) {
			AbstractButton currentButton = sexButtonsElements.nextElement();
			currentButton.setEnabled(!currentButton.isEnabled());
		}
	}
	
	
	private static class ButtonGroupBuilder {
		private JPanel panel;
		private String statName;
		
		private int x;
		private int y;

		ButtonGroupBuilder(JPanel panel, String statName) {
			this.panel = panel;
			this.statName = statName;
		}

		ButtonGroupBuilder location(int x, int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		ButtonGroup buildAndAdd() {
			this.panel.add(createStatNameLabel(statName, x, y));
			
			int shiftedX = this.x + STAT_NAME_LABEL_LENGTH + 12;
			
			ButtonGroup buttonGroup = new ButtonGroup();
			
			for (int i = 0; i < BUTTON_COUNT; i++) {
				JRadioButton button = new JRadioButton();
				button.setBounds(shiftedX + (20 + HORIZONTAL_BUTTON_DISTANCE) * i, this.y + 1, 20, 20);
				button.setActionCommand(String.valueOf(i));
				
				this.panel.add(button);
				buttonGroup.add(button);
				
				if(i == DEFAULT_BUTTON_SELECTION - 1) {
					button.setSelected(true);
				}
			}
			
			return buttonGroup;
		}

		private JLabel createStatNameLabel(String statName, int x, int y) {
			JLabel label = new JLabel(statName + ":");
			label.setBounds(x, y, STAT_NAME_LABEL_LENGTH, 20);
			label.setVerticalAlignment(JLabel.CENTER);
			label.setHorizontalAlignment(JLabel.RIGHT);
			label.setFont(new Font(label.getFont().getName(), label.getFont().getStyle(), 12));
			return label;
		}
	}
}
