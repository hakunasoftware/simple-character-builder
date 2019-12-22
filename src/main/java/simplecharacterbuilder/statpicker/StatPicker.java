package simplecharacterbuilder.statpicker;

import javax.swing.border.Border;

import lombok.Builder;
import lombok.Data;
import simplecharacterbuilder.abstractview.CharacterBuilderComponent.CharacterBuilderMainComponent;

public final class StatPicker extends CharacterBuilderMainComponent {
	private final RegularStatSelectionPanel regularStatSelectionPanel;
	private final StatDisplayPanel statDisplayPanel;
	private final BeautySelectionPanel beautySelectionPanel;
	private final GenerateButton generateButton;


	private StatPicker(int x, int y, String configPath) {
		super(x, y);
		
		regularStatSelectionPanel = new RegularStatSelectionPanel(GAP_WIDTH, GAP_WIDTH);
		statDisplayPanel = new StatDisplayPanel(3 * GAP_WIDTH + RegularStatSelectionPanel.WIDTH + BeautySelectionPanel.WIDTH, GAP_WIDTH);
		beautySelectionPanel = new BeautySelectionPanel(2 * GAP_WIDTH + RegularStatSelectionPanel.WIDTH, GAP_WIDTH);
		generateButton = createGenerateButton(configPath);

		mainPanel.add(regularStatSelectionPanel);
		mainPanel.add(statDisplayPanel);
		mainPanel.add(beautySelectionPanel);
		mainPanel.add(generateButton);
	}

	public static StatPicker createInstance(int x, int y, String configPath) {
		return new StatPicker(x, y, configPath);
	}

	public StatDTO getStats() {
		return statDisplayPanel.getStats();
	}

	public void setStats(StatDTO statDTO) {
		statDisplayPanel.displayStats(statDTO);
		// TODO integrate selections
	}

	public void setVisible(boolean aFlag) {
		mainPanel.setVisible(aFlag);
	}

	public void setBorder(Border border) {
		mainPanel.setBorder(border);
	}

	private GenerateButton createGenerateButton(String configPath) {
		int x = 2 * GAP_WIDTH + RegularStatSelectionPanel.WIDTH;
		int y = GAP_WIDTH + RegularStatSelectionPanel.HEIGHT - GenerateButton.HEIGHT;
		return new GenerateButton(x, y, configPath, regularStatSelectionPanel, beautySelectionPanel, statDisplayPanel);
	}

	@Data
	@Builder
	public static class StatDTO {
		private int constitution;
		private int agility;
		private int strength;
		private int intelligence;
		private int charisma;
		private int obedience;
		private int sex;
		private int beauty;
	}
}