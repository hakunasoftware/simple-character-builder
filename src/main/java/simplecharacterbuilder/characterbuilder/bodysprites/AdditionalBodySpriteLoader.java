package simplecharacterbuilder.characterbuilder.bodysprites;

import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.CONTROLPANEL_WIDTH;
import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.GAP_WIDTH;
import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.MAINPANEL_HEIGHT;
import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.MAINPANEL_WIDTH;

import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;

import simplecharacterbuilder.characterbuilder.util.holder.BodyImageFileHolder;
import simplecharacterbuilder.characterbuilder.util.holder.BodyPartRepository;
import simplecharacterbuilder.characterbuilder.util.holder.JFileChooserPool;
import simplecharacterbuilder.characterbuilder.util.ui.UIComponentFactory;
import simplecharacterbuilder.characterbuilder.util.ui.UIComponentFactory.ListComponentDto;
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.CharacterBuilderMainComponent;
import simplecharacterbuilder.common.uicomponents.ContentPanel;

@SuppressWarnings("serial")
public class AdditionalBodySpriteLoader extends ContentPanel {
	private static final int YPOS = GAP_WIDTH + 189;
	static final int WIDTH = MAINPANEL_WIDTH - CONTROLPANEL_WIDTH - 3 * GAP_WIDTH;
	static final int HEIGHT = MAINPANEL_HEIGHT - GAP_WIDTH - YPOS;

	private static final int LOAD_BUTTON_HEIGHT = 35;
	private static final int PREVIEW_HEIGHT = HEIGHT - LOAD_BUTTON_HEIGHT + 1;
	private static final int PREVIEW_WIDTH = (int) (PREVIEW_HEIGHT / 192.0 * 128);
	private static final int LIST_XPOS = 0;
	private static final int LIST_YPOS = 34;
	private static final int LIST_WIDTH = WIDTH - PREVIEW_WIDTH + 1;
	private static final int LIST_HEIGHT = HEIGHT - 65;
	private static final int REMOVE_BUTTON_HEIGHT = HEIGHT - LOAD_BUTTON_HEIGHT - LIST_HEIGHT + 2;

	private final PreviewLabel combinedPreviewLabel;
	
	private final Map<String, File> bodyParts = new HashMap<>();

	private String[] additionalBodyPartOptions;

	private Component applicationFrame;
	private JLabel previewLabel;
	private JList<String> bodyPartList;
	private JPanel listContainer;
	private JButton removeButton;
	
	private boolean areListComponentsDisplayed = false;
	
	private String lastPreviewed;

	AdditionalBodySpriteLoader(PreviewLabel combinedPreviewLabel) {
		super(GAP_WIDTH, YPOS, WIDTH, HEIGHT);
		this.combinedPreviewLabel = combinedPreviewLabel;
		addSpritePreview();
		addLoadButton();
		addBodyPartList();
		addRemoveButton();
	}

	private void addLoadButton() {
		JButton loadButton = new JButton("Load additional body part (optional)");
		loadButton.setBounds(0, 0, WIDTH, LOAD_BUTTON_HEIGHT);
		loadButton.setBorder(CharacterBuilderMainComponent.BORDER);
		loadButton.setToolTipText("Click to load additional body part sprites. Make sure to consult experienced spriters before using these!");
		loadButton.addActionListener(e -> loadAdditionalSprite());
		this.add(loadButton);
	}

