package simplecharacterbuilder.characterbuilder.util;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

public class JFileChooserPool {
	private static final JFileChooserPool INSTANCE = new JFileChooserPool();
	
	private final JFileChooser portraitChooser;

	private JFileChooserPool() {
		this.portraitChooser = createPortraitChooser();
	}
	
	
	public static void init() {
		// LookAndFeel needs to be set after creating all Components, but before 
		// creating JFileChoosers, which need to be created before running the application. 
		// I have no fucking clue why, but shit breaks otherwise. 
		// This empty method is just for triggering the static members.
	}

	
	private static JFileChooser createPortraitChooser() {
		Boolean old = UIManager.getBoolean("FileChooser.readOnly");
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		JFileChooser chooser = new JFileChooser();
		UIManager.put("FileChooser.readOnly", old);

		chooser.setDialogTitle("Select Portrait (200x200px and png-format)");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory() || file.getName().endsWith(".png");
			}

			@Override
			public String getDescription() {
				return "Folders and .png's";
			}
		});
		return chooser;
	}
	
	public static JFileChooser getPortraitChooser() {
		return INSTANCE.portraitChooser;
	}

}
