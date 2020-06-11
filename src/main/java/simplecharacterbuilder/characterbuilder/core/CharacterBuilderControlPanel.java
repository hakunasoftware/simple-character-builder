package simplecharacterbuilder.characterbuilder.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import simplecharacterbuilder.characterbuilder.maincomponents.various.SocialMainComponent;
import simplecharacterbuilder.characterbuilder.util.holder.BodyImageFileHolder;
import simplecharacterbuilder.characterbuilder.util.holder.JAXBContextHolder;
import simplecharacterbuilder.characterbuilder.util.holder.PostInfoXmlGenerationRunnableHolder;
import simplecharacterbuilder.characterbuilder.util.transform.ValueFormatter;
import simplecharacterbuilder.common.generated.Actor;
import simplecharacterbuilder.common.generated.Actor.Name;
import simplecharacterbuilder.common.generated.Actor.Source;
import simplecharacterbuilder.common.resourceaccess.GameFileAccessor;
import simplecharacterbuilder.common.resourceaccess.PropertyRepository;
import simplecharacterbuilder.common.statgenerator.StatGenerator;
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent;
import simplecharacterbuilder.common.uicomponents.ControlPanel;

public class CharacterBuilderControlPanel extends ControlPanel {
	public static final int X_POS = MAINPANEL_WIDTH - CONTROLPANEL_WIDTH - GAP_WIDTH;
	public static final int Y_POS = MAINPANEL_HEIGHT - CONTROLPANEL_HEIGHT - GAP_WIDTH;

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
		