	private void addBodyPartList() {
		ListComponentDto dto = UIComponentFactory.createList(LIST_XPOS, LIST_YPOS, LIST_WIDTH, LIST_HEIGHT);

		this.listContainer = dto.getContainer();
		this.listContainer.setVisible(this.areListComponentsDisplayed);
		this.add(listContainer);

		this.bodyPartList = dto.getList();
		this.bodyPartList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                if (!arg0.getValueIsAdjusting()) {
                	List<String> selectedBodyParts = bodyPartList.getSelectedValuesList();
                	if(selectedBodyParts.size() != 1) {
                		setPreview(null);
                	} else {
                		AdditionalBodySpriteLoader.this.lastPreviewed = selectedBodyParts.get(0);
                		setPreview(bodyParts.get(AdditionalBodySpriteLoader.this.lastPreviewed));
                	}
                }
            }
        });
	}

	private JLabel addSpritePreview() {
		this.previewLabel = new JLabel();
		this.previewLabel.setBounds(WIDTH - PREVIEW_WIDTH, LIST_YPOS, PREVIEW_WIDTH, PREVIEW_HEIGHT);
		this.previewLabel.setBorder(CharacterBuilderMainComponent.BORDER);
		this.previewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		this.previewLabel.setVisible(this.areListComponentsDisplayed);
		this.add(previewLabel);
		return previewLabel;
	}

	private void addRemoveButton() {
		this.removeButton = new JButton("Remove selected body part");
		this.removeButton.setBounds(0, HEIGHT - REMOVE_BUTTON_HEIGHT, LIST_WIDTH, REMOVE_BUTTON_HEIGHT);
		this.removeButton.setBorder(CharacterBuilderMainComponent.BORDER);
		this.removeButton.setVisible(this.areListComponentsDisplayed);
		this.removeButton.addActionListener(e -> {
			this.bodyPartList.getSelectedValuesList().stream().forEach(v -> {
				this.bodyParts.remove(v);
				BodyImageFileHolder.remove(v);
				if(v != null && v.equals(AdditionalBodySpriteLoader.this.lastPreviewed)) {
					setPreview(null);
					AdditionalBodySpriteLoader.this.lastPreviewed = null;
				}
			});
			updateDisplayedBodyPartList();
			combinedPreviewLabel.update();
			displayListComponents(this.bodyParts.size() > 0);
		});
		this.add(this.removeButton);
	}

	private void loadAdditionalSprite() {
		String spriteType = getSpriteTypeFromUser();
		if (spriteType == null) {
			return;
		}
		File spriteFile = loadAdditionalSpriteFile();
		if (spriteFile == null) {
			return;
		}
		this.bodyParts.put(spriteType, spriteFile);
		updateDisplayedBodyPartList();
		BodyImageFileHolder.put(spriteType, spriteFile);
		combinedPreviewLabel.update();
		displayListComponents(true);
	}

	private String getSpriteTypeFromUser() {
		return (String) JOptionPane.showInputDialog(getApplicationFrame(), "Select the body part you want to add", "Select a Body Part",
				JOptionPane.QUESTION_MESSAGE, null, getAdditionalBodyPartOptions(), getAdditionalBodyPartOptions()[0]);
	}

	private File loadAdditionalSpriteFile() {
		JFileChooser fileChooser = JFileChooserPool
				.getPngFileChooser("Select the sprite for the body part (128x192px and png-format)");
		try {
			fileChooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
			if (fileChooser.showOpenDialog(getApplicationFrame()) != JFileChooser.APPROVE_OPTION) {
				return null;
			}
			File selectedFile = fileChooser.getSelectedFile();
			Image image = new ImageIcon(selectedFile.getAbsolutePath()).getImage();
			if (!selectedFile.getName().endsWith(".png") || image.getWidth(null) != 128
					|| image.getHeight(null) != 192) {
				JOptionPane.showMessageDialog(getApplicationFrame(), "Sprites must be 128x192px and in png-format.",
						"Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
			return selectedFile;
		} catch (Exception e) {
			throw new IllegalArgumentException("Loading additional body part sprite failed", e);
		}
	}

	private void updateDisplayedBodyPartList() {
		DefaultListModel<String> listModel = new DefaultListModel<>();
		bodyParts.keySet().stream().forEach(k -> listModel.addElement(k));
		this.bodyPartList.setModel(listModel);
	}

	private void setPreview(File spriteFile) {
		if(spriteFile == null || !spriteFile.exists()) {
			this.previewLabel.setIcon(null);
			return;
		}
		Image image = new ImageIcon(spriteFile.getAbsolutePath()).getImage();
		Image scaledImage = image.getScaledInstance(PREVIEW_WIDTH, PREVIEW_HEIGHT, Image.SCALE_SMOOTH);
		this.previewLabel.setIcon(new ImageIcon(scaledImage));
	}
	
	private String[] getAdditionalBodyPartOptions() {
		if(this.additionalBodyPartOptions == null) {
			List<String> bodyPartList = BodyPartRepository.getAdditionalBodyParts();
			bodyPartList.sort((a, b) -> a.equalsIgnoreCase("Hair (Back)") ? -1 : b.equalsIgnoreCase("Hair (Back)") ? 1 : a.compareTo(b));
			this.additionalBodyPartOptions = bodyPartList.toArray(new String[0]);
		}		
		return this.additionalBodyPartOptions; 
	}
	
	private void displayListComponents(boolean display) {
		if(display != areListComponentsDisplayed) {
			this.removeButton.setVisible(display);
			this.listContainer.setVisible(display);
			this.previewLabel.setVisible(display);
			this.areListComponentsDisplayed = display;
		}
	}

	private Component getApplicationFrame() {
		return applicationFrame == null ? applicationFrame = (JFrame) SwingUtilities.getWindowAncestor(this)
				: applicationFrame;
	}

}
