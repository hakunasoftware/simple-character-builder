package simplecharacterbuilder.statgenerator;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.IntSupplier;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import lombok.Getter;
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
	
	private final List<StatButtonGroup> buttonGroups = new ArrayList<>();
	private final StatGenerator statGenerator;
	
	private JCheckBox virginCheckBox;
	
	RegularStatSelectionPanel(StatGenerator statGenerator, int xPos, int yPos) {
		this.statGenerator = statGenerator;
		
		this.setBounds(xPos, yPos, WIDTH, HEIGHT);
		this.setBorder(CharacterBuilderComponent.BORDER);
		this.setLayout(null);
		
		this.addOptionLabels();
		this.addRegStatButtons();
		this.addVirginCheckbox();
	}
	
	void setSelection(Stat stat, int selection) {
		if(stat.equals(Stat.BEAUTY)) {
			throw new IllegalArgumentException("Beauty can't be displayed on the RegStatPanel");
		}
		if(!stat.equals(Stat.SEX)) {
			getButtonGroup(stat).setSelection(selection);
			return;
		}
		if(selection == -1) {
			getButtonGroup(Stat.SEX).setSelection(0);
			if(!virginCheckBox.isSelected()) {
				virginCheckBox.setSelected(true);
			}
		} else {
			getButtonGroup(Stat.SEX).setSelection(selection);
			if(virginCheckBox.isSelected()) {
				virginCheckBox.setSelected(false);
			}
		}
	}
	
	int getSelection(Stat stat) {
		if(stat.equals(Stat.SEX) && virginCheckBox.isSelected()) {
			return -1;
		}
		return Integer.parseInt(getButtonGroup(stat).getSelection().getActionCommand());
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
	
	private StatButtonGroup getButtonGroup(Stat stat) {
		return buttonGroups.stream().filter(b -> b.getStat().equals(stat)).findFirst().get();
	}

	private void addRegStatButtons() {
		Stat.forRegStats(stat -> buildButtonGroup(stat));
	}
	
	private void buildButtonGroup(Stat regStat) {
		addStatNameLabelAndButtons(regStat, CONTENT_XPOS, CONTENT_YPOS + HEADLINE_OFFSET + VERTICAL_BUTTON_DISTANCE * buttonGroups.size());
	}
	
	private void addVirginCheckbox() {
		virginCheckBox = new JCheckBox("Virgin");
		virginCheckBox.setForeground(HEADLINE_COLOR);
		virginCheckBox.setFont(new Font(HEADLINE_FONT.getName(), HEADLINE_FONT.getStyle(), 11));
		
		int x = CONTENT_XPOS + STAT_NAME_LABEL_LENGTH + 12;
		int y = CONTENT_YPOS + HEADLINE_OFFSET + 6 * VERTICAL_BUTTON_DISTANCE + VERTICAL_VIRGIN_CHECKBOX_DISTANCE;
		virginCheckBox.setBounds(x, y, 70, 20);
		
		virginCheckBox.addItemListener(e -> disableSexButtons());
		virginCheckBox.addActionListener(StatButtonGroup.createDisplayActionListener(statGenerator, Stat.SEX, 
				() -> virginCheckBox.isSelected() ? -1 : getButtonGroup(Stat.SEX).getSelectionIndex()));
				
		this.add(virginCheckBox);
	}
	
	private void disableSexButtons() {
		Enumeration<AbstractButton> sexButtonsElements = getButtonGroup(Stat.SEX).getElements();
		while(sexButtonsElements.hasMoreElements()) {
			AbstractButton currentButton = sexButtonsElements.nextElement();
			currentButton.setEnabled(!currentButton.isEnabled());
		}
	}
	
	private void addStatNameLabelAndButtons(Stat stat, int x, int y) {
		this.add(createStatNameLabel(stat, x, y));
		buttonGroups.add(createRegStatButtonGroup(stat, x + STAT_NAME_LABEL_LENGTH + 12, y));
	}
	
	private JLabel createStatNameLabel(Stat stat, int x, int y) {
		JLabel label = new JLabel(stat.getName() + ":");
		label.setBounds(x, y, STAT_NAME_LABEL_LENGTH, 20);
		label.setVerticalAlignment(JLabel.CENTER);
		label.setHorizontalAlignment(JLabel.RIGHT);
		label.setFont(new Font(label.getFont().getName(), label.getFont().getStyle(), 12));
		return label;
	}
	
	private StatButtonGroup createRegStatButtonGroup(Stat stat, int x, int y) {
		StatButtonGroup buttonGroup = new StatButtonGroup(stat);
		for (int i = 0; i < BUTTON_COUNT; i++) {
			JRadioButton button = new JRadioButton();
			button.setBounds(x + (20 + HORIZONTAL_BUTTON_DISTANCE) * i, y + 1, 20, 20);
			button.setActionCommand(String.valueOf(i));
			button.addActionListener(StatButtonGroup.createDisplayActionListener(statGenerator, stat, 
					() -> Integer.valueOf(button.getActionCommand())));
			this.add(button);
			buttonGroup.add(button);
		}
		return buttonGroup;
	}
	
	@Getter
	static class StatButtonGroup extends ButtonGroup {
		private final Stat stat;
		StatButtonGroup(Stat stat) {
			this.stat = stat;
		}

		void setSelection(int selectionIndex) {
			Enumeration<AbstractButton> buttons = this.getElements();
			while(buttons.hasMoreElements()) {
				AbstractButton currentButton = buttons.nextElement();
				if(Integer.parseInt(currentButton.getActionCommand()) == selectionIndex) {
					currentButton.setSelected(true);
					return;
				}
			}
		}
		
		int getSelectionIndex() {
			Enumeration<AbstractButton> buttons = this.getElements();
			while(buttons.hasMoreElements()) {
				AbstractButton currentButton = buttons.nextElement();
				if(currentButton.isSelected()) {
					return Integer.parseInt(currentButton.getActionCommand());
				}
			}
			throw new IllegalArgumentException();
		}
		
		static ActionListener createDisplayActionListener(StatGenerator statGenerator, Stat stat, IntSupplier supplier) {
			return new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = supplier.getAsInt();
					int comparisonValue = statGenerator.getStatDisplayPanel().getComparisonValue(stat);
					if(selection != statGenerator.getStatCalculator().generateSelection(stat, comparisonValue)) {
						int generatedValue = statGenerator.getStatCalculator().generateStatFromSelection(stat, selection);
						statGenerator.getStatDisplayPanel().displayStat(stat, generatedValue);
					} else {
						statGenerator.getStatDisplayPanel().displayStat(stat, comparisonValue);
					}
				}
			};
		}
	}
}
