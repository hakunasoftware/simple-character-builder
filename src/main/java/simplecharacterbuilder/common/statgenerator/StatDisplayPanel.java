package simplecharacterbuilder.common.statgenerator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import simplecharacterbuilder.common.resourceaccess.ConfigReaderRepository;
import simplecharacterbuilder.common.resourceaccess.PropertyRepository;
import simplecharacterbuilder.common.uicomponents.ContentPanel;

@SuppressWarnings("serial")
class StatDisplayPanel extends ContentPanel {

	static int WIDTH = 75;
	static final int HEIGHT = BeautySelectionPanel.HEIGHT;

	private static final int VERTICAL_DISPLAY_DISTANCE = 28;

	private static final int TOP_OFFSET = 3;
	private static final int LEFT_OFFSET = 3;

	private static final String TEXTFIELD_REGEX = "^[0-9]{1,3}$";

	private static final Color POSITIVE_GREEN = new Color(34, 139, 34);
	private static final Color WARNING_RED = new Color(220, 20, 60);

	private final RegularStatSelectionPanel regStatSelectionPanel;
	private final BeautySelectionPanel beautySelectionPanel;
	private final StatCalculator statCalculator;

	private final List<StatDisplay> statDisplays = new ArrayList<>();

	private final boolean showComparisons;

	StatDisplayPanel(StatGenerator statGenerator, int xPos, int yPos, boolean showComparisons) {
		super(xPos, yPos, showComparisons ? WIDTH = StatGenerator.COMPARISON_WIDTH + WIDTH : WIDTH, HEIGHT);

		this.regStatSelectionPanel = statGenerator.getRegularStatSelectionPanel();
		this.beautySelectionPanel = statGenerator.getBeautySelectionPanel();
		this.statCalculator = statGenerator.getStatCalculator();

		this.showComparisons = showComparisons;

		this.createStatDisplays();
		this.setDefaultValues();
	}

	private void createStatDisplays() {
		Stat.forAll(stat -> createStatDisplay(stat));
	}

	private void createStatDisplay(Stat stat) {
		statDisplays
				.add(new StatDisplay(stat, LEFT_OFFSET, TOP_OFFSET + VERTICAL_DISPLAY_DISTANCE * statDisplays.size()));
	}

	private StatDisplay getStatDisplayPanel(Stat stat) {
		return statDisplays.stream().filter(display -> display.stat.equals(stat)).findFirst().get();
	}

	void displayStats(Map<Stat, Integer> stats) {
		stats.keySet().stream().forEach(stat -> displayStat(stat, stats.get(stat)));
	}

	void displayStat(Stat stat, int value) {
		getStatDisplayPanel(stat).setValue(Math.min(value, 999));
		displayStatOnSelectionPanel(stat);
	}

	void setComparisonValues(Map<Stat, Integer> values) {
		Stat.forAll(stat -> getStatDisplayPanel(stat).setComparisonValue(values.get(stat)));
	}

	private void displayStatOnSelectionPanel(Stat stat) {
		if (stat.equals(Stat.BEAUTY)) {
			beautySelectionPanel.setSelection(
					statCalculator.generateSelection(Stat.BEAUTY, getStatDisplayPanel(Stat.BEAUTY).getValue()));
		} else {
			regStatSelectionPanel.setSelection(stat,
					statCalculator.generateSelection(stat, getStatDisplayPanel(stat).getValue()));
		}
	}

	void displaySelectedStats() {
		Stat.forAll(stat -> displaySelectedStat(stat));
	}

	private void displaySelectedStat(Stat stat) {
		int selection = stat == Stat.BEAUTY ? beautySelectionPanel.getSelection()
				: regStatSelectionPanel.getSelection(stat);
		displayStat(stat, statCalculator.generateStatFromSelection(stat, selection));
	}

	Map<Stat, Integer> getStats() {
		Map<Stat, Integer> stats = new HashMap<>();
		Stat.forAll(stat -> stats.put(stat, getStatDisplayPanel(stat).getValue()));
		return stats;
	}

	List<String> getEvaluationWarnings() {
		List<String> warnings = new ArrayList<>();
		Stat.forAll(stat -> warnings.addAll(getStatDisplayPanel(stat).getEvaluationWarnings()));
		return warnings;
	}

	void lockDisplays() {
		statDisplays.stream().forEach(display -> display.lock());
	}

	int getComparisonValue(Stat stat) {
		return getStatDisplayPanel(stat).comparisonPanel.getComparisonValue();
	}

	private void setDefaultValues() {
		Stat.forAll(stat -> displayStat(stat, getDefault(stat)));
	}

	private int getDefault(Stat stat) {
		return statCalculator.getAverage(stat);
	}

	private class StatDisplay {
		private static final int VERT_TEXTFIELD_OFFSET = 4;
		private final Font numberFont;

		private final int DISTANCE = 37 + (showComparisons ? 50 : 0);

		private JTextField textField;

		private final int minValue;
		private final int maxValue;

		private final Stat stat;

		private final ComparisonPanels comparisonPanel;

