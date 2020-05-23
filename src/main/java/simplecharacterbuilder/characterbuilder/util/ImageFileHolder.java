package simplecharacterbuilder.characterbuilder.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ImageFileHolder {
	private static final ImageFileHolder INSTANCE = new ImageFileHolder();

	public static final String PORTRAIT = "Portrait";
	public static final String BODY = "Body";
	public static final String HAIR = "Hair";
	
	private final Map<String, File> IMAGES = new HashMap<>();
	
	private ImageFileHolder(){}
	
	public static void put(String imageName, File originalFile) {
		INSTANCE.IMAGES.put(imageName, originalFile);
	}
	
	public static File get(String imageName) {
		return INSTANCE.IMAGES.get(imageName);
	}
	
	public static void remove(String imageName) {
		INSTANCE.IMAGES.remove(imageName);
	}

	public static void copyImagesToTargetDirectory(File targetDir) {
		INSTANCE.IMAGES.keySet().stream().forEach(k -> copyFile(INSTANCE.IMAGES.get(k), new File(targetDir, k + ".png")));
	}
	
	private static void copyFile(File origin, File target) {
		try {
			Files.copy(origin.toPath(), target.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