		this.marshaller = JAXBContextHolder.createMarshaller();
	}

	public static CharacterBuilderControlPanel getInstance() {
		return INSTANCE;
	}

	public void next() {
		if (!this.button1.isEnabled()) {
			this.button1.setEnabled(true);
		}
		mainComponents.get(pageIndex).disable();
		mainComponents.get(++pageIndex).enable();

		if (pageIndex == mainComponents.size() - 1) {
			this.button2.setVisible(false);
			this.saveButton.setVisible(true);
		}
		
		if(mainComponents.get(pageIndex) instanceof SocialMainComponent && ((SocialMainComponent) mainComponents.get(pageIndex)).isEmpty()) {
			next();
		}
	}

	public void previous() {
		if (!this.button2.isVisible()) {
			this.saveButton.setVisible(false);
			this.button2.setVisible(true);
		}

		mainComponents.get(pageIndex).disable();
		mainComponents.get(--pageIndex).enable();

		if (pageIndex == 0) {
			this.button1.setEnabled(false);
		}
		
		if(mainComponents.get(pageIndex) instanceof SocialMainComponent && ((SocialMainComponent) mainComponents.get(pageIndex)).isEmpty()) {
			previous();
		}
	}

	private synchronized void save() {
		Actor actor = new Actor();
		this.mainComponents.stream().forEach(c -> c.setValues(actor));
//		verifyActor(actor);
		confirmIntent();
		
		try {
			String xml = marshallActorToInfoXmlString(actor);
			System.out.println("Generated Info.xml:\n" + xml);
			
//			File characterFolder = getCharacterFolder(actor.getSource().getFranchise(), getInstallmentDirName(actor.getSource().getInstallment()), fullName);
//			if(!characterFolder.mkdir()) {
//				JOptionPane.showMessageDialog(null, "Character folder could not be created - it may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
//				PostInfoXmlGenerationRunnableHolder.clear();
//				return;
//			}
//			writeStringToFile(xml, new File(characterFolder, "Info.xml"));
//			
//			BodyImageFileHolder.copyImagesToTargetDirectory(characterFolder);
			
			addActorToActorsList(actor);
			PostInfoXmlGenerationRunnableHolder.runAll();
			PostInfoXmlGenerationRunnableHolder.clear();
			
			JOptionPane.showMessageDialog(null, "Your informations were successfully saved! Don't forget to commit the changes and please do a quick check for any errors!");
		} catch (Exception e) {
			PostInfoXmlGenerationRunnableHolder.clear();
			JOptionPane.showMessageDialog(null, "An error occured during saving, sorry. Please report this so that it can be fixed! You may need to clean up some changes that were already made.", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private boolean verifyActor(Actor actor) {
		List<String> verificationErrors = getVerificationErrors(actor);
		boolean errorsFound = verificationErrors.size() > 0;
		if(errorsFound) {
			StringBuilder errorMsg = new StringBuilder("Values are missing or invalid: ");
			for(String error : verificationErrors) {
				errorMsg.append("\n- ").append(error);
			}
			JOptionPane.showMessageDialog(null, errorMsg.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			PostInfoXmlGenerationRunnableHolder.clear();
		}
		return errorsFound;
	}
	
	private void confirmIntent() {
		if(!statGenerator.confirmIntentIfWarningsExist()) {
			PostInfoXmlGenerationRunnableHolder.clear();
			return;
		}
		int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to write your input into the respective files?", "Confirm Saving", JOptionPane.YES_NO_OPTION);
		if(dialogResult != JOptionPane.YES_OPTION){
			PostInfoXmlGenerationRunnableHolder.clear();
			return;
		}
	}
	
	private String marshallActorToInfoXmlString(Actor actor) throws JAXBException {
		StringWriter writer = new StringWriter();
		this.marshaller.marshal(actor, writer);
		Actor.Name name = actor.getName();
		String fullName = ValueFormatter.formatFullName(name.getFirst(), name.getMiddle(), name.getLast(), false);
		String xml = writer.toString().replace("<Actor>", "<Actor><!-- " + fullName + " -->");
		xml = addEmptyLinesAfterTags(xml, "</Likes>", "</Jobs>", "</Quest>", "</Body>", "</Source>", "</Stats>", "</Skills>");
		return xml;
	}
	
	private void addActorToActorsList(Actor actor) throws IOException {
		Name name = actor.getName();
		String fullName = ValueFormatter.formatFullName(name.getFirst(), name.getMiddle(), name.getLast(), false);
		Source source = actor.getSource();
		String sourceString = ValueFormatter.isEmpty(source.getInstallment()) ? source.getFranchise() : source.getInstallment();
		File file = GameFileAccessor.getFileFromProperty(PropertyRepository.ACTORS_LIST);
		File tempFile = new File(file.getParentFile(), file.getName() + ".tmp");
		
		try(BufferedReader reader = new BufferedReader(new FileReader(file));
				PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tempFile)))) {
			boolean actorAdded = false;
			String line;
			while ((line = reader.readLine()) != null) {
				writer.println(line);
			    if (!actorAdded && line.contains(sourceString)) {
			    	writer.println(ValueFormatter.formatListEntry(fullName));
			    	actorAdded = true;
			    }
			}
			if(!actorAdded) {
				writer.println();
				writer.println("#---- " + sourceString + " ----");
				writer.println(ValueFormatter.formatListEntry(fullName));
			}
		}
		
		file.delete();
		tempFile.renameTo(file);
	}
	
	private List<String> getVerificationErrors(Actor actor) {
		List<String> errors = new ArrayList<>();
		verifyActor(actor, errors);
		verifyImages(errors);
		return errors;
	}

	private void verifyActor(Actor actor, List<String> errors){
		if(actor.getName() == null || ValueFormatter.isEmpty(actor.getName().getFirst()) && ValueFormatter.isEmpty(actor.getName().getLast())) {
			errors.add("Enter at least a first or a last name.");
		}
		if(actor.getName() != null && !ValueFormatter.isEmpty(actor.getName().getMiddle()) 
				&& (ValueFormatter.isEmpty(actor.getName().getFirst()) || ValueFormatter.isEmpty(actor.getName().getLast()))) {
			errors.add("To use a middle name, a first and a last name need to be set.");
		}
		if(actor.getSource() == null || ValueFormatter.isEmpty(actor.getSource().getFranchise())) {
			errors.add("Enter a franchise.");
		}
		if(!actor.getType().equals("Slave") && actor.getMaintenance() == null) {
			errors.add("Enter a maintenance fee or change the character's type to Slave.");
		}
	}
	
	private void verifyImages(List<String> errors) {
		verifyImage(errors, BodyImageFileHolder.PORTRAIT, "Load a portrait.");
		verifyImage(errors, BodyImageFileHolder.BODY, "Load a body sprite.");
		verifyImage(errors, BodyImageFileHolder.HAIR, "Load a hair sprite.");
	}
	
	private void verifyImage(List<String> errors, String imageName, String errorMessage) {
		File image = BodyImageFileHolder.get(imageName);
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
		
		for(int i = 1; i < mainComponents.size(); i++) {
			mainComponents.get(i).disable();
		}
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
