package simplecharacterbuilder.characterbuilder.core;

import simplecharacterbuilder.common.resourceaccess.ConfigReaderRepository;

public class CharacterBuilderStartTest {
	
	public static void main(String[] args) {
		ConfigReaderRepository.useTestPaths();
		CharacterBuilderStart.main(args);
	}
	
}