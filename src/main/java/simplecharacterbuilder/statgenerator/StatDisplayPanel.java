package simplecharacterbuilder.statgenerator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import simplecharacterbuilder.abstractview.CharacterBuilderComponent;

@SuppressWarnings("serial")
class StatDisplayPanel extends JPanel {

	static final int WIDTH  = 80;
	static final int HEIGHT = BeautySelectionPanel.HEIGHT;

	private static final int VERTICAL_DISPLAY_DISTANCE = 28;

	private static final int TOP_OFFSET  = 3;
	private static final int LEFT_OFFSET = 8;

	private static final String TEXTFIELD_REGEX = "^[0-9]{1,3}$";

	private final RegularStatSelectionPanel regStatSelectionPanel;
	private final BeautySelectionPanel      beautySelectionPanel;
	private final StatCalculator            statCalculator;
	
	private final List<StatDisplay> statDisplays = new ArrayList<>();

	StatDisplayPanel(StatGenerator statGenerator, int xPos, int yPos) {
		this.regStatSelectionPanel = statGenerator.getRegularStatSelectionPanel();
		this.beautySelectionPanel  = statGenerator.getBeautySelectionPanel();
		this.statCalculator        = statGenerator.getStatCalculator();

		this.setBounds(xPos, yPos, WIDTH, HEIGHT);
		this.setBorder(CharacterBuilderComponent.BORDER);
		this.setLayout(null);
		
		this.createStatDisplays();
		this.setDefaultValues();
	}

	private void createStatDisplays() {
		Stat.forAll(stat -> createStatDisplay(stat));
	}
	
	private void createStatDisplay(Stat stat) {
		statDisplays.add(new StatDisplay(stat, LEFT_OFFSET, TOP_OFFSET + VERTICAL_DISPLAY_DISTANCE * statDisplays.size()));
	}
	
	private StatDisplay getStatDisplayPanel(Stat stat) {
		return statDisplays.stream().filter(display -> display.stat.equals(stat)).findFirst().get();
	}

	void displayStats(Map<Stat, Integer> stats) {
		stats.keySet().stream().forEach(stat -> displayStat(stat, stats.get(stat)));
//		displayStatsOnSelectionPanels();
	}

	void displayStat(Stat stat, int value) {
		getStatDisplayPanel(stat).setValue(Math.min(value, 999));
		displayStatOnSelectionPanel(stat);
	}
	

	private void displayStatsOnSelectionPanels() {
		beautySelectionPanel.setSelection(statCalculator.generateSelection(Stat.BEAUTY, getStatDisplayPanel(Stat.BEAUTY).getValue()));
		regStatSelectionPanel.setSelections(statCalculator.generateSelections(getRegStats()));
	}
	
	private void displayStatOnSelectionPanel(Stat stat) {
		if(stat.equals(Stat.BEAUTY)) { 
			beautySelectionPanel.setSelection(statCalculator.generateSelection(Stat.BEAUTY, getStatDisplayPanel(Stat.BEAUTY).getValue()));
		} else { 
			regStatSelectionPanel.setSelections(statCalculator.generateSelections(getRegStats()));
		}
	}

	void displaySelectedStats() {
		Map<Stat, Integer> statSelections = regStatSelectionPanel.getSelections();
		statSelections.put(Stat.BEAUTY, beautySelectionPanel.getSelection());
		displayStats(statCalculator.generateStats(statSelections));
	}

	Map<Stat, Integer> getStats() {
		Map<Stat, Integer> stats = new HashMap<>();
		Stat.forAll(stat -> stats.put(stat, getStatDisplayPanel(stat).getValue()));
		return stats;
	}
	
	private Map<Stat, Integer> getRegStats() {
		Map<Stat, Integer> stats = new HashMap<>();
		Stat.forRegStats(stat -> stats.put(stat, getStatDisplayPanel(stat).getValue()));
		return stats;
	}
	
	List<String> getEvaluationWarnings(){
		List<String> warnings = new ArrayList<>();
		Stat.forAll(stat -> warnings.addAll(getStatDisplayPanel(stat).getEvaluationWarnings()));
		return warnings;
	}

	void lockDisplays() {
		statDisplays.stream().forEach(display -> display.lock());
	}
	
	private class StatDisplay {

		private static final int DISTANCE = 37;

		private JTextField textField;

		private final int minValue;
		private final int maxValue;

		private final Stat stat;

		StatDisplay(Stat stat, int x, int y) {
			this.stat = stat;

			minValue = stat.equals(Stat.SEX) ? 0 : statCalculator.getMin(stat);
			maxValue = statCalculator.getMax(stat);

			JLabel statNameLabel = new JLabel();
			statNameLabel.setText(stat.getAbbreviation() + ":");
			statNameLabel.setBounds(x, y, 35, 30);
			statNameLabel.setHorizontalAlignment(JLabel.RIGHT);
			statNameLabel.setForeground(RegularStatSelectionPanel.HEADLINE_COLOR);
			StatDisplayPanel.this.add(statNameLabel);

			textField = new JTextField();
			textField.setText("0");
			textField.setBounds(x + DISTANCE, y + 4, 25, 22);
			textField.setHorizontalAlignment(JLabel.RIGHT);
			textField.setBorder(null);
			textField.setFont(new Font(statNameLabel.getFont().getName(), statNameLabel.getFont().getStyle(), 13));
			textField.setForeground(Color.BLACK);
			textField.setBackground(statNameLabel.getBackground());

			((AbstractDocument) textField.getDocument()).setDocumentFilter(new RegexDocumentFilter());

			textField.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent evt) {
					SwingUtilities.invokeLater(() -> textField.selectAll());
				}
			});

			StatDisplayPanel.this.add(textField);
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
						.append(stat.getName())
						.append(" value of ")
						.append(minValue)
						.toString());
			}
			if (currentValue > maxValue) {
				warnings.add(new StringBuilder("Value ")
						.append(currentValue)
						.append(" exceeds the maximal ")
						.append(stat.getName())
						.append(" value of ")
						.append(maxValue)
						.toString());
			}
			if (currentValue % statCalculator.getMultiplier() != 0) {
				warnings.add(new StringBuilder("Value ")
						.append(currentValue)
						.append(" for ")
						.append(stat.getName())
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
				StatDisplayPanel.this.displayStatOnSelectionPanel(StatDisplay.this.stat);
			}

			@Override
			public void insertString(FilterBypass fb, int offset, String str, AttributeSet a) throws BadLocationException {
				modifyText(fb, offset, 0, str, null);
				StatDisplayPanel.this.displayStatOnSelectionPanel(StatDisplay.this.stat);
			}

			@Override
			public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
				modifyText(fb, offset, length, "", null);
				StatDisplayPanel.this.displayStatOnSelectionPanel(StatDisplay.this.stat);
			}

			private void modifyText(FilterBypass fb, int offset, int length, String str, AttributeSet a) throws BadLocationException {
				String text = fb.getDocument().getText(0, fb.getDocument().getLength());
				int currentTextLength = text.length();

				text = new StringBuilder(text.substring(0, offset)).append(str)
						.append(text.substring(offset + length, text.length())).toString();

				if (text.isEmpty() || text.equals("0")) {
					fb.replace(0, currentTextLength, "0", a);
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
		Stat.forAll(stat -> displayStat(stat, getDefault(stat)));
		displayStatsOnSelectionPanels();
	}
	private int getDefault(Stat stat) {
		switch(stat) {
			case SEX:    return 0;
			default:     return statCalculator.getAverage(stat);
		}
	}
}
