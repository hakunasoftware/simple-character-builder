package simplecharacterbuilder.statgenerator;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

import simplecharacterbuilder.abstractview.CharacterBuilderComponent;
import simplecharacterbuilder.statgenerator.RegularStatSelectionPanel.StatButtonGroup;


class BeautySelectionPanel extends JPanel {
	private static final long serialVersionUID = 2878239138376169175L;

	private static final String[] BEAUTY_OPTIONS = new String[] { "Unattractive", "Plain", "Normal", "Pretty", "Beautiful", "Stunning", "Perfect", "Divine" };

	static final int WIDTH = 130;
	static final int HEIGHT = RegularStatSelectionPanel.HEIGHT - 50 - StatGenerator.GAP_WIDTH;
	
	private final StatButtonGroup buttonGroup = new StatButtonGroup(Stat.BEAUTY);
	private final StatGenerator statGenerator;

	BeautySelectionPanel(StatGenerator statGenerator, int x, int y) {
		this.statGenerator = statGenerator;
		
		this.setBounds(x, y, WIDTH, HEIGHT);
		this.setBorder(CharacterBuilderComponent.BORDER);
		this.setLayout(null);
		
		addCheckBoxes();
	}
	
	int getSelection() {
		return Integer.parseInt(buttonGroup.getSelection().getActionCommand());
	}
	
	void setSelection(int selectionIndex) {
		this.buttonGroup.setSelection(selectionIndex);
	}
	
	private void addCheckBoxes() {
		for (int i = 0; i < BEAUTY_OPTIONS.length; i++) {
			JToggleButton checkBox = new JRadioButton(BEAUTY_OPTIONS[i]);
			checkBox.setBounds(10, 10 + 31 * i, 110, 20);
			checkBox.setActionCommand(String.valueOf(i));
			checkBox.addActionListener(StatButtonGroup.createDisplayActionListener(statGenerator, Stat.BEAUTY, 
					() -> Integer.valueOf(checkBox.getActionCommand())));
			buttonGroup.add(checkBox);
			this.add(checkBox);
		}
	}
}
