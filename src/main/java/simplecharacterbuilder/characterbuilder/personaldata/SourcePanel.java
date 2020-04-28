package simplecharacterbuilder.characterbuilder.personaldata;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import simplecharacterbuilder.characterbuilder.util.SearchableComboBox;
import simplecharacterbuilder.common.resourceaccess.GameFileAccessor;
import simplecharacterbuilder.common.resourceaccess.PropertyRepository;
import simplecharacterbuilder.common.uicomponents.ContentPanel;

@SuppressWarnings("serial")
public class SourcePanel extends ContentPanel {
	private static final String[] PRIMARY_SOURCE_TYPES = new String[] { "Anime", "Manga", "Game", "Light Novel",
			"Eroge", "Other" };
	private static final String[] SECONDARY_SOURCE_TYPES = new String[] { "<None>", "Anime", "Manga", "Game",
			"Light Novel", "Eroge", "Other" };

	private static final int COMPONENT_HEIGHT = 20;
	private static final int YPOS_FIRST_PANEL = 10;
	private static final int YPOS_NEXT_PANEL = 25;

	private static final String FRANCHISE = "Franchise";
	private static final String INSTALLMENT = "Installment";
	private static final String PRIMARY_SOURCE_TYPE = "1st Type";
	private static final String SECONDARY_SOURCE_TYPE = "2nd Type";
	
	private static final String DIRECTORY_NAME_REGEX = "^[0-9a-zA-Z_\\-. \\+'!#;&%$()=,@~]+";

	private static final File ACTORS_DIRECTORY = GameFileAccessor.getFileFromProperty(PropertyRepository.ACTORS_FOLDER);

	private final Map<String, JComboBox<String>> comboBoxes = new HashMap<>();

	private SearchableComboBox franchiseComboBox;
	private SearchableComboBox installmentComboBox;

	public SourcePanel(int x, int y, int width, int height) {
		super(x, y, width, height);

		createFranchiseOptions();
		createInstallmentOptions();
		createPrimaryTypeOptions();
		createSecondaryOptions();
	}

	private void createFranchiseOptions() {
		int yPos = YPOS_FIRST_PANEL;
		String tooltip = "Select or add the franchise the character is from (i.e. 'Fate'). The entered value must be a valid filename.";
		createFormattedLabel(FRANCHISE + ":", 0, yPos, 75, COMPONENT_HEIGHT);
		String[] options = ACTORS_DIRECTORY.list((f, n) -> f.isDirectory());
		this.franchiseComboBox = (SearchableComboBox) createComboBox(FRANCHISE, options, 85, yPos, 108, tooltip, true);
		this.franchiseComboBox.setRegex(DIRECTORY_NAME_REGEX);
		this.franchiseComboBox.addSelectionListener(() -> {
			String[] installmentOptions = determineInstallmentOptions();
			installmentComboBox.setOptions(installmentOptions);
			String currentInstallmentSelection = (String) installmentComboBox.getEditor().getItem();
			if(Arrays.binarySearch(installmentOptions, currentInstallmentSelection) < 0) {
				installmentComboBox.getEditor().setItem("");
				installmentComboBox.hidePopup();
			}
		});
	}

	private void createInstallmentOptions() {
		int yPos = YPOS_FIRST_PANEL + YPOS_NEXT_PANEL;
		String tooltip = "<html>Select or add the installment of the franchise the character is from (i.e. 'Zero' for Fate Zero).<br/>Only add this if necessary. The entered value must be a valid filename.</html>";
		createFormattedLabel(INSTALLMENT + ":", 0, yPos, 75, COMPONENT_HEIGHT);
		this.installmentComboBox = (SearchableComboBox) createComboBox(INSTALLMENT, new String[] {}, 85, yPos, 108,
				tooltip, true);
		this.installmentComboBox.setRegex(DIRECTORY_NAME_REGEX);
		this.installmentComboBox.getEditor().getEditorComponent().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent evt) {
				installmentComboBox.setOptions(determineInstallmentOptions());
				installmentComboBox.showPopup();
			}
		});
	}

	private String[] determineInstallmentOptions() {
		String selectedFranchiseName = (String) this.franchiseComboBox.getEditor().getItem();
		if (selectedFranchiseName == null || selectedFranchiseName.equals("")) {
			return new String[] {};
		}
		File selectedFranchise = new File(ACTORS_DIRECTORY, selectedFranchiseName);
		if (!selectedFranchise.exists()) {
			return new String[] {};
		}
		String[] firstTierSubdirectories = selectedFranchise.list((f, n) -> f.isDirectory());
		if (firstTierSubdirectories.length >= 0) {
			boolean containsInfoXml = new File(selectedFranchise, firstTierSubdirectories[0])
					.list((f, n) -> n.equals("Info.xml")).length > 0;
			if (!containsInfoXml) {
				return firstTierSubdirectories;
			}
		}
		return new String[] {};
	}

	private void createPrimaryTypeOptions() {
		int yPos = YPOS_FIRST_PANEL + 2 * YPOS_NEXT_PANEL;
		String tooltip = "Select the type of the source the character first appeared in (compare 2nd Type).";
		createFormattedLabel(PRIMARY_SOURCE_TYPE + ":", 0, yPos, 75, COMPONENT_HEIGHT);
		createComboBox(PRIMARY_SOURCE_TYPE, PRIMARY_SOURCE_TYPES, 85, yPos, 108, tooltip, false);
	}

	private void createSecondaryOptions() {
		int yPos = YPOS_FIRST_PANEL + 3 * YPOS_NEXT_PANEL;
		String tooltip = "<html>Select the type of the source you based your implementation on if it's not the same as the 1st Type.<br/>Example: If you based your implementation on an anime adaption of a manga, the 1st Type would<br/>be 'Manga' and the 2nd Type 'Anime'.</html>";
		createFormattedLabel(SECONDARY_SOURCE_TYPE + ":", 0, yPos, 75, COMPONENT_HEIGHT);
		createComboBox(SECONDARY_SOURCE_TYPE, SECONDARY_SOURCE_TYPES, 85, yPos, 108, tooltip, false);
	}

	private JLabel createFormattedLabel(String text, int xPos, int yPos, int width, int height) {
		JLabel label = new JLabel(text);
		label.setBounds(xPos, yPos, width, height);
		label.setHorizontalAlignment(JLabel.RIGHT);
		label.setVerticalAlignment(JLabel.CENTER);
		this.add(label);
		return label;
	}

	private JComboBox<String> createComboBox(String name, String[] options, int xPos, int yPos, int width,
			String tooltip, boolean searchable) {
		JComboBox<String> comboBox = searchable ? new SearchableComboBox(options) : new JComboBox<String>(options);
		if (options.length > 0) {
			comboBox.setSelectedIndex(0);
		}
		comboBox.setBounds(xPos, yPos, width, 20);
		comboBox.setToolTipText(tooltip);
		this.comboBoxes.put(name, comboBox);
		this.add(comboBox);
		return comboBox;
	}

}
