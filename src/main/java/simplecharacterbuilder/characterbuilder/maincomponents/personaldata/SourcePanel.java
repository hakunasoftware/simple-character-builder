package simplecharacterbuilder.characterbuilder.maincomponents.personaldata;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;

import simplecharacterbuilder.characterbuilder.core.CharacterBuilderControlPanel;
import simplecharacterbuilder.characterbuilder.util.transform.ValueFormatter;
import simplecharacterbuilder.characterbuilder.util.ui.DocumentChangeListener;
import simplecharacterbuilder.characterbuilder.util.ui.SearchableComboBox;
import simplecharacterbuilder.common.resourceaccess.GameFileAccessor;
import simplecharacterbuilder.common.resourceaccess.InfoXmlReaderWriter;
import simplecharacterbuilder.common.resourceaccess.PropertyRepository;
import simplecharacterbuilder.common.uicomponents.ContentPanel;

@SuppressWarnings("serial")
public class SourcePanel extends ContentPanel {
	private static final String[] PRIMARY_SOURCE_TYPES = new String[] { "Anime", "Manga", "Game", "Light Novel",
			"Eroge", "Other" };
	private static final String[] SECONDARY_SOURCE_TYPES = new String[] { ValueFormatter.NONE_OPTION, "Anime", 
			"Manga", "Game", "Light Novel", "Eroge", "Other" };

	private static final int COMPONENT_HEIGHT = 20;
	private static final int YPOS_FIRST_PANEL = 10;
	private static final int YPOS_NEXT_PANEL = 25;

	private static final String FRANCHISE = "Franchise";
	private static final String INSTALLMENT = "Installment";
	private static final String PRIMARY_SOURCE_TYPE = "1st Type";
	private static final String SECONDARY_SOURCE_TYPE = "2nd Type";

	private static final File ACTORS_DIRECTORY = GameFileAccessor.getFileFromProperty(PropertyRepository.ACTORS_FOLDER);

	private SearchableComboBox franchiseComboBox;
	private SearchableComboBox installmentComboBox;

	private JComboBox<String> primaryOptionsComboBox;
	private JComboBox<String> secondaryOptionsComboBox;

	public SourcePanel(int x, int y, int width, int height) {
		super(x, y, width, height);

		createFranchiseOptions();
		createInstallmentOptions();
		createPrimaryTypeOptions();
		createSecondaryOptions();
	}

	public String getSelectedFranchise() {
		return (String) this.franchiseComboBox.getEditor().getItem();
	}

	public String getSelectedInstallment() {
		return this.installmentComboBox.isEnabled() ? (String) this.installmentComboBox.getEditor().getItem() : null;
	}

	public String getPrimaryType() {
		return (String) primaryOptionsComboBox.getSelectedItem();
	}

	public String getSecondaryType() {
		return ValueFormatter.checkStringForNoneOption((String) secondaryOptionsComboBox.getSelectedItem());
	}

	private void createFranchiseOptions() {
		int yPos = YPOS_FIRST_PANEL;
		String tooltip = "Select or add the franchise the character is from (i.e. 'Fate'). The entered value must be a valid filename.";
		createFormattedLabel(FRANCHISE + ":", 0, yPos, 75, COMPONENT_HEIGHT);
		String[] options = ACTORS_DIRECTORY.list((f, n) -> f.isDirectory());
		this.franchiseComboBox = (SearchableComboBox) createComboBox(FRANCHISE, options, 85, yPos, 108, tooltip, true);
		this.franchiseComboBox.setRegex(GameFileAccessor.DIRECTORY_NAME_REGEX);
		this.franchiseComboBox.addSelectionListener(() -> {
			String[] installmentOptions = determineInstallmentOptions();
			installmentComboBox.setOptions(installmentOptions);
			String currentInstallmentSelection = (String) installmentComboBox.getEditor().getItem();
			if (Arrays.binarySearch(installmentOptions, currentInstallmentSelection) < 0) {
				installmentComboBox.getEditor().setItem("");
				if (installmentOptions.length > 0) {
					installmentComboBox.setSelectedIndex(0);
				}
				installmentComboBox.hidePopup();
			}
			if (installmentOptions.length == 0) {
				determineSourceTypes((String) this.franchiseComboBox.getEditor().getItem(), null);
				installmentComboBox.setEnabled(false);
			} else {
				installmentComboBox.setEnabled(true);
			}
		});
		JTextComponent editor = (JTextComponent) this.franchiseComboBox.getEditor().getEditorComponent();
		editor.getDocument().addDocumentListener(new DocumentChangeListener(() -> {
			CharacterBuilderControlPanel.getInstance().setFranchise(editor.getText());
			installmentComboBox.setEnabled(true);
		}));
	}

