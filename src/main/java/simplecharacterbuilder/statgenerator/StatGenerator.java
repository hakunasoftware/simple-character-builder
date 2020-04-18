package simplecharacterbuilder.statgenerator;

import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.border.Border;

import simplecharacterbuilder.util.CharacterBuilderComponent.CharacterBuilderMainComponent;
import simplecharacterbuilder.util.ConfigReader;

public final class StatGenerator extends CharacterBuilderMainComponent {

	public static final int WIDTH  = RegularStatSelectionPanel.WIDTH + BeautySelectionPanel.WIDTH + StatDisplayPanel.WIDTH + 4 * GAP_WIDTH;
	public static final int HEIGHT = RegularStatSelectionPanel.HEIGHT + 2 * GAP_WIDTH;
	
	public static final int COMPARISON_WIDTH = 82;
	
	private final RegularStatSelectionPanel regularStatSelectionPanel;
	private final BeautySelectionPanel      beautySelectionPanel;
	private final StatDisplayPanel          statDisplayPanel;
	private final StatCalculator            statCalculator;
	private final ConfigReader              configReader;
	
	private final boolean ALWAYS_SCALE;
	private final boolean NEVER_SCALE;

	private StatGenerator(int x, int y, String configPath, boolean showComparison) {
		super(x, y);

		configReader              = new ConfigReader(configPath);
		statCalculator            = new StatCalculator(configReader);
		regularStatSelectionPanel = new RegularStatSelectionPanel(this, GAP_WIDTH, GAP_WIDTH, showComparison);
		beautySelectionPanel      = new BeautySelectionPanel(this, 2 * GAP_WIDTH + RegularStatSelectionPanel.WIDTH, GAP_WIDTH, showComparison);
		statDisplayPanel          = new StatDisplayPanel(this, 3 * GAP_WIDTH + RegularStatSelectionPanel.WIDTH + BeautySelectionPanel.WIDTH, GAP_WIDTH, showComparison);

		mainPanel.add(regularStatSelectionPanel);
		mainPanel.add(statDisplayPanel);
		mainPanel.add(beautySelectionPanel);
		
		if(showComparison) {
			mainPanel.setBounds(mainPanel.getX(), mainPanel.getY(), mainPanel.getWidth() + COMPARISON_WIDTH, mainPanel.getHeight());
		}
		
		ALWAYS_SCALE = configReader.readBoolean("always_scale");
		NEVER_SCALE  = configReader.readBoolean("never_scale");
	}
	
	public static StatGenerator createInstance(int x, int y, String configPath, boolean showComparison) {
		return new StatGenerator(x, y, configPath, showComparison);
	}

	public Map<Stat, Integer> getStats() {
		return statDisplayPanel.getStats();
	}

	public void setStatSuggestions(Map<Stat, Integer> stats) {
		long valuesOutOfBounds = Stat.getAll().stream().filter(stat -> stats.get(stat) > statCalculator.getMax(stat)).count();
		if(!NEVER_SCALE && valuesOutOfBounds >= 1 && (ALWAYS_SCALE || confirmScaling(valuesOutOfBounds))) {
			statCalculator.scaleToMaximalValue(stats);
		}
		statDisplayPanel.displayStats(statCalculator.generateStatSuggestions(stats));
	}
	
	private boolean confirmScaling(long valuesOutOfBounds) {
		StringBuilder dialogueText = new StringBuilder().append(valuesOutOfBounds);
		if(valuesOutOfBounds == 1) {
			dialogueText.append(" stat exceeds its maximal value.");
		} else {
			dialogueText.append(" stats exceed their maximal value.");
		}
		dialogueText.append(" Do you want to downscale all stats?");
		return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
			    mainPanel.getParent(),
			    dialogueText,
			    "Loading stats from Info.xml",
			    JOptionPane.YES_NO_OPTION);
	}
	
	public void setStats(Map<Stat, Integer> stats) {
		statDisplayPanel.displayStats(stats);
	}

	public void setVisible(boolean aFlag) {
		mainPanel.setVisible(aFlag);
	}

	public void setBorder(Border border) {
		mainPanel.setBorder(border);
	}
	
	public boolean confirmIntentIfWarningsExist() {
		List<String> warnings = statDisplayPanel.getEvaluationWarnings();
		if(warnings.isEmpty()) {
			return true;
		} 
		
		StringBuilder dialogueText = new StringBuilder("The following values don't comply with the configured settings: \n\n");
		warnings.stream().forEach(warning -> dialogueText.append("- ").append(warning).append("\n"));
		dialogueText.append("\nAre you sure that you want to proceed?");
		
		return JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(
			    mainPanel.getParent(),
			    dialogueText.toString(),
			    "Are you sure?",
			    JOptionPane.OK_CANCEL_OPTION);
	}
	
	public void setComparisonValues(Map<Stat, Integer> values) {
		statDisplayPanel.setComparisonValues(values);
	}

	RegularStatSelectionPanel getRegularStatSelectionPanel() {
		return regularStatSelectionPanel;
	}

	StatDisplayPanel getStatDisplayPanel() {
		return statDisplayPanel;
	}

	BeautySelectionPanel getBeautySelectionPanel() {
		return beautySelectionPanel;
	}

	StatCalculator getStatCalculator() {
		return statCalculator;
	}
}