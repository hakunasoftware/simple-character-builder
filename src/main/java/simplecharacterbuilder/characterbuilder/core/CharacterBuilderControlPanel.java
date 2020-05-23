package simplecharacterbuilder.characterbuilder.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import simplecharacterbuilder.characterbuilder.util.ImageFileHolder;
import simplecharacterbuilder.characterbuilder.util.ValueFormatter;
import simplecharacterbuilder.common.generated.Actor;
import simplecharacterbuilder.common.generated.ObjectFactory;
import simplecharacterbuilder.common.resourceaccess.GameFileAccessor;
import simplecharacterbuilder.common.resourceaccess.PropertyRepository;
import simplecharacterbuilder.common.statgenerator.StatGenerator;
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent;
import simplecharacterbuilder.common.uicomponents.ControlPanel;

public class CharacterBuilderControlPanel extends ControlPanel {
	private static final int X_POS = MAINPANEL_WIDTH - CONTROLPANEL_WIDTH - GAP_WIDTH;
	private static final int Y_POS = MAINPANEL_HEIGHT - CONTROLPANEL_HEIGHT - GAP_WIDTH;

	private static final CharacterBuilderControlPanel INSTANCE = new CharacterBuilderControlPanel(X_POS, Y_POS);
	
	private final Marshaller marshaller;

	private String firstName = "";
	private String middleName = "";
	private String lastName = "";
	private boolean isAsian;
	
	private final JButton saveButton;
	
	private Map<String, String> cachedInstallments;

	private List<CharacterBuilderMainComponent> mainComponents;
	private StatGenerator statGenerator;

	private int pageIndex = 0;

	private CharacterBuilderControlPanel(int x, int y) {
		super(x, y, false, "Back", "Next", null);

		this.saveButton = new ControlButton("Save", ControlButton.WIDTH_BASIC, false);
		this.saveButton.setVisible(false);
		this.mainPanel.add(saveButton);

		this.button1.addActionListener(e -> previous());
		this.button2.addActionListener(e -> next());
		this.saveButton.addActionListener(e -> save());

		this.button1.setEnabled(false);
		
		try {
			this.marshaller = JAXBContext.newInstance(ObjectFactory.class).createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		} catch (JAXBException e) {
			throw new RuntimeException("Creation of marshaller failed", e);
		}
	}

	public static CharacterBuilderControlPanel getInstance() {
		return INSTANCE;
	}

	private void next() {
		if (!this.button1.isEnabled()) {
			this.button1.setEnabled(true);
		}

		mainComponents.get(pageIndex).disable();
		mainComponents.get(++pageIndex).enable();

		if (pageIndex == mainComponents.size() - 1) {
			this.button2.setVisible(false);
			this.saveButton.setVisible(true);
		}
	}

	private void previous() {
		if (!this.button2.isVisible()) {
			this.saveButton.setVisible(false);
			this.button2.setVisible(true);
		}

		mainComponents.get(pageIndex).disable();
		mainComponents.get(--pageIndex).enable();

		if (pageIndex == 0) {
			this.button1.setEnabled(false);
		}
	}

