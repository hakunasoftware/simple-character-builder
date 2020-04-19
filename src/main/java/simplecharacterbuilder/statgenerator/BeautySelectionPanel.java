package simplecharacterbuilder.statgenerator;

import static simplecharacterbuilder.util.CharacterBuilderComponent.CONTROLPANEL_HEIGHT;
import static simplecharacterbuilder.util.CharacterBuilderComponent.GAP_WIDTH;
import static simplecharacterbuilder.util.CharacterBuilderComponent.MAINPANEL_HEIGHT;

import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

import simplecharacterbuilder.statgenerator.RegularStatSelectionPanel.StatButtonGroup;
import simplecharacterbuilder.util.ContentPanel;


@SuppressWarnings("serial")
class BeautySelectionPanel extends ContentPanel {

	private static final String[] BEAUTY_OPTIONS = new String[] { "Unattractive", "Plain", "Normal", "Pretty", "Beautiful", "Stunning", "Perfect", "Divine" };

	static final int WIDTH  = 115;
	static final int HEIGHT = MAINPANEL_HEIGHT - CONTROLPANEL_HEIGHT - 3 * GAP_WIDTH;
	
	private final StatButtonGroup buttonGroup = new StatButtonGroup(Stat.BEAUTY);
	private final StatGenerator statGenerator;
	private final boolean showComparison;

	BeautySelectionPanel(StatGenerator statGenerator, int x, int y, boolean showComparison) {
		super(x, y, WIDTH, HEIGHT);
		this.statGenerator = statGenerator;
		
		this.showComparison = showComparison;
		
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
			checkBox.setBounds(10, 8 + 28 * i, 100, 20);
			checkBox.setActionCommand(String.valueOf(i));
			checkBox.addActionListener(StatButtonGroup.createDisplayActionListener(statGenerator, Stat.BEAUTY, 
					() -> Integer.valueOf(checkBox.getActionCommand()), showComparison));
			buttonGroup.add(checkBox);
			this.add(checkBox);
		}
	}
}
