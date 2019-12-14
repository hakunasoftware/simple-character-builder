package infostatsreader;

import infostatsreader.util.InfoXmlReaderWriter;

public class StatsReaderWriter extends InfoXmlReaderWriter<StatsDTO> {
	
	//TODO: Doesn't set <Stats> text if it doesn't exist yet

	public StatsReaderWriter(String xmlURI) {
		super(xmlURI);
	}

	public StatsDTO readDTOFromXml() {
		return StatsDTO.builder()
				.constitution(readIntegerByUniqueTagName("Con"))
				.agility(readIntegerByUniqueTagName("Agi"))
				.strength(readIntegerByUniqueTagName("Str"))
				.intelligence(readIntegerByUniqueTagName("Int"))
				.sex(readIntegerByUniqueTagName("Sex"))
				.beauty(readIntegerByUniqueTagName("Beauty"))
				.charisma(readIntegerByUniqueTagName("Charisma"))
				.obedience(readIntegerByUniqueTagName("Obedience"))
				.build();
	}

	public void updateXmlFromDTO(StatsDTO dto) {
		updateOrAddUniqueTagFromInt("Con", dto.getConstitution());
		updateOrAddUniqueTagFromInt("Agi", dto.getAgility());
		updateOrAddUniqueTagFromInt("Str", dto.getStrength());
		updateOrAddUniqueTagFromInt("Int", dto.getIntelligence());
		updateOrAddUniqueTagFromInt("Sex", dto.getSex());
		updateOrAddUniqueTagFromInt("Beauty", dto.getBeauty());
		updateOrAddUniqueTagFromInt("Charisma", dto.getCharisma());
		updateOrAddUniqueTagFromInt("Obedience", dto.getObedience());
		
		persistChangesToDocument();
	}
}