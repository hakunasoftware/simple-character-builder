package simplecharacterbuilder.abstractview;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ApplicationFrame extends JFrame {

	public ApplicationFrame(int width, int height, List<CharacterBuilderComponent> components) {
		this.setMinimumSize(new Dimension(width + 10, height + 30));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(new GridBagLayout());
		this.setResizable(false);
		this.setVisible(true);
		
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(null);
		contentPanel.setPreferredSize(new Dimension(width, height));
		components.stream().forEach(component -> component.addTo(contentPanel));
		this.add(contentPanel);
	}
	
	
}