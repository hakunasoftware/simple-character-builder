package simplecharacterbuilder.common.resourceaccess;

import java.io.File;

public class GameFileAccessor {
	public static final GameFileAccessor INSTANCE = new GameFileAccessor();
	
	public static final String DIRECTORY_NAME_REGEX = "^[0-9a-zA-Z_\\-. \\+'!#;&%$()=,@~]+";

	private File rootDirectory;

	private ConfigReader configReader;

	private GameFileAccessor() {
	}

	public static void init() {
		INSTANCE.configReader = ConfigReaderRepository.getPathVariablesConfigReader();
		INSTANCE.rootDirectory = new File(INSTANCE.configReader.readString(PropertyRepository.GAME_ROOT_DIRECTORY));
	}

	public static File getFileFromProperty(String propertyName) {
		return new File(INSTANCE.rootDirectory, ConfigReaderRepository.getPathVariablesConfigReader().readString(propertyName));
	}

	public static File getFileFromCharacterbuilderProperty(String propertyName) {
		return new File(INSTANCE.rootDirectory, ConfigReaderRepository.getCharacterbuilderConfigReader().readString(propertyName));
	}
}
