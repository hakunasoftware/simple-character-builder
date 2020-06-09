package simplecharacterbuilder.characterbuilder.util.holder;

import java.util.ArrayList;
import java.util.List;

public class PostInfoXmlGenerationRunnableHolder {
	private static final PostInfoXmlGenerationRunnableHolder INSTANCE = new PostInfoXmlGenerationRunnableHolder();
	
	private final List<Runnable> runnables = new ArrayList<>();
	
	private PostInfoXmlGenerationRunnableHolder() {}
	
	/**
	 * Add a runnable that will be executed after generating the character's Info.xml.
	 */
	public static void add(Runnable runnable) {
		INSTANCE.runnables.add(runnable);
	}
	
	public static void runAll() {
		INSTANCE.runnables.stream().forEach(r -> r.run());
	}
	
	public static void clear() {
		INSTANCE.runnables.clear();
	}
	
}
