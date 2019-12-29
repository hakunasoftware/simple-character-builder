package simplecharacterbuilder.statgenerator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import simplecharacterbuilder.abstractview.CharacterBuilderComponent;
import simplecharacterbuilder.statgenerator.RegularStatSelectionPanel.RegularStatSelectionDTO;

@SuppressWarnings("serial")
class StatDisplayPanel extends JPanel {

	static final int WIDTH = 80;
	static final int HEIGHT = BeautySelectionPanel.HEIGHT;

	private static final int VERTICAL_DISPLAY_DISTANCE = 28;

	private static final int TOP_OFFSET = 3;
	private static final int LEFT_OFFSET = 8;

	private static final String TEXTFIELD_REGEX = "^[0-9]{1,3}$";

	private final RegularStatSelectionPanel regStatSelectionPanel;
	private final BeautySelectionPanel beautySelectionPanel;
	private final StatCalculator statCalculator;

	private StatDisplay conDisplay;
	private StatDisplay agiDisplay;
	private StatDisplay strDisplay;
	private StatDisplay intDisplay;
	private StatDisplay chaDisplay;
	private StatDisplay obeDisplay;
	private StatDisplay sexDisplay;
	private StatDisplay beaDisplay;

	StatDisplayPanel(int xPos, int yPos, StatCalculator statCalculator, RegularStatSelectionPanel regularStatSelectionPanel, BeautySelectionPanel beautySelectionPanel) {
		this.regStatSelectionPanel = regularStatSelectionPanel;
		this.beautySelectionPanel  = beautySelectionPanel;
		this.statCalculator        = statCalculator;

		this.setBounds(xPos, yPos, WIDTH, HEIGHT);
		this.setBorder(CharacterBuilderComponent.BORDER);
		this.setLayout(null);

		this.addStatOutput(LEFT_OFFSET, TOP_OFFSET);
		this.addDisplayLock(LEFT_OFFSET + 1, 2 * TOP_OFFSET + 8 * VERTICAL_DISPLAY_DISTANCE);

		this.setDefaultValues();
	}

	void displayStats(StatDTO statDTO) {
		displayValue(conDisplay, statDTO.getConstitution());
		displayValue(agiDisplay, statDTO.getAgility());
		displayValue(strDisplay, statDTO.getStrength());
		displayValue(intDisplay, statDTO.getIntelligence());
		displayValue(chaDisplay, statDTO.getCharisma());
		displayValue(beaDisplay, statDTO.getBeauty());
		displayValue(sexDisplay, statDTO.getSex());
		displayValue(obeDisplay, statDTO.getObedience());

		displayStatsOnSelectionPanels();
	}

	private void displayStatsOnSelectionPanels() {
		beautySelectionPanel .setSelection(statCalculator.generateBeautySelection(beaDisplay.getValue()));
		regStatSelectionPanel.setSelection(statCalculator.generateRegularStatSelectionDTO(getStats()));
	}

	void displaySelectedStats() {
		RegularStatSelectionDTO regularStatSelectionDTO = regStatSelectionPanel.getSelections();
		int beautySelection = beautySelectionPanel.getSelection();
		displayStats(statCalculator.generateStats(regularStatSelectionDTO, beautySelection));
	}

	private void displayValue(StatDisplay statDisplay, int value) {
		statDisplay.setValue(Math.min(value, 999));
	}

	StatDTO getStats() {
		return StatDTO.builder()
				.constitution(conDisplay.getValue())
				.agility(agiDisplay.getValue())
				.strength(strDisplay.getValue())
				.intelligence(intDisplay.getValue())
				.charisma(chaDisplay.getValue())
				.beauty(beaDisplay.getValue())
				.sex(sexDisplay.getValue())
				.obedience(obeDisplay.getValue())
				.build();
	}
	
	List<String> getEvaluationWarnings(){
		List<String> warnings = new ArrayList<>();
		warnings.addAll(conDisplay.getEvaluationWarnings());
		warnings.addAll(agiDisplay.getEvaluationWarnings());
		warnings.addAll(strDisplay.getEvaluationWarnings());
		warnings.addAll(intDisplay.getEvaluationWarnings());
		warnings.addAll(chaDisplay.getEvaluationWarnings());
		warnings.addAll(beaDisplay.getEvaluationWarnings());
		warnings.addAll(sexDisplay.getEvaluationWarnings());
		warnings.addAll(obeDisplay.getEvaluationWarnings());
		return warnings;
	}

	private void addStatOutput(int x, int y) {
		conDisplay = createStatDisplay("CON", 0);
		agiDisplay = createStatDisplay("AGI", 1);
		strDisplay = createStatDisplay("STR", 2);
		intDisplay = createStatDisplay("INT", 3);
		chaDisplay = createStatDisplay("CHA", 4);
		obeDisplay = createStatDisplay("OBE", 5);
		sexDisplay = createStatDisplay("SEX", 6);
		beaDisplay = createStatDisplay("BEA", 7);
	}

	private StatDisplay createStatDisplay(String statName, int count) {
		return new StatDisplay(this, statName, LEFT_OFFSET, TOP_OFFSET + count * VERTICAL_DISPLAY_DISTANCE);
	}

	private void addDisplayLock(int x, int y) {
		JCheckBox checkBox = new JCheckBox("Unlock");
		checkBox.addItemListener(e -> lockDisplays());
		checkBox.setBounds(x, y, 70, 20);
		checkBox.setForeground(RegularStatSelectionPanel.HEADLINE_COLOR);
		checkBox.setFont(new Font(RegularStatSelectionPanel.HEADLINE_FONT.getName(), RegularStatSelectionPanel.HEADLINE_FONT.getStyle(), 11));
		this.add(checkBox);
	}

