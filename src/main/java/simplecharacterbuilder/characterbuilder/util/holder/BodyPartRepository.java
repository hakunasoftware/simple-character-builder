package simplecharacterbuilder.characterbuilder.util.holder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import simplecharacterbuilder.common.generated.BodyParts;
import simplecharacterbuilder.common.generated.BodyParts.BodyPart;
import simplecharacterbuilder.common.generated.BodyParts.BodyPart.ExtraLayers;
import simplecharacterbuilder.common.resourceaccess.GameFileAccessor;
import simplecharacterbuilder.common.resourceaccess.PropertyRepository;

public class BodyPartRepository {
	private static final BodyPartRepository INSTANCE = new BodyPartRepository();
	
	private final Map<String, Long> bodyPartMap = new HashMap<>();
	
	private List<String> additionalBodyParts;
	
	private BodyPartRepository() {}
	
	public static void init() {
		try {
			readBodyPartsFromFile(GameFileAccessor.getFileFromProperty(PropertyRepository.RETRACTABLE_BODY_PARTS));
			readBodyPartsFromFile(GameFileAccessor.getFileFromProperty(PropertyRepository.NON_RETRACTABLE_BODY_PARTS));
		} catch (JAXBException e) {
			throw new IllegalArgumentException("Parsing of body parts failed", e);
		}
	}
	
	public static List<String> getAdditionalBodyParts() {
		if(INSTANCE.additionalBodyParts == null) {
			INSTANCE.additionalBodyParts = INSTANCE.bodyPartMap.keySet().stream()
					.filter(k -> !ImageFileHolder.BODY.equals(k) && !ImageFileHolder.HAIR.equals(k))
					.collect(Collectors.toList());
		}
		return new ArrayList<>(INSTANCE.additionalBodyParts);
	}
	
	public static Long getDrawIndex(String bodyPart) {
		return INSTANCE.bodyPartMap.get(bodyPart);
	}
	
	private static void readBodyPartsFromFile(File file) throws JAXBException {
		BodyParts bodyParts = (BodyParts) JAXBContextHolder.createUnmarshaller().unmarshal(file);
		for(BodyPart bodyPart : bodyParts.getBodyPart()) {
			INSTANCE.bodyPartMap.put(bodyPart.getName(), DrawIndexRepository.parse(bodyPart.getDrawIndex()));
			ExtraLayers extraLayers = bodyPart.getExtraLayers();
			if(extraLayers != null) {
				extraLayers.getExtra().stream().forEach(e -> {INSTANCE.bodyPartMap.put(e.getName(), DrawIndexRepository.parse(e.getDrawIndex()));});
			}
		}
	}
}
