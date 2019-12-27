package simplecharacterbuilder.statgenerator;

import javax.swing.JButton;

@SuppressWarnings("serial")
class GenerateButton extends JButton {

	static final int WIDTH = BeautySelectionPanel.WIDTH + StatDisplayPanel.WIDTH + StatGenerator.GAP_WIDTH;
	static final int HEIGHT = 50;

	GenerateButton(int x, int y, StatDisplayPanel statDisplayPanel) {
		this.setText("Generate Stats");
		this.setBounds(x, y, WIDTH, HEIGHT);
		this.addActionListener(e -> statDisplayPanel.displaySelectedStats());
	}
}
