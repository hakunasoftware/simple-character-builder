package simplecharacterbuilder.characterbuilder.util.holder;

import java.io.File;
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

	private final Map<String, List<EquipTypeType>> categories = new HashMap<>();
	private final Map<String, EquipTypeType> equipTypes = new HashMap<>();

	private EquipTypeRepository() {
	}

	public static void init() {
		FileUtils.listFiles(GameFileAccessor.getFileFromProperty(PropertyRepository.EQUIPTYPE_FOLDER),
				new String[] { "xml" }, false).stream().forEach(f -> INSTANCE.readEquipTypesFromFile(f));
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

			this.categories.put(file.getName().substring(2, file.getName().length() - 4), types);
			types.stream().forEach(e -> {
				this.equipTypes.put(e.getName(), e);
			});
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to parse EquipTypes", e);
		}
	}

}
