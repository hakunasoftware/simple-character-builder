package simplecharacterbuilder.characterbuilder.util.holder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class BodyImageFileHolder {
	private static final BodyImageFileHolder INSTANCE = new BodyImageFileHolder();

	public static final String PORTRAIT = "Portrait";
	public static final String BODY = "Body";
	public static final String HAIR = "Hair";
	
	private File portrait;
	private final Map<String, File> sprites = new HashMap<>();

	private BodyImageFileHolder(){}
	
	public static void put(String imageName, File originalFile) {
		if(PORTRAIT.equals(imageName)) {
			INSTANCE.portrait = originalFile;
		} else {
			INSTANCE.sprites.put(imageName, originalFile);
		}
	}
	
	public static File get(String imageName) {
		if(PORTRAIT.equals(imageName)) {
			return INSTANCE.portrait;
		} else {
			return INSTANCE.sprites.get(imageName);
		}
	}
	
	public static void remove(String imageName) {
		if(PORTRAIT.equals(imageName)) {
			INSTANCE.portrait = null;
		} else {
			INSTANCE.sprites.remove(imageName);
		}
	}
	
	public static Map<String, File> getSprites(){
		return new HashMap<String, File>(INSTANCE.sprites);
	}

	public static void copyImagesToTargetDirectory(File targetDir) {
		copyFile(INSTANCE.portrait, new File(targetDir, PORTRAIT + ".png"));
		INSTANCE.sprites.keySet().stream().forEach(k -> copyFile(INSTANCE.sprites.get(k), new File(targetDir, k + ".png")));
	}
	
	private static void copyFile(File origin, File target) {
		try {
			Files.copy(origin.toPath(), target.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
