package simplecharacterbuilder.statpicker;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import simplecharacterbuilder.statpicker.RegularStatSelectionPanel.RegularStatSelectionDTO;
import simplecharacterbuilder.statpicker.StatPicker.StatDTO;

class StatCalculator {
	private static final String[] CONFIG_REG_STAT_BOUNDARIES = new String[] {"regStat_veryLow", "regStat_low", "regStat_average", "regStat_high", "regStat_veryHigh", "regStat_max"};
	private static final String[] CONFIG_BEAUTY_BOUNDARIES	 = new String[] {"unattractive", "plain", "normal", "pretty", "beautiful", "stunning", "perfect", "divine", "beauty_max"};

	private final int[] regularStatBoundaries 	= new int[CONFIG_REG_STAT_BOUNDARIES.length];
	private final int[] beautyBoundaries 		= new int[CONFIG_BEAUTY_BOUNDARIES.length];

	private Properties prop = new Properties();
	private Random random 	= new Random();
	private int multiplier; 
	

	StatCalculator(String configPath) {
		readConfig(configPath);
	}

	StatDTO generateStats(RegularStatSelectionDTO regularStatsSelectionDTO, int beautySelection) {
		return StatPicker.StatDTO.builder()
				.constitution(generateRegStatFromSelection(regularStatsSelectionDTO.getConstitutionSelection()))
				.agility(generateRegStatFromSelection(regularStatsSelectionDTO.getAgilitySelection()))
				.strength(generateRegStatFromSelection(regularStatsSelectionDTO.getStrengthSelection()))
				.intelligence(generateRegStatFromSelection(regularStatsSelectionDTO.getIntelligenceSelection()))
				.charisma(generateRegStatFromSelection(regularStatsSelectionDTO.getCharismaSelection()))
				.obedience(generateRegStatFromSelection(regularStatsSelectionDTO.getObedienceSelection()))
				.sex(generateSexStatFromSelection(regularStatsSelectionDTO.getSexSelection()))
				.beauty(generateStatFromBoundariesAndSelection(beautyBoundaries, beautySelection))
				.build();
	}

	private int generateRegStatFromSelection(int selectionIndex) {
		return generateStatFromBoundariesAndSelection(regularStatBoundaries, selectionIndex);
	}

	private int generateSexStatFromSelection(int sexSelection) {
		return sexSelection == -1 ? 0 : generateRegStatFromSelection(sexSelection);
	}

	private int generateStatFromBoundariesAndSelection(int[] boundaries, int selectionIndex) {
		int lowerBoundary = boundaries[selectionIndex];
		int upperBoundary = boundaries[selectionIndex + 1] - 1;
		int lowerOffset = (lowerBoundary + (multiplier - 1)) / multiplier;
		return multiplier * (random.nextInt(upperBoundary / multiplier - lowerOffset + 1) + lowerOffset);
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
			e.printStackTrace();
		}
	}

	private int readIntFromProp(String propName) {
		return Integer.parseInt(this.prop.getProperty(propName));
	}
}
