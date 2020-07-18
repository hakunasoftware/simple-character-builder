package simplecharacterbuilder.characterbuilder.util.holder;

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
	public static final String EXTRA_LAYER_SUFFIX = "_Extra";

	private File portrait;
	private final Map<String, File> bodySprites = new HashMap<>();
	private final Map<String, File> equipSprites = new HashMap<>();
	
	private ImageFileHolder() {
	}

	public static void putPortrait(File portrait) {
		INSTANCE.portrait = portrait;
	}

	public static void putBodySprite(String name, File originalFile) {
		INSTANCE.bodySprites.put(name, originalFile);
	}

	public static void putEquipSprite(String name, File originalFile) {
		INSTANCE.equipSprites.put(name, originalFile);
	}

	public static File getPortrait() {
		return INSTANCE.portrait;
	}

	public static File getBodySprite(String name) {
		return INSTANCE.bodySprites.get(name);
	}

	public static File getEquipSprite(String name) {
		return INSTANCE.equipSprites.get(name);
	}

	public static void removeBodySprite(String name) {
		INSTANCE.bodySprites.remove(name);
	}

	public static void removeEquipSprite(String name) {
		INSTANCE.equipSprites.remove(name);
	}

	public static Map<String, File> getBodySprites() {
		return new HashMap<>(INSTANCE.bodySprites);
	}

	public static Map<String, File> getEquipSprites() {
		return new HashMap<>(INSTANCE.equipSprites);
	}

	public static void copyBodyImagesToTargetDirectory(File targetDir) {
		copyFile(INSTANCE.portrait, new File(targetDir, PORTRAIT + ".png"));
		copySpriteMapToTargetDirectory(INSTANCE.bodySprites, targetDir);
	}
	
	public static void copyEquipSpritesToTargetDirectory(File targetDir) {
		copySpriteMapToTargetDirectory(INSTANCE.equipSprites, targetDir);
	}
	
	private static void copySpriteMapToTargetDirectory(Map<String, File> spriteMap, File targetDir) {
		spriteMap.keySet().stream().forEach(k -> copyFile(INSTANCE.equipSprites.get(k), new File(targetDir, k + ".png")));
	}

	private static void copyFile(File origin, File target) {
		try {
			Files.copy(origin.toPath(), target.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
