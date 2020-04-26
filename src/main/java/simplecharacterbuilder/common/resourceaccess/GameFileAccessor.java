package simplecharacterbuilder.common.resourceaccess;

import java.io.File;

public class GameFileAccessor {
	public static final GameFileAccessor INSTANCE = new GameFileAccessor();

	private File rootDirectory;

	private ConfigReader configReader;

	private GameFileAccessor() {
	}

	public static void init() {
		INSTANCE.configReader = ConfigReaderRepository.getPathVariablesConfigReader();
		INSTANCE.rootDirectory = new File(INSTANCE.configReader.readString(PropertyRepository.GAME_ROOT_DIRECTORY));
	}

	public static File getFileFromProperty(String propertyName) {
		return new File(INSTANCE.rootDirectory,
				ConfigReaderRepository.getPathVariablesConfigReader().readString(propertyName));
	}
}
