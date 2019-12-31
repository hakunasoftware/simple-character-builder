package simplecharacterbuilder.statgenerator;

import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.border.Border;

import simplecharacterbuilder.abstractview.CharacterBuilderComponent.CharacterBuilderMainComponent;

public final class StatGenerator extends CharacterBuilderMainComponent {
	private final RegularStatSelectionPanel regularStatSelectionPanel;
	private final StatDisplayPanel          statDisplayPanel;
	private final BeautySelectionPanel      beautySelectionPanel;
	private final GenerateButton            generateButton;
	
	private final StatCalculator statCalculator;

	private StatGenerator(int x, int y, String configPath) {
		super(x, y);

		statCalculator = new StatCalculator(configPath);
		
		regularStatSelectionPanel   = new RegularStatSelectionPanel(GAP_WIDTH, GAP_WIDTH);
		beautySelectionPanel        = new BeautySelectionPanel(2 * GAP_WIDTH + RegularStatSelectionPanel.WIDTH, GAP_WIDTH);
		statDisplayPanel            = createStatDisplayPanel();
		generateButton              = createGenerateButton();

		mainPanel.add(regularStatSelectionPanel);
		mainPanel.add(statDisplayPanel);
		mainPanel.add(beautySelectionPanel);
		mainPanel.add(generateButton);
	}

	public static StatGenerator createInstance(int x, int y, String configPath) {
		return new StatGenerator(x, y, configPath);
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
	
	private StatDisplayPanel createStatDisplayPanel() {
		int x = 3 * GAP_WIDTH + RegularStatSelectionPanel.WIDTH + BeautySelectionPanel.WIDTH;
		int y = GAP_WIDTH;
		return new StatDisplayPanel(x, y, statCalculator, regularStatSelectionPanel, beautySelectionPanel);
	}

	private GenerateButton createGenerateButton() {
		int x = 2 * GAP_WIDTH + RegularStatSelectionPanel.WIDTH;
		int y = GAP_WIDTH + RegularStatSelectionPanel.HEIGHT - GenerateButton.HEIGHT;
		return new GenerateButton(x, y, statDisplayPanel);
	}
}