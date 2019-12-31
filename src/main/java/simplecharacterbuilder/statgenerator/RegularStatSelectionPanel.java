package simplecharacterbuilder.statgenerator;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import simplecharacterbuilder.abstractview.CharacterBuilderComponent;

@SuppressWarnings("serial")
class RegularStatSelectionPanel extends JPanel {
	
	private static final int CONTENT_XPOS = 5;
	private static final int CONTENT_YPOS = 13;

	private static final int BUTTON_COUNT = 5;
	
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
	
	private final List<RegStatButtonLine> buttonLines = new ArrayList<>();
	
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
	Map<Stat, Integer> getSelections() {
		Map<Stat, Integer> regStats = new HashMap<>();
		Stat.getAll().stream()
			.filter(stat -> !stat.equals(Stat.BEAUTY))
			.forEach(stat -> regStats.put(stat, getSelectionFromButtons(stat)));
		return regStats;
	}
	
	void setSelection(Map<Stat, Integer> selections) {
		Stat.getRegStats().stream().filter(stat -> !stat.equals(Stat.SEX))
			.forEach(stat -> setSelectionForButtonGroup(getButtonLine(stat).getButtonGroup(), selections.get(stat)));
		
		int sexSelection = selections.get(Stat.SEX);
		if(sexSelection == -1) {
			setSelectionForButtonGroup(getButtonLine(Stat.SEX).getButtonGroup(), 0);
			if(!virginCheckBox.isSelected()) {
				virginCheckBox.setSelected(true);
			}
		} else {
			setSelectionForButtonGroup(getButtonLine(Stat.SEX).getButtonGroup(), sexSelection);
			if(virginCheckBox.isSelected()) {
				virginCheckBox.setSelected(false);
			}
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
	
	private int getSelectionFromButtons(Stat stat) {
		if(stat.equals(Stat.SEX) && virginCheckBox.isSelected()) {
			return -1;
		}
		return Integer.parseInt(getButtonLine(stat).getButtonGroup().getSelection().getActionCommand());
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
		Stat.forRegStats(stat -> buildButtonGroup(stat));
	}
	
	private RegStatButtonLine getButtonLine(Stat stat) {
		return buttonLines.stream().filter(b -> b.regStat.equals(stat)).findFirst().get();
	}
	
	private void buildButtonGroup(Stat regStat) {
		new RegStatButtonLine(regStat)
			.location(CONTENT_XPOS, CONTENT_YPOS + HEADLINE_OFFSET + VERTICAL_BUTTON_DISTANCE * buttonLines.size())
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
		Enumeration<AbstractButton> sexButtonsElements = getButtonLine(Stat.SEX).getButtonGroup().getElements();
		while(sexButtonsElements.hasMoreElements()) {
			AbstractButton currentButton = sexButtonsElements.nextElement();
			currentButton.setEnabled(!currentButton.isEnabled());
		}
	}
	
	
	private class RegStatButtonLine {
		private final Stat regStat;
		private final ButtonGroup buttonGroup;
		
		private int x;
		private int y;

		RegStatButtonLine(Stat regStat) {
			this.regStat = regStat;
			
			this.buttonGroup = new ButtonGroup();
		}

		RegStatButtonLine location(int x, int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		RegStatButtonLine buildAndAdd() {
			RegularStatSelectionPanel.this.add(createStatNameLabel(regStat, x, y));
			
			int shiftedX = this.x + STAT_NAME_LABEL_LENGTH + 12;
			
			for (int i = 0; i < BUTTON_COUNT; i++) {
				JRadioButton button = new JRadioButton();
				button.setBounds(shiftedX + (20 + HORIZONTAL_BUTTON_DISTANCE) * i, this.y + 1, 20, 20);
				button.setActionCommand(String.valueOf(i));
				
				RegularStatSelectionPanel.this.add(button);
				buttonGroup.add(button);
			}
			buttonLines.add(this);
			return this;
		}

		private JLabel createStatNameLabel(Stat stat, int x, int y) {
			JLabel label = new JLabel(stat.getName() + ":");
			label.setBounds(x, y, STAT_NAME_LABEL_LENGTH, 20);
			label.setVerticalAlignment(JLabel.CENTER);
			label.setHorizontalAlignment(JLabel.RIGHT);
			label.setFont(new Font(label.getFont().getName(), label.getFont().getStyle(), 12));
			return label;
		}
		
		ButtonGroup getButtonGroup() {
			return this.buttonGroup;
		}
	}
}
