package simplecharacterbuilder.util;

import java.io.File;

public class GameFileRepository {
	public static final GameFileRepository INSTANCE = new GameFileRepository();
	
	private String root;
	private File actorsFolder;

	private File bodyTypesList;
	private File racesList;

	private GameFileRepository() {
	}

	public static GameFileRepository getInstance() {
		return INSTANCE;
	}

	public static void init(String pathVariableConfigPath) {
		ConfigReader pathVariableReader = new ConfigReader(new File(pathVariableConfigPath));
		INSTANCE.root = pathVariableReader.readString("game_root");
		INSTANCE.actorsFolder = initFile("actors_folder", pathVariableReader);
		INSTANCE.bodyTypesList = initFile("body_types", pathVariableReader);
		INSTANCE.racesList = initFile("races", pathVariableReader);
	}

	private static File initFile(String propertyName, ConfigReader pathVariableReader){
		return new File(new File(INSTANCE.root), pathVariableReader.readString(propertyName));
	}
	
	public static File getActorsFolder() {
		return INSTANCE.actorsFolder;
	}
	
	public static File getBodyTypesList() {
		return INSTANCE.bodyTypesList;
	}
	
	public static File getRacesList() {
		return INSTANCE.racesList;
	}
}