		StatDisplay(Stat stat, int x, int y) {
			this.stat = stat;

			minValue = statCalculator.getMin(stat);
			maxValue = statCalculator.getMax(stat);

			JLabel statNameLabel = new JLabel();
			statNameLabel.setText(stat.getAbbreviation() + ":");
			statNameLabel.setBounds(x, y, 35, 30);
			statNameLabel.setHorizontalAlignment(JLabel.RIGHT);
			statNameLabel.setForeground(RegularStatSelectionPanel.HEADLINE_COLOR);
			StatDisplayPanel.this.add(statNameLabel);

			numberFont = new Font(statNameLabel.getFont().getName(), statNameLabel.getFont().getStyle(), 13);

			textField = new JTextField();
			textField.setText("0");
			textField.setBounds(x + DISTANCE, y + VERT_TEXTFIELD_OFFSET, 25, 22);
			textField.setHorizontalAlignment(JLabel.RIGHT);
			textField.setBorder(null);
			textField.setFont(numberFont);
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

			comparisonPanel = showComparisons ? new ComparisonPanels(x + 37, y + VERT_TEXTFIELD_OFFSET) : null;
		}

		private class ComparisonPanels {
			private static final int WIDTH = 25;
			private static final int HEIGHT = 22;

			private final JLabel oldValueLabel;
			private final JLabel differenceLabel;

			ComparisonPanels(int x, int y) {
				oldValueLabel = createLabel(x, y, WIDTH);
				JLabel arrowLabel = createLabel(x + WIDTH + 6, y, 20);
				differenceLabel = createLabel(x + 79, y, WIDTH + 14);

				oldValueLabel.setHorizontalAlignment(JLabel.RIGHT);
				arrowLabel.setText(ConfigReaderRepository.getCharacterbuilderConfigReader().readString(PropertyRepository.BALANCING_ARROW));
				arrowLabel.setForeground(RegularStatSelectionPanel.HEADLINE_COLOR);

				StatDisplayPanel.this.add(oldValueLabel);
				StatDisplayPanel.this.add(differenceLabel);
				StatDisplayPanel.this.add(arrowLabel);

				oldValueLabel.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						StatDisplay.this.setValue(Integer.parseInt(oldValueLabel.getText()));
					}
				});
			}

			JLabel createLabel(int x, int y, int width) {
				JLabel label = new JLabel();
				label.setHorizontalAlignment(JLabel.LEFT);
				label.setBounds(x, y, width, HEIGHT);
				label.setFont(numberFont);
				return label;
			}

			void setComparisonValue(int value) {
				oldValueLabel.setText(String.valueOf(value));
				oldValueLabel.setForeground(
						statCalculator.isValidValue(stat, value) ? RegularStatSelectionPanel.HEADLINE_COLOR
								: WARNING_RED);
			}

			int getComparisonValue() {
				String upperText = oldValueLabel.getText();
				return Integer.valueOf(upperText == "" ? "0" : upperText);
			}

			void setDifference(int newValue) {
				int diff = newValue - getComparisonValue();
				if (diff == 0) {
					differenceLabel.setForeground(RegularStatSelectionPanel.HEADLINE_COLOR);
					differenceLabel.setText("(+0)");
					return;
				}
				StringBuilder difference = new StringBuilder("(");
				if (diff < 0) {
					difference.append("-");
					differenceLabel.setForeground(WARNING_RED);
				} else {
					difference.append("+");
					differenceLabel.setForeground(POSITIVE_GREEN);
				}
				differenceLabel.setText(difference.append(String.valueOf(Math.abs(diff))).append(")").toString());
			}
		}

		List<String> getEvaluationWarnings() {
			List<String> warnings = new ArrayList<>();

			int currentValue = Integer.parseInt(textField.getText());

			if (currentValue < minValue) {
				warnings.add(new StringBuilder("Value ").append(currentValue).append(" is below the minimal ")
						.append(stat.getName()).append(" value of ").append(minValue).toString());
			}
			if (currentValue > maxValue) {
				warnings.add(new StringBuilder("Value ").append(currentValue).append(" exceeds the maximal ")
						.append(stat.getName()).append(" value of ").append(maxValue).toString());
			}
			if (currentValue % statCalculator.getMultiplier() != 0) {
				warnings.add(new StringBuilder("Value ").append(currentValue).append(" for ").append(stat.getName())
						.append(" is not a multiple of ").append(statCalculator.getMultiplier()).toString());
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

		void setComparisonValue(int value) {
			comparisonPanel.setComparisonValue(value);
		}

		void lock() {
			textField.setEditable(!textField.isEditable());
		}

		private class RegexDocumentFilter extends DocumentFilter {
			@Override
			public void replace(FilterBypass fb, int offset, int length, String str, AttributeSet a)
					throws BadLocationException {
				modifyText(fb, offset, length, str, null);
				StatDisplayPanel.this.displayStatOnSelectionPanel(StatDisplay.this.stat);
			}

			@Override
			public void insertString(FilterBypass fb, int offset, String str, AttributeSet a)
					throws BadLocationException {
				modifyText(fb, offset, 0, str, null);
				StatDisplayPanel.this.displayStatOnSelectionPanel(StatDisplay.this.stat);
			}

			@Override
			public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
				modifyText(fb, offset, length, "", null);
				StatDisplayPanel.this.displayStatOnSelectionPanel(StatDisplay.this.stat);
			}

			private void modifyText(FilterBypass fb, int offset, int length, String str, AttributeSet a)
					throws BadLocationException {
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

			private void setDifferenceFromString(String text) {
				comparisonPanel.setDifference(Integer.valueOf(text));
			}

			private void checkBoundsAndMultiplier(String text) {
				int newValue = Integer.parseInt(text);
				if (!statCalculator.isValidValue(stat, newValue)) {
					textField.setForeground(WARNING_RED);
				} else {
					textField.setForeground(Color.BLACK);
				}
				if (showComparisons) {
					setDifferenceFromString(text);
				}
			}
		}
	}
}
