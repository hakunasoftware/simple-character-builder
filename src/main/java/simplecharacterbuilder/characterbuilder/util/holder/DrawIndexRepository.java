package simplecharacterbuilder.characterbuilder.util.holder;

import java.util.HashMap;
import java.util.Map;

import simplecharacterbuilder.common.resourceaccess.ConfigReader;
import simplecharacterbuilder.common.resourceaccess.GameFileAccessor;
import simplecharacterbuilder.common.resourceaccess.PropertyRepository;

public class DrawIndexRepository {
	private static final DrawIndexRepository INSTANCE = new DrawIndexRepository();
	
	private final Map<String, Long> drawIndices = new HashMap<>();

	private DrawIndexRepository() {}
	
	public static void init() {
		Map<String, Long> drawIndices = INSTANCE.drawIndices;
		ConfigReader configReader = new ConfigReader(GameFileAccessor.getFileFromProperty(PropertyRepository.DRAW_INDICES));
		configReader.readAllKeys().stream().forEach(k -> {drawIndices.put(configReader.readString(k), Long.parseLong(k, 16));});
	}
	
	public static Long parse(String drawIndexName) {
		return INSTANCE.drawIndices.get(drawIndexName);
	}
}
