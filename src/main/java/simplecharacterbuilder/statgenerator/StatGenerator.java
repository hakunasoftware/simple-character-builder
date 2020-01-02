package simplecharacterbuilder.statgenerator;

import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.border.Border;

import simplecharacterbuilder.abstractview.CharacterBuilderComponent.CharacterBuilderMainComponent;

public final class StatGenerator extends CharacterBuilderMainComponent {

	public static final int WIDTH  = RegularStatSelectionPanel.WIDTH + BeautySelectionPanel.WIDTH + StatDisplayPanel.WIDTH + 4 * GAP_WIDTH;
	public static final int HEIGHT = RegularStatSelectionPanel.HEIGHT + 2 * GAP_WIDTH;
	
	public static final int COMPARISON_WIDTH = 26;
	
	private final RegularStatSelectionPanel regularStatSelectionPanel;
	private final StatDisplayPanel          statDisplayPanel;
	private final BeautySelectionPanel      beautySelectionPanel;
	private final StatCalculator            statCalculator;


	private StatGenerator(int x, int y, String configPath, boolean showComparison) {
		super(x, y);

		statCalculator            = new StatCalculator(configPath);
		regularStatSelectionPanel = new RegularStatSelectionPanel(this, GAP_WIDTH, GAP_WIDTH);
		beautySelectionPanel      = new BeautySelectionPanel(this, 2 * GAP_WIDTH + RegularStatSelectionPanel.WIDTH, GAP_WIDTH);
		statDisplayPanel          = new StatDisplayPanel(this, 3 * GAP_WIDTH + RegularStatSelectionPanel.WIDTH + BeautySelectionPanel.WIDTH, GAP_WIDTH, showComparison);

		mainPanel.add(regularStatSelectionPanel);
		mainPanel.add(statDisplayPanel);
		mainPanel.add(beautySelectionPanel);
		
		if(showComparison) {
			mainPanel.setBounds(mainPanel.getX(), mainPanel.getY(), mainPanel.getWidth() + COMPARISON_WIDTH, mainPanel.getHeight());
		}
	}
	
	public static StatGenerator createInstance(int x, int y, String configPath, boolean showComparison) {
		return new StatGenerator(x, y, configPath, showComparison);
	}

	public Map<Stat, Integer> getStats() {
		return statDisplayPanel.getStats();
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