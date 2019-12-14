package simplecharacterbuilder.start;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ApplicationFrame extends JFrame {

	ApplicationFrame(int width, int height, List<CharacterBuilderComponent> components) {
		this.setMinimumSize(new Dimension(width + 20, height + 40));
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
	
	public interface CharacterBuilderComponent {
		public CharacterBuilderComponent location(int x, int y);
		public void addTo(JPanel panel);
	}
}