	private synchronized void save() {
		Actor actor = new Actor();
		this.mainComponents.stream().forEach(c -> c.setValues(actor));
		
		List<String> verificationErrors = getVerificationErrors(actor);
		if(verificationErrors.size() > 0) {
			StringBuilder errorMsg = new StringBuilder("Values are missing or invalid: ");
			for(String error : verificationErrors) {
				errorMsg.append("\n- ").append(error);
			}
			JOptionPane.showMessageDialog(null, errorMsg.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(!statGenerator.confirmIntentIfWarningsExist())
		{
			return;
		}
		
		int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to write your input into the respective files?", "Confirm Saving", JOptionPane.YES_NO_OPTION);
		if(dialogResult != JOptionPane.YES_OPTION){
		  return;
		}
		
		StringWriter writer = new StringWriter();
		try {
			this.marshaller.marshal(actor, writer);
			Actor.Name name = actor.getName();
			String fullName = ValueFormatter.formatFullName(name.getFirst(), name.getMiddle(), name.getLast(), false);
			String xml = writer.toString().replace("<Actor>", "<Actor><!-- " + fullName + " -->");
			xml = addEmptyLinesAfterTags(xml, "</Likes>", "</Jobs>", "</Quest>", "</Body>", "</Source>", "</Stats>", "</Skills>");
			
			File characterFolder = getCharacterFolder(actor.getSource().getFranchise(), getInstallmentDirName(actor.getSource().getInstallment()), fullName);
			characterFolder.mkdir();
			writeStringToFile(xml, new File(characterFolder, "Info.xml"));
			System.out.println(xml);
			
			ImageFileHolder.copyImagesToTargetDirectory(characterFolder);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
	}

	private List<String> getVerificationErrors(Actor actor) {
		List<String> errors = new ArrayList<>();
		verifyActor(actor, errors);
		verifyImages(errors);
		return errors;
	}

	private void verifyActor(Actor actor, List<String> errors){
		if(actor.getName() == null || actor.getName().getFirst() == null && actor.getName().getLast() == null) {
			errors.add("Enter a first or a last name.");
		}
		if(actor.getSource() == null || actor.getSource().getFranchise() == null) {
			errors.add("Enter a franchise.");
		}
		if(!actor.getType().equals("Slave") && actor.getMaintenance() == null) {
			errors.add("Enter a maintenance fee or change the character's type to Slave.");
		}
	}
	
	private void verifyImages(List<String> errors) {
		verifyImage(errors, ImageFileHolder.PORTRAIT, "Load a portrait.");
	}
	
	private void verifyImage(List<String> errors, String imageName, String errorMessage) {
		File image = ImageFileHolder.get(imageName);
		if(image == null || !image.exists()) {
			errors.add(errorMessage);
		}
	}

	private String getInstallmentDirName(String selectedInstallment) {
		if(ValueFormatter.isEmpty(selectedInstallment)) {
			return null;
		}
		String cachedDir = cachedInstallments.get(selectedInstallment);
		if(cachedDir != null) {
			return cachedDir;
		}
		if(selectedInstallment.matches(GameFileAccessor.DIRECTORY_NAME_REGEX)) {
			return selectedInstallment;
		}
		return getInstallmentDirFromUserInput(selectedInstallment);
	}
	
	private String getInstallmentDirFromUserInput(String selectedInstallment) {
		String input = JOptionPane.showInputDialog("Please enter a valid directory name for the installment directory (i.e. remove special characters).", selectedInstallment);
		boolean isValid = !ValueFormatter.isEmpty(input) && input.matches(GameFileAccessor.DIRECTORY_NAME_REGEX);
		return isValid ? input : getInstallmentDirFromUserInput(selectedInstallment);
	}

	private File getCharacterFolder(String franchiseDirName, String installmentDirName, String fullCharacterName) {
		File parentDir = new File(GameFileAccessor.getFileFromProperty(PropertyRepository.ACTORS_FOLDER), franchiseDirName);
		if(!parentDir.exists()) {
			parentDir.mkdir();
		}
		if(installmentDirName != null) {
			parentDir = new File(parentDir, installmentDirName);
			if(!parentDir.exists()) {
				parentDir.mkdir();
			}
		}
		return new File(parentDir, fullCharacterName);
	}
	
	private void writeStringToFile(String input, File outputFile) throws IOException {
		try (FileWriter writer = new FileWriter(outputFile)){
			writer.write(input);
		}
	}

	private void updateNameLabel() {
		this.nameLabel.setText(ValueFormatter.formatFullName(firstName, middleName, lastName, isAsian));
	}
	
	private String addEmptyLinesAfterTags(String xml, String... tags) {
		for(String tag : tags) {
			xml = xml.replace(tag, tag + "\n");
		}
		return xml;
	}

	public void init(List<CharacterBuilderComponent> components) {
		mainComponents = components.stream().filter(c -> c instanceof CharacterBuilderMainComponent)
				.map(c -> (CharacterBuilderMainComponent) c).collect(Collectors.toList());
		statGenerator = (StatGenerator) mainComponents.stream().filter(c -> c instanceof StatGenerator).findFirst().get(); 
	}

	public static void setFirstName(String firstName) {
		INSTANCE.firstName = firstName;
		INSTANCE.updateNameLabel();
	}
	
	public static void setMiddleName(String middleName) {
		INSTANCE.middleName = middleName;
		INSTANCE.updateNameLabel();
	}

	public static void setLastName(String lastName) {
		INSTANCE.lastName = lastName;
		INSTANCE.updateNameLabel();
	}

	public static void setIsAsian(boolean isAsian) {
		INSTANCE.isAsian = isAsian;
		INSTANCE.updateNameLabel();
	}

	public Map<String, String> getCachedInstallments() {
		return cachedInstallments;
	}
	
	public void setCachedInstallments(Map<String, String> cachedInstallments) {
		this.cachedInstallments = cachedInstallments;
	}
	
}
