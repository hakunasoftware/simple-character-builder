package simplecharacterbuilder.common.resourceaccess;

import java.io.File;

public class ConfigReaderRepository {
	private static final ConfigReaderRepository INSTANCE = new ConfigReaderRepository();
	
	private static final String CONFIG_FOLDER_PATH_USE = "config/";
	private static final String CONFIG_FOLDER_PATH_TEST = "src/test/resources/";

	public static final String PATH_VARIABLES_CONFIG_NAME = "pathvariables.config";
	public static final String CHARACTERBUILDER_CONFIG_NAME = "characterbuilder.config";
	
	private boolean useTestPaths = false;
	
	private ConfigReader pathVariablesConfigReader;
	private ConfigReader characterbuilderConfigReader;
	
	private ConfigReaderRepository() {
		
	}
	
	
	public static void useTestPaths() {
		INSTANCE.useTestPaths = true;
	}
	
	public static void init() {
		INSTANCE.characterbuilderConfigReader = new ConfigReader(INSTANCE.getCharacterbuilderConfig());
		INSTANCE.pathVariablesConfigReader = new ConfigReader(INSTANCE.getPathVariablesConfig());
		
		GameFileAccessor.init();
	}
	
	public static ConfigReader getPathVariablesConfigReader() {
		return INSTANCE.pathVariablesConfigReader;
	}
	
	public static ConfigReader getCharacterbuilderConfigReader() {
		return INSTANCE.characterbuilderConfigReader;
	}
	
	
	private File getConfigFolder() {
		return new File(useTestPaths ? CONFIG_FOLDER_PATH_TEST : CONFIG_FOLDER_PATH_USE);
	}
	
	private File getPathVariablesConfig()
	{
		return new File(getConfigFolder(), PATH_VARIABLES_CONFIG_NAME);
	}
	
	private File getCharacterbuilderConfig()
	{
		return new File(getConfigFolder(), CHARACTERBUILDER_CONFIG_NAME);
	}
	
}
