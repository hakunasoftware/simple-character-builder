package simplecharacterbuilder.characterbuilder.util.holder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;

import org.apache.commons.io.FileUtils;

import simplecharacterbuilder.common.generated.EquipTypeType;
import simplecharacterbuilder.common.generated.EquipTypes;
import simplecharacterbuilder.common.resourceaccess.ConfigReaderRepository;
import simplecharacterbuilder.common.resourceaccess.GameFileAccessor;
import simplecharacterbuilder.common.resourceaccess.PropertyRepository;

public class EquipTypeRepository {
	private static final EquipTypeRepository INSTANCE = new EquipTypeRepository();

	private final Map<String, List<String>> categories = new HashMap<>();
	private final Map<String, EquipTypeType> equipTypes = new HashMap<>();

	private EquipTypeRepository() {
	}

	public static void init() {
		FileUtils.listFiles(GameFileAccessor.getFileFromProperty(PropertyRepository.EQUIPTYPE_FOLDER),
				new String[] { "xml" }, false).stream().forEach(f -> INSTANCE.readEquipTypesFromFile(f));
	}

	public static List<String> getCategories() {
		return new ArrayList<>(INSTANCE.categories.keySet());
	}
	
	public static List<String> getEquipTypesFromCategory(String category){
		return INSTANCE.categories.get(category);
	}
	
	public static EquipTypeType getEquipType(String equipType) {
		return INSTANCE.equipTypes.get(equipType);
	}
	
	public static String getCategoryOfEquipType(String equipType) {
		for(String category : INSTANCE.categories.keySet()) {
			if(INSTANCE.categories.get(category).contains(equipType)) {
				return category;
			}
		}
		throw new IllegalArgumentException("Error finding category of " + equipType);
	}

	@SuppressWarnings("unchecked")
	private void readEquipTypesFromFile(File file) {
		try {
			List<String> ignoredTypes = Arrays
					.asList(ConfigReaderRepository.getCharacterbuilderConfigReader()
							.readString(PropertyRepository.IGNORED_EQUIPTYPES).split(","))
					.stream().map(s -> s.trim()).collect(Collectors.toList());

			Object object = JAXBContextHolder.createUnmarshaller().unmarshal(file);
			List<EquipTypeType> types = (object instanceof EquipTypes ? ((EquipTypes) object).getEquipType()
					: Arrays.asList(((JAXBElement<EquipTypeType>) object).getValue())).stream()
							.filter(e -> !ignoredTypes.contains(e.getName())).collect(Collectors.toList());
			if(types.size() > 0) {
				this.categories.put(file.getName().substring(2, file.getName().length() - 4), 
						types.stream().map(t -> t.getName()).collect(Collectors.toList()));
				
				types.stream().forEach(e -> {this.equipTypes.put(e.getName(), e);});
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to parse EquipTypes", e);
		}
	}
}
