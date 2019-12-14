package simplecharacterbuilder.statpicker;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import lombok.Builder;
import lombok.Data;
import simplecharacterbuilder.statpicker.RegularStatSelectionPanel.RegularStatSelectionDTO;
import simplecharacterbuilder.statpicker.StatCalculator.ConfigReader.BoundaryDTO;

class StatCalculator {

	private final BoundaryDTO boundaries;

	StatCalculator() {
		this.boundaries = new ConfigReader().readBoundaries();
	}

	StatPicker.StatDTO generateStats(RegularStatSelectionDTO regularStatsSelectionDTO, int beautySelection) {
		return StatPicker.StatDTO.builder()
				.constitution(generateStatFromSelection(regularStatsSelectionDTO.getConstitutionSelection()))
				.agility(generateStatFromSelection(regularStatsSelectionDTO.getAgilitySelection()))
				.strength(generateStatFromSelection(regularStatsSelectionDTO.getStrengthSelection()))
				.intelligence(generateStatFromSelection(regularStatsSelectionDTO.getIntelligenceSelection()))
				.charisma(generateStatFromSelection(regularStatsSelectionDTO.getCharismaSelection()))
				.obedience(generateStatFromSelection(regularStatsSelectionDTO.getObedienceSelection()))
				.sex(generateSexStatFromSelection(regularStatsSelectionDTO.getSexSelection()))
				.beauty(generateBeautyStatFromSelection(beautySelection))
				.build();
	}
	

	private int generateStatFromSelection(int selectionIndex) {
		// TODO
		return 20 * selectionIndex;
	}

	private int generateSexStatFromSelection(int sexSelection) {
		return sexSelection == 0 ? 0 : generateStatFromSelection(sexSelection);
	}

	private int generateBeautyStatFromSelection(int beautySelection) {
		// TODO
		return 500;
	}
	
	static class ConfigReader {

		private Properties prop = new Properties();

		public ConfigReader() {
			readProperties();
		}

		public BoundaryDTO readBoundaries() {
			return BoundaryDTO.builder().veryLowMinimum(readIntFromProp("veryLowMinimum"))
					.veryLowMaximum(readIntFromProp("veryLowMaximum"))
					.lowMaximum(readIntFromProp("lowMaximum"))
					.averageMaximum(readIntFromProp("averageMaximum"))
					.highMaximum(readIntFromProp("highMaximum"))
					.veryHighMaximum(readIntFromProp("veryHighMaximum"))
					.build();
		}

		private int readIntFromProp(String propName) {
			return Integer.parseInt(this.prop.getProperty(propName));
		}

		private void readProperties() {
			try (InputStream inputStream = new FileInputStream(StatPicker.CONFIG_PATH)) {
				this.prop.load(inputStream);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Data
		@Builder
		public static class BoundaryDTO {
			private int veryLowMinimum;
			private int veryLowMaximum;
			private int lowMaximum;
			private int averageMaximum;
			private int highMaximum;
			private int veryHighMaximum;
		}
	}
}
