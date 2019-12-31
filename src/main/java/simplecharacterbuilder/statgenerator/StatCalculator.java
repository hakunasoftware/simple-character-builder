package simplecharacterbuilder.statgenerator;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

class StatCalculator {
	private static final String[] CONFIG_REG_STAT_BOUNDARIES = new String[] {"regStat_veryLow", "regStat_low", "regStat_average", "regStat_high", "regStat_veryHigh", "regStat_max"};
	private static final String[] CONFIG_BEAUTY_BOUNDARIES	 = new String[] {"unattractive", "plain", "normal", "pretty", "beautiful", "stunning", "perfect", "divine", "beauty_max"};

	private final int[] regularStatBoundaries 	= new int[CONFIG_REG_STAT_BOUNDARIES.length];
	private final int[] beautyBoundaries 		= new int[CONFIG_BEAUTY_BOUNDARIES.length];

	private Properties prop   = new Properties();
	private Random     random = new Random();
	
	private int multiplier; 

	StatCalculator(String configPath) {
		readConfig(configPath);
	}

	//TODO fuse
	Map<Stat, Integer> generateStats(Map<Stat, Integer> regularStatsSelections, int beautySelection) {
		Map<Stat, Integer> stats = new HashMap<>();
		Stat.forRegStats(stat -> stats.put(stat, generateRegStatFromSelection(stat, regularStatsSelections.get(stat))));
		stats.put(Stat.BEAUTY, generateStatFromBoundariesAndSelection(beautyBoundaries, beautySelection));

	
		return stats;
	}
	
	Map<Stat, Integer> generateRegularStatSelections(Map<Stat, Integer> stats) {
		Map<Stat, Integer> selections = new HashMap<Stat, Integer>();
		Stat.getRegStats().stream().forEach(stat -> selections.put(stat, getIndexForStat(stat, stats.get(stat))));
		return selections;
	}
	
	private int getIndexForStat(Stat stat, int value) {
		switch(stat) {
			case SEX: if(value == 0 || value == 1) return -1;
			default: return getIndexFromBoundaries(regularStatBoundaries, value);
		}
	}
	
	int generateBeautySelection(int beauty) {
		return getIndexFromBoundaries(beautyBoundaries, beauty);
	}
	
	private int getIndexFromBoundaries(int[] boundaries, int stat) {
		for(int i = 1; i < boundaries.length; i++) {
			if(stat < boundaries[i]) {
				return i - 1;
			}
		}
		return boundaries.length - 2;
	}
	
	int getAverageRegStat() {
		return (regularStatBoundaries[3] + regularStatBoundaries[2]) / 2;
	}

	int getAverageBeauty() {
		return (beautyBoundaries[3] + beautyBoundaries[2]) / 2;
	}

	int getMinRegStat() {
		return regularStatBoundaries[0];
	}

	int getMaxRegStat() {
		return regularStatBoundaries[regularStatBoundaries.length - 1] - 1;
	}

	int getMinBeauty() {
		return beautyBoundaries[0];
	}

	int getMaxBeauty() {
		return beautyBoundaries[beautyBoundaries.length - 1] - 1;
	}
	
	int getMultiplier() {
		return multiplier;
	}
	
	private int generateRegStatFromSelection(Stat stat, int selectionIndex) {
		switch(stat) {
		case SEX: if(selectionIndex == -1) return 0;
		default: return generateStatFromBoundariesAndSelection(regularStatBoundaries, selectionIndex);
		}
		
	}

	private int generateStatFromBoundariesAndSelection(int[] boundaries, int selectionIndex) {
		int lowerBoundary = boundaries[selectionIndex];
		int upperBoundary = boundaries[selectionIndex + 1] - 1;
		int lowerOffset = (lowerBoundary + (multiplier - 1)) / multiplier;
		int result = multiplier * (random.nextInt(upperBoundary / multiplier - lowerOffset + 1) + lowerOffset);
		return Math.min(result, 999);
	}

	private void readConfig(String configPath) {
		loadProperty(configPath);
		multiplier = readIntFromProp("multiplier");
		initializeBoundaries(regularStatBoundaries, CONFIG_REG_STAT_BOUNDARIES);
		initializeBoundaries(beautyBoundaries, CONFIG_BEAUTY_BOUNDARIES);
	}
	
	private void initializeBoundaries(int[] boundaries, String[] configNames) {
		for(int i = 0; i < boundaries.length; i++) {
			boundaries[i] = readIntFromProp(configNames[i]);
		}
		boundaries[boundaries.length - 1]++;
	}

	private void loadProperty(String configPath) {
		try (InputStream inputStream = new FileInputStream(configPath)) {
			this.prop.load(inputStream);
		} catch (Exception e) {
			System.err.println("Error loading config");
			e.printStackTrace();
		}
	}

	private int readIntFromProp(String propName) {
		return Integer.parseInt(this.prop.getProperty(propName));
	}
}
