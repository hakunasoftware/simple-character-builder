package simplecharacterbuilder.common.uicomponents;

import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.BORDER;

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
