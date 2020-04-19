package simplecharacterbuilder.util;

import static simplecharacterbuilder.util.CharacterBuilderComponent.BORDER;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ContentPanel extends JPanel {
	
	public ContentPanel(int x, int y, int width, int height)
	{
		this.setBounds(x, y, width, height);
		this.setBorder(BORDER);
		this.setLayout(null);
	}

}
