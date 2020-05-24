package simplecharacterbuilder.characterbuilder.bodysprites;

import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.CONTROLPANEL_WIDTH;
import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.GAP_WIDTH;
import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.MAINPANEL_HEIGHT;
import static simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.MAINPANEL_WIDTH;

import java.awt.GridLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
	
	AdditionalBodySpriteLoader () {
		super(GAP_WIDTH, YPOS, WIDTH, HEIGHT);
		addLoadButton();
		addBodyPartList();
		addSpritePreview();
		addRemoveButton();
	}

	private void addLoadButton() {
		JButton loadButton = new JButton("Load additional body part (optional)");
		loadButton.setBounds(0, 0, WIDTH, LOAD_BUTTON_HEIGHT);
		loadButton.setBorder(CharacterBuilderMainComponent.BORDER);
		loadButton.setToolTipText("Click to load additional body part sprites. Make sure to consult experienced spriters before using this option!");
		this.add(loadButton);
	}

	private void addBodyPartList() {
		JPanel listContainer = new JPanel();
		listContainer.setBounds(LIST_XPOS, LIST_YPOS, LIST_WIDTH, LIST_HEIGHT);
		listContainer.setBorder(CharacterBuilderMainComponent.BORDER);
		listContainer.setLayout(new GridLayout(1,1));
		this.add(listContainer);
		
		DefaultListModel<String> model = new DefaultListModel<>();
		JList<String> bodyPartList = new JList<>(model);
		bodyPartList.setBorder(null);
		bodyPartList.setBackground(null);
		JScrollPane scrollPane = new JScrollPane(bodyPartList);
		scrollPane.setBorder(null);
		listContainer.add(scrollPane);
	}

	private void addSpritePreview() {
		JLabel previewLabel = new JLabel();
		previewLabel.setBounds(WIDTH - PREVIEW_WIDTH, LIST_YPOS, PREVIEW_WIDTH, PREVIEW_HEIGHT);
		previewLabel.setBorder(CharacterBuilderMainComponent.BORDER);
		this.add(previewLabel);
	}
	
	private void addRemoveButton() {
		JButton removeButton = new JButton("Remove selected body part");
		removeButton.setBounds(0, HEIGHT - REMOVE_BUTTON_HEIGHT, LIST_WIDTH, REMOVE_BUTTON_HEIGHT);
		removeButton.setBorder(CharacterBuilderMainComponent.BORDER);
		removeButton.setEnabled(false);
		this.add(removeButton);
	}

}
