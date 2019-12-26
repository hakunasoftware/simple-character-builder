package simplecharacterbuilder.statgenerator;

import javax.swing.border.Border;

import simplecharacterbuilder.abstractview.CharacterBuilderComponent.CharacterBuilderMainComponent;

public final class StatGenerator extends CharacterBuilderMainComponent {
	private final RegularStatSelectionPanel regularStatSelectionPanel;
	private final StatDisplayPanel          statDisplayPanel;
	private final BeautySelectionPanel      beautySelectionPanel;
	private final GenerateButton            generateButton;
	
	private final StatCalculator statcalculator;

	private StatGenerator(int x, int y, String configPath) {
		super(x, y);

		statcalculator = new StatCalculator(configPath);
		
		regularStatSelectionPanel   = new RegularStatSelectionPanel(GAP_WIDTH, GAP_WIDTH);
		statDisplayPanel            = new StatDisplayPanel(3 * GAP_WIDTH + RegularStatSelectionPanel.WIDTH + BeautySelectionPanel.WIDTH, GAP_WIDTH);
		beautySelectionPanel        = new BeautySelectionPanel(2 * GAP_WIDTH + RegularStatSelectionPanel.WIDTH, GAP_WIDTH);
		generateButton              = createGenerateButton(statcalculator);

		mainPanel.add(regularStatSelectionPanel);
		mainPanel.add(statDisplayPanel);
		mainPanel.add(beautySelectionPanel);
		mainPanel.add(generateButton);
	}

	public static StatGenerator createInstance(int x, int y, String configPath) {
		return new StatGenerator(x, y, configPath);
	}

	public StatDTO getStats() {
		return statDisplayPanel.getStats();
	}

	public void setStats(StatDTO statDTO) {
		statDisplayPanel.displayStats(statDTO);
		beautySelectionPanel.setSelection(statcalculator.generateBeautySelection(statDTO));
		regularStatSelectionPanel.setSelection(statcalculator.generateRegularStatSelectionDTO(statDTO));
	}

	public void setVisible(boolean aFlag) {
		mainPanel.setVisible(aFlag);
	}

	public void setBorder(Border border) {
		mainPanel.setBorder(border);
	}

	private GenerateButton createGenerateButton(StatCalculator statCalculator) {
		int x = 2 * GAP_WIDTH + RegularStatSelectionPanel.WIDTH;
		int y = GAP_WIDTH + RegularStatSelectionPanel.HEIGHT - GenerateButton.HEIGHT;
		return new GenerateButton(x, y, statCalculator, regularStatSelectionPanel, beautySelectionPanel, statDisplayPanel);
	}
}