	private void createInstallmentOptions() {
		int yPos = YPOS_FIRST_PANEL + YPOS_NEXT_PANEL;
		String tooltip = "<html>Select or add the installment of the franchise the character is from. This will replace the franchise,<br/>so make sure to use the full name (i.e. 'Final Fantasy VII' and not just 'VII'). Only add this if necessary.</html>";
		createFormattedLabel(INSTALLMENT + ":", 0, yPos, 75, COMPONENT_HEIGHT);
		this.installmentComboBox = (SearchableComboBox) createComboBox(INSTALLMENT, new String[] {}, 85, yPos, 108,
				tooltip, true);
		this.installmentComboBox.getEditor().getEditorComponent().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent evt) {
				installmentComboBox.setOptions(determineInstallmentOptions());
				installmentComboBox.showPopup();
			}
		});

		this.installmentComboBox.addSelectionListener(() -> {
			String currentInstallment = (String) this.installmentComboBox.getEditor().getItem();
			if ("".equals(currentInstallment)) {
				return;
			}
			String currentFranchise = (String) this.franchiseComboBox.getEditor().getItem();
			determineSourceTypes(currentFranchise,
					CharacterBuilderControlPanel.getInstance().getCachedInstallments().get(currentInstallment));
		});
	}

	private String[] determineInstallmentOptions() {
		String selectedFranchise = (String) this.franchiseComboBox.getEditor().getItem();
		Map<String, String> installments = new HashMap<>();
		for (String instOpt : determineInstallmentSubfolders(selectedFranchise)) {
			installments.put(determineInstallment(selectedFranchise, instOpt), instOpt);
		}
		CharacterBuilderControlPanel.getInstance().setCachedInstallments(installments);
		return installments.keySet().toArray(new String[0]);
	}

	private String[] determineInstallmentSubfolders(String selectedFranchise) {

		if (ValueFormatter.isEmpty(selectedFranchise)) {
			return new String[] {};
		}
		File selectedFranchiseFolder = new File(ACTORS_DIRECTORY, selectedFranchise);
		if (!selectedFranchiseFolder.exists()) {
			return new String[] {};
		}
		String[] firstTierSubdirectories = selectedFranchiseFolder.list((f, n) -> f.isDirectory());
		if (firstTierSubdirectories.length >= 0) {
			boolean containsInfoXml = new File(selectedFranchiseFolder, firstTierSubdirectories[0])
					.list((f, n) -> n.equals("Info.xml")).length > 0;
			if (!containsInfoXml) {
				return firstTierSubdirectories;
			}
		}
		return new String[] {};
	}

	private String determineInstallment(String franchise, String installmentFolder) {
		return readInstallmentFromInfoXml(findExampleInfoXml(franchise, installmentFolder));
	}

	private void createPrimaryTypeOptions() {
		int yPos = YPOS_FIRST_PANEL + 2 * YPOS_NEXT_PANEL;
		String tooltip = "Select the type of the source the character first appeared in (compare 2nd Type).";
		createFormattedLabel(PRIMARY_SOURCE_TYPE + ":", 0, yPos, 75, COMPONENT_HEIGHT);
		this.primaryOptionsComboBox = createComboBox(PRIMARY_SOURCE_TYPE, PRIMARY_SOURCE_TYPES, 85, yPos, 108, tooltip,
				false);
	}

	private void createSecondaryOptions() {
		int yPos = YPOS_FIRST_PANEL + 3 * YPOS_NEXT_PANEL;
		String tooltip = "<html>Select the type of the source you based your implementation on if it's not the same as the 1st Type.<br/>Example: If you based your implementation on an anime adaption of a manga, the 1st Type would<br/>be 'Manga' and the 2nd Type 'Anime'.</html>";
		createFormattedLabel(SECONDARY_SOURCE_TYPE + ":", 0, yPos, 75, COMPONENT_HEIGHT);
		this.secondaryOptionsComboBox = createComboBox(SECONDARY_SOURCE_TYPE, SECONDARY_SOURCE_TYPES, 85, yPos, 108,
				tooltip, false);
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
		this.add(comboBox);
		return comboBox;
	}

	private void determineSourceTypes(String franchise, String installment) {
		if (franchise == null || "".equals(franchise)) {
			return;
		}
		String[] sourceTypes = readSourceTypesFromInfoXml(findExampleInfoXml(franchise, installment)).split("\\|");
		primaryOptionsComboBox.setSelectedIndex(findIndex(PRIMARY_SOURCE_TYPES, sourceTypes[0].trim()));
		String secondarySourceType = sourceTypes.length > 1 ? sourceTypes[1].trim() : ValueFormatter.NONE_OPTION;
		secondaryOptionsComboBox.setSelectedIndex(findIndex(SECONDARY_SOURCE_TYPES, secondarySourceType));
	}

	private File findExampleInfoXml(String franchise, String installment) {
		try {
			File selectionDirectory = new File(ACTORS_DIRECTORY, franchise);
			if (installment != null) {
				selectionDirectory = new File(selectionDirectory, installment);
			}
			return Arrays.asList(selectionDirectory.listFiles()[0].listFiles()).stream()
					.filter(f -> "Info.xml".equalsIgnoreCase(f.getName())).findFirst().get();
		} catch (Exception e) {
			throw new IllegalArgumentException("No Info.xml found for " + franchise + "-" + installment, e);
		}
	}

	private String readSourceTypesFromInfoXml(File infoXml) {
		return new InfoXmlReaderWriter(infoXml.getAbsolutePath()).readStringFromUniqueTagPath("Source/Type");
	}

	private String readInstallmentFromInfoXml(File infoXml) {
		return new InfoXmlReaderWriter(infoXml.getAbsolutePath()).readStringFromUniqueTagPath("Source/Installment");
	}

	private int findIndex(String[] array, String string) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equalsIgnoreCase(string)) {
				return i;
			}
		}
		throw new IllegalArgumentException("The String " + string + " is not a valid source type");
	}

}