	private void lockDisplays() {
		conDisplay.lock();
		agiDisplay.lock();
		strDisplay.lock();
		intDisplay.lock();
		chaDisplay.lock();
		beaDisplay.lock();
		sexDisplay.lock();
		obeDisplay.lock();
	}
	
	private class StatDisplay {

		private static final int DISTANCE = 37;

		private JTextField textField;

		private final int minValue;
		private final int maxValue;

		private final String statName;

		StatDisplay(JPanel panel, String statName, int x, int y) {
			this.statName = statName;

			minValue = statName == "BEA" ? statCalculator.getMinBeauty()
					: statName == "SEX" ? 0 : statCalculator.getMinRegStat();
			maxValue = statName == "BEA" ? statCalculator.getMaxBeauty() : statCalculator.getMaxRegStat();

			JLabel statNameLabel = new JLabel();
			statNameLabel.setText(statName + ":");
			statNameLabel.setBounds(x, y, 35, 30);
			statNameLabel.setHorizontalAlignment(JLabel.RIGHT);
			statNameLabel.setForeground(RegularStatSelectionPanel.HEADLINE_COLOR);
			panel.add(statNameLabel);

			textField = new JTextField();
			textField.setText("0");
			textField.setBounds(x + DISTANCE, y + 4, 25, 22);
			textField.setHorizontalAlignment(JLabel.RIGHT);
			textField.setEditable(false);
			textField.setBorder(null);
			textField.setFont(new Font(statNameLabel.getFont().getName(), statNameLabel.getFont().getStyle(), 13));
			textField.setForeground(Color.BLACK);

			((AbstractDocument) textField.getDocument()).setDocumentFilter(new RegexDocumentFilter());

			textField.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent evt) {
					SwingUtilities.invokeLater(() -> textField.selectAll());
				}
			});

			panel.add(textField);
		}

		private boolean isValidValue(int value) {
			return value % statCalculator.getMultiplier() == 0 
					&& value >= minValue 
					&& value <= maxValue;
		}

		List<String> getEvaluationWarnings() {
			List<String> warnings = new ArrayList<>();

			int currentValue = Integer.parseInt(textField.getText());

			if (currentValue < minValue) {
				warnings.add(new StringBuilder("Value ")
						.append(currentValue)
						.append(" is under the minimal ")
						.append(statName)
						.append(" value of ")
						.append(minValue)
						.toString());
			}
			if (currentValue > maxValue) {
				warnings.add(new StringBuilder("Value ")
						.append(currentValue)
						.append(" exceeds the maximal ")
						.append(statName)
						.append(" value of ")
						.append(maxValue)
						.toString());
			}
			if (currentValue % statCalculator.getMultiplier() != 0) {
				warnings.add(new StringBuilder("Value ")
						.append(currentValue)
						.append(" for ")
						.append(statName)
						.append(" is not a multiple of ")
						.append(statCalculator.getMultiplier())
						.toString());
			}

			return warnings;
		}

		void setValue(int value) {
			textField.setText(String.valueOf(value));
		}

		int getValue() {
			String text = textField.getText();
			if (text == null || text.isEmpty()) {
				return 0;
			}
			return Integer.valueOf(textField.getText());
		}

		void lock() {
			textField.setEditable(!textField.isEditable());
		}

		private class RegexDocumentFilter extends DocumentFilter {
			@Override
			public void replace(FilterBypass fb, int offset, int length, String str, AttributeSet a) throws BadLocationException {
				modifyText(fb, offset, length, str, null);
				StatDisplayPanel.this.displayStatsOnSelectionPanels();
			}

			@Override
			public void insertString(FilterBypass fb, int offset, String str, AttributeSet a) throws BadLocationException {
				modifyText(fb, offset, 0, str, null);
				StatDisplayPanel.this.displayStatsOnSelectionPanels();
			}

			@Override
			public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
				modifyText(fb, offset, length, "", null);
				StatDisplayPanel.this.displayStatsOnSelectionPanels();
			}

			private void modifyText(FilterBypass fb, int offset, int length, String str, AttributeSet a) throws BadLocationException {
				String text = fb.getDocument().getText(0, fb.getDocument().getLength());

				text = new StringBuilder(text.substring(0, offset)).append(str)
						.append(text.substring(offset + length, text.length())).toString();

				if (text == null || text.isEmpty()) {
					fb.replace(offset, length, "0", a);
					checkBoundsAndMultiplier("0");
					return;
				}

				if (text.startsWith("0") && str != null && str.length() != 0 && str.matches(TEXTFIELD_REGEX)) {
					fb.replace(0, 1, str, a);
					checkBoundsAndMultiplier(str);
					return;
				}

				if (!text.matches(TEXTFIELD_REGEX)) {
					Toolkit.getDefaultToolkit().beep();
					return;
				}

				fb.replace(offset, length, str, a);
				checkBoundsAndMultiplier(text);
			}

			private void checkBoundsAndMultiplier(String text) {
				int newValue = Integer.parseInt(text);
				if (!isValidValue(newValue)) {
					textField.setForeground(Color.RED);
				} else {
					textField.setForeground(Color.BLACK);
				}
			}
		}
	}

	private void setDefaultValues() {
		displayValue(conDisplay, statCalculator.getAverageRegStat());
		displayValue(agiDisplay, statCalculator.getAverageRegStat());
		displayValue(strDisplay, statCalculator.getAverageRegStat());
		displayValue(intDisplay, statCalculator.getAverageRegStat());
		displayValue(chaDisplay, statCalculator.getAverageRegStat());
		displayValue(sexDisplay, 0);
		displayValue(obeDisplay, statCalculator.getAverageRegStat());
		displayValue(beaDisplay, statCalculator.getAverageBeauty());

		displayStatsOnSelectionPanels();
	}
}
