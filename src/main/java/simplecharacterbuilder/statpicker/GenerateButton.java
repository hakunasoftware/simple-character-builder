package simplecharacterbuilder.statpicker;

import javax.swing.JButton;

import simplecharacterbuilder.statpicker.RegularStatSelectionPanel.RegularStatSelectionDTO;

@SuppressWarnings("serial")
class GenerateButton extends JButton {

	static final int WIDTH = BeautySelectionPanel.WIDTH + StatDisplayPanel.WIDTH + StatPicker.GAP_WIDTH;
	static final int HEIGHT = 50;

	private final RegularStatSelectionPanel regularStatSelectionPanel;
	private final BeautySelectionPanel beautySelectionPanel;
	private final StatDisplayPanel statDisplayPanel;
	private final StatCalculator statCalculator;

	GenerateButton(int x, int y, RegularStatSelectionPanel regularStatSelectionPanel, BeautySelectionPanel beautySelectionPanel, StatDisplayPanel statDisplayPanel) {
		this.regularStatSelectionPanel = regularStatSelectionPanel;
		this.beautySelectionPanel = beautySelectionPanel;
		this.statDisplayPanel = statDisplayPanel;
		this.statCalculator = new StatCalculator();
		
		this.setText("Generate Stats");
		this.setBounds(x, y, WIDTH, HEIGHT);
		
		this.addActionListener(e -> readAndGenerateStats());
	}

	private void readAndGenerateStats() {
		RegularStatSelectionDTO regularStatSelectionDTO = regularStatSelectionPanel.getSelections();
		int beautySelection = beautySelectionPanel.getSelection();
		statDisplayPanel.displayStats(statCalculator.generateStats(regularStatSelectionDTO, beautySelection));
	}

}
