package simplecharacterbuilder.statpicker;

import javax.swing.JPanel;

class BeautySelectionPanel extends JPanel {
	private static final long serialVersionUID = 2878239138376169175L;
	
	static final int WIDTH = 130;
	static final int HEIGHT = RegularStatSelectionPanel.HEIGHT - GenerateButton.HEIGHT - StatPicker.GAP_WIDTH;
	
	BeautySelectionPanel(int x, int y) {
		this.setBounds(x, y, WIDTH, HEIGHT);
		this.setBorder(RegularStatSelectionPanel.BORDER);
		this.setLayout(null);
	}
	
	//TODO
	
}
