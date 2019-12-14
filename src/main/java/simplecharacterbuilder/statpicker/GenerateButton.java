package simplecharacterbuilder.statpicker;

import javax.swing.JButton;

import simplecharacterbuilder.statpicker.RegularStatSelectionPanel.RegularStatSelectionDTO;

@SuppressWarnings("serial")
class GenerateButton extends JButton {
	
	static final int WIDTH = BeautySelectionPanel.WIDTH + StatDisplayPanel.WIDTH + StatPicker.GAP_WIDTH;
	static final int HEIGHT = 50;

	private final RegularStatSelectionPanel regularStatSelectionPanel;
	private final StatDisplayPanel statDisplayPanel;
	private final StatCalculator statCalculator;

	GenerateButton(int x, int y, RegularStatSelectionPanel regularStatSelectionPanel, StatDisplayPanel statDisplayPanel) {
		this.regularStatSelectionPanel = regularStatSelectionPanel;
		this.statDisplayPanel = statDisplayPanel;
		this.statCalculator = new StatCalculator();
		
		this.setText("Generate Stats");
		this.setBounds(x, y, WIDTH, HEIGHT);
		
		this.addActionListener(e -> readAndGenerateStats());
	}
	
	private void readAndGenerateStats() {
		RegularStatSelectionDTO regularStatSelectionDTO = regularStatSelectionPanel.getSelections();
		int beautySelection = 10;
		statDisplayPanel.displayStats(statCalculator.generateStats(regularStatSelectionDTO, beautySelection));
	}

}