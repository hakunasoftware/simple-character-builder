package simplecharacterbuilder.characterbuilder.util.holder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import simplecharacterbuilder.common.generated.TraitType;
import simplecharacterbuilder.common.generated.Traits;
import simplecharacterbuilder.common.resourceaccess.GameFileAccessor;
import simplecharacterbuilder.common.resourceaccess.PropertyRepository;

public class TraitRepository {
	private static final TraitRepository INSTANCE = new TraitRepository();

	private final List<TraitType> personalityTraits = new ArrayList<>();
	private final List<TraitType> backgroundTraits = new ArrayList<>();
	private final List<TraitType> physicalTraits = new ArrayList<>();

	private final Unmarshaller unmarshaller = JAXBContextHolder.createUnmarshaller();

	private TraitRepository() {
	}

	public static void init() {
		try {
			mapTraitsFromFileToList(INSTANCE.personalityTraits, PropertyRepository.TRAITFOLDER_PERSONALITY);
			mapTraitsFromFileToList(INSTANCE.backgroundTraits, PropertyRepository.TRAITFOLDER_BACKGROUND);
			mapTraitsFromFileToList(INSTANCE.physicalTraits, PropertyRepository.TRAITFOLDER_PHYSICAL);
		} catch (JAXBException e) {
			throw new IllegalArgumentException("Parsing of trait xmls failed", e);
		}
	}

	public static List<TraitType> getPersonalityTraits() {
		return new ArrayList<>(INSTANCE.personalityTraits);
	}

	public static List<TraitType> getBackgroundTraits() {
		return new ArrayList<>(INSTANCE.backgroundTraits);
	}

	public static List<TraitType> getPhysicalTraits() {
		return new ArrayList<>(INSTANCE.physicalTraits);
	}

	private static void mapTraitsFromFileToList(List<TraitType> traits, String folderProperty) throws JAXBException {
		File folder = GameFileAccessor.getFileFromProperty(folderProperty);
		for (File file : folder.listFiles(f -> f.getName().endsWith(".xml"))) {
			traits.addAll(readTraitsFromXml(file));
		}
	}

	@SuppressWarnings("unchecked")
	private static List<TraitType> readTraitsFromXml(File xml) throws JAXBException {
		Object unmarshalledTraits = INSTANCE.unmarshaller.unmarshal(xml);
		return unmarshalledTraits instanceof Traits ? ((Traits) unmarshalledTraits).getTrait() 
				: Arrays.asList(((JAXBElement<TraitType>) unmarshalledTraits).getValue());
	}
}
