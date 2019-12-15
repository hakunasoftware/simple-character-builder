package simplecharacterbuilder.statpicker;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.border.Border;

import lombok.Builder;
import lombok.Data;
import simplecharacterbuilder.start.ApplicationFrame.CharacterBuilderComponent;

public final class StatPicker implements CharacterBuilderComponent {
	
	static final int GAP_WIDTH = 15;

	private final RegularStatSelectionPanel regularStatSelectionPanel;
	private final StatDisplayPanel statDisplayPanel;
	private final BeautySelectionPanel beautySelectionPanel;
	private final GenerateButton generateButton;

	private final JPanel mainPanel;

	public static final int WIDTH = 4 * GAP_WIDTH + RegularStatSelectionPanel.WIDTH + BeautySelectionPanel.WIDTH + StatDisplayPanel.WIDTH;
	public static final int HEIGHT = 2 * GAP_WIDTH + RegularStatSelectionPanel.HEIGHT;

	private StatPicker(String configPath) {
		regularStatSelectionPanel = new RegularStatSelectionPanel(GAP_WIDTH, GAP_WIDTH);
		statDisplayPanel = new StatDisplayPanel(3 * GAP_WIDTH + RegularStatSelectionPanel.WIDTH + BeautySelectionPanel.WIDTH, GAP_WIDTH);
		beautySelectionPanel = new BeautySelectionPanel(2 * GAP_WIDTH + RegularStatSelectionPanel.WIDTH, GAP_WIDTH);
		generateButton = createGenerateButton(configPath);

		mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		mainPanel.setLayout(null);

		mainPanel.add(regularStatSelectionPanel);
		mainPanel.add(statDisplayPanel);
		mainPanel.add(beautySelectionPanel);
		mainPanel.add(generateButton);
	}

	public static StatPicker createInstance(String configPath) {
		return new StatPicker(configPath);
	}

	@Override
	public CharacterBuilderComponent location(int x, int y) {
		mainPanel.setBounds(x, y, WIDTH, HEIGHT);
		return this;
	}

	@Override
	public void addTo(JPanel panel) {
		panel.add(mainPanel);
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