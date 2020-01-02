package simplecharacterbuilder.abstractview;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;

@SuppressWarnings("serial")
public class ApplicationFrame extends JFrame {

	public ApplicationFrame(int width, int height, String title, List<CharacterBuilderComponent> components) {
		this.setMinimumSize(new Dimension(width + 10, height + 30));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(new GridBagLayout());
		this.setResizable(false);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setTitle(title);
		
		JLayeredPane contentPanel = new JLayeredPane();
		contentPanel.setLayout(null);
		contentPanel.setPreferredSize(new Dimension(width, height));
		
		int i = 0;
		components.stream().forEach(component -> component.addTo(contentPanel, i));
		this.add(contentPanel);
	}
	
	
}
