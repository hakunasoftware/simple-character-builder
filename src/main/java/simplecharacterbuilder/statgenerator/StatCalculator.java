package simplecharacterbuilder.statgenerator;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

class StatCalculator {
	private static final String[] CONFIG_REG_STAT_BOUNDARIES = new String[] {"regStat_veryLow", "regStat_low", "regStat_average", "regStat_high", "regStat_veryHigh", "regStat_max"};
	private static final String[] CONFIG_BEAUTY_BOUNDARIES   = new String[] {"unattractive", "plain", "normal", "pretty", "beautiful", "stunning", "perfect", "divine", "beauty_max"};

	private final int[] regularStatBoundaries   = new int[CONFIG_REG_STAT_BOUNDARIES.length];
	private final int[] beautyBoundaries        = new int[CONFIG_BEAUTY_BOUNDARIES.length];

	private Properties prop   = new Properties();
	private Random     random = new Random();
	
	private int multiplier; 

	StatCalculator(String configPath) {
		readConfig(configPath);
	}

	int generateSelection(Stat stat, int value) {
		switch(stat) {
			case BEAUTY: return getIndexFromBoundaries(beautyBoundaries, value);
			case SEX: if(value == 0 || value == 1) return -1;
			default: return getIndexFromBoundaries(regularStatBoundaries, value);
		}
	}
	
	private int getIndexFromBoundaries(int[] boundaries, int value) {
		for(int i = 1; i < boundaries.length; i++) {
			if(value < boundaries[i]) {
				return i - 1;
			}
		}
		return boundaries.length - 2;
	}
	
	int[] getBoundaries(Stat stat) {
		return stat.equals(Stat.BEAUTY) ? beautyBoundaries : regularStatBoundaries;
	}
	
	int getAverage(Stat stat) {
		int[] boundaries =  getBoundaries(stat);
		return (boundaries[3] + boundaries[2]) / 2;
	}
	
	int getMin(Stat stat) {
		if(stat.equals(Stat.SEX)) {
			return 0;
		}
		int[] boundaries =  getBoundaries(stat);
		return boundaries[0];
	}
	
	int getMax(Stat stat) {
		int[] boundaries =  getBoundaries(stat);
		return boundaries[boundaries.length - 1] - 1;
	}
	
	int getMultiplier() {
		return multiplier;
	}
	
	int generateStatFromSelection(Stat stat, int selectionIndex) {
		switch(stat) {
			case BEAUTY: return generateStatFromBoundariesAndSelection(beautyBoundaries, selectionIndex);
			case SEX: if(selectionIndex == -1) return 0;
			default: return generateStatFromBoundariesAndSelection(regularStatBoundaries, selectionIndex);
		}
	}
	
	boolean isValidValue(Stat stat, int value) {
		return value % getMultiplier() == 0 
				&& value >= getMin(stat)
				&& value <= getMax(stat);
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
