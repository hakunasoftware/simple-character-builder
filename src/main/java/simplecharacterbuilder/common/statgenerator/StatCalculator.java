package simplecharacterbuilder.common.statgenerator;

import static simplecharacterbuilder.common.resourceaccess.PropertyRepository.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import simplecharacterbuilder.common.resourceaccess.ConfigReader;
import simplecharacterbuilder.common.resourceaccess.ConfigReaderRepository;

class StatCalculator {
	private static final String[] CONFIG_REG_STAT_BOUNDARIES = new String[] { REG_STAT_VERY_LOW, REG_STAT_LOW,
			REG_STAT_AVERAGE, REG_STAT_HIGH, REG_STAT_VERY_HIGH, REG_STAT_MAX };
	private static final String[] CONFIG_BEAUTY_BOUNDARIES = new String[] { BEA_STAT_UNATTRACTIVE, BEA_STAT_PLAIN,
			BEA_STAT_NORMAL, BEA_STAT_PRETTY, BEA_STAT_BEAUTIFUL, BEA_STAT_STUNNING, BEA_STAT_PERFECT, BEA_STAT_DIVINE,
			BEA_STAT_MAX };

	private final int[] regularStatBoundaries = new int[CONFIG_REG_STAT_BOUNDARIES.length];
	private final int[] beautyBoundaries = new int[CONFIG_BEAUTY_BOUNDARIES.length];

	private int multiplier;

	private ConfigReader configReader;

	private Random random = new Random();

	StatCalculator() {
		this.configReader = ConfigReaderRepository.getCharacterbuilderConfigReader();
		readConfig();
	}

	int generateSelection(Stat stat, int value) {
		return getIndexFromBoundaries(getBoundaries(stat), value);
	}

	private int getIndexFromBoundaries(int[] boundaries, int value) {
		for (int i = 1; i < boundaries.length; i++) {
			if (value < boundaries[i]) {
				return i - 1;
			}
		}
		return boundaries.length - 2;
	}

	int[] getBoundaries(Stat stat) {
		return stat.equals(Stat.BEAUTY) ? beautyBoundaries : regularStatBoundaries;
	}

	int getAverage(Stat stat) {
		return generateStatFromBoundariesAndSelection(getBoundaries(stat), 2);
	}

	int getMin(Stat stat) {
		int[] boundaries = getBoundaries(stat);
		return boundaries[0];
	}

	int getMax(Stat stat) {
		int[] boundaries = getBoundaries(stat);
		return boundaries[boundaries.length - 1] - 1;
	}

	int getMultiplier() {
		return multiplier;
	}

	int generateStatFromSelection(Stat stat, int selectionIndex) {
		if (stat.equals(Stat.SEX) && selectionIndex == -1) {
			return 0;
		}
		return generateStatFromBoundariesAndSelection(getBoundaries(stat), selectionIndex);

	}

	boolean isValidValue(Stat stat, int value) {
		return value % getMultiplier() == 0 && value >= getMin(stat) && value <= getMax(stat);
	}

	private int generateStatFromBoundariesAndSelection(int[] boundaries, int selectionIndex) {
		int lowerBoundary = boundaries[selectionIndex];
		int upperBoundary = boundaries[selectionIndex + 1] - 1;
		int lowerOffset = (lowerBoundary + (multiplier - 1)) / multiplier;
		int result = multiplier * (random.nextInt(upperBoundary / multiplier - lowerOffset + 1) + lowerOffset);
		return Math.min(result, 999);
	}

	private void readConfig() {
		multiplier = configReader.readInt(MULTIPLIER);
		initializeBoundaries(regularStatBoundaries, CONFIG_REG_STAT_BOUNDARIES);
		initializeBoundaries(beautyBoundaries, CONFIG_BEAUTY_BOUNDARIES);
	}

	private void initializeBoundaries(int[] boundaries, String[] configNames) {
		for (int i = 0; i < boundaries.length; i++) {
			boundaries[i] = configReader.readInt(configNames[i]);
		}
		boundaries[boundaries.length - 1]++;
	}

	Map<Stat, Integer> generateStatSuggestions(Map<Stat, Integer> stats) {
		Map<Stat, Integer> suggestions = new HashMap<>();
		Stat.forAll(stat -> suggestions.put(stat,
				isValidValue(stat, stats.get(stat)) ? stats.get(stat) : generateSuggestion(stat, stats.get(stat))));
		return suggestions;
	}

	private int generateSuggestion(Stat stat, int value) {
		if (stat.equals(Stat.SEX) && value == 1) {
			return generateStatFromSelection(Stat.SEX, 0);
		}
		if (value > getMax(stat)) {
			return generateMaxSuggestion(stat);
		}
		if (value < getMin(stat)) {
			return generateMinSuggestion(stat);
		}
		return roundToMultiplier(stat, value);
	}

	int generateMaxSuggestion(Stat stat) {
		int max = getMax(stat);
		int maxValid = max - max % multiplier;
		int closestValidMultipleOf10 = maxValid - maxValid % 10;
		return roundToMultiplier(stat, random.nextInt(maxValid - closestValidMultipleOf10) + closestValidMultipleOf10);
	}

	private int generateMinSuggestion(Stat stat) {
		int min = getMin(stat);
		int distance = min % multiplier;
		int minValid = distance == 0 ? min : min - distance + multiplier;
		int closestValidMultipleOf10 = minValid % 10 == 0 ? minValid + 10 : minValid - minValid % 10 + 10;
		return roundToMultiplier(stat, random.nextInt(closestValidMultipleOf10 - minValid) + minValid);
	}

	private int roundToMultiplier(Stat stat, int value) {
		int[] boundaries = getBoundaries(stat);
		int selectionIndex = getIndexFromBoundaries(boundaries, value);

		int lowerDistance = value % multiplier;
		int upperDistance = multiplier - lowerDistance;

		int lowerValue = value - lowerDistance;
		int upperValue = value + upperDistance;

		boolean lowerValueInBounds = isInBounds(boundaries, selectionIndex, lowerValue);
		boolean upperValueInBounds = isInBounds(boundaries, selectionIndex, upperValue);

		if (lowerValueInBounds && upperValueInBounds) {
			return upperDistance <= lowerDistance ? upperValue : lowerValue;
		}
		if (lowerValueInBounds) {
			return lowerValue;
		}
		if (upperValueInBounds) {
			return upperValue;
		}
		throw new IllegalArgumentException("Distance between boundaries too big for multiplier");
	}

	private boolean isInBounds(int[] boundaries, int selectionIndex, int value) {
		return value >= boundaries[selectionIndex] && value < boundaries[selectionIndex + 1];
	}

	public void scaleToMaximalValue(Map<Stat, Integer> stats) {
		int maxValue = Stat.getAll().stream().filter(stat -> !stat.equals(Stat.BEAUTY))
				.mapToInt(stat -> stats.get(stat)).max().getAsInt();
		int maxRegStatSuggestion = generateMaxSuggestion(Stat.AGILITY);
		Stat.forRegStats(stat -> stats.put(stat, stats.get(stat) * maxRegStatSuggestion / maxValue));
	}
}
