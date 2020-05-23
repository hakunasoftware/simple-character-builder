package simplecharacterbuilder.statbalancer;

import simplecharacterbuilder.common.resourceaccess.ConfigReaderRepository;

public class StatBalancerTestStart {
	
	public static void main(String[] args) {
		ConfigReaderRepository.useTestPaths();
		StatBalancerStart.main(args);
	}
	
}