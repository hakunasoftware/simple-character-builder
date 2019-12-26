package simplecharacterbuilder.statgenerator.xmlreaderwriter;

import simplecharacterbuilder.InfoXmlReaderWriter;
import simplecharacterbuilder.statgenerator.StatDTO;

public class StatXmlReaderWriter extends InfoXmlReaderWriter<StatDTO>{
	StatXmlReaderWriter(String xmlURI){
		super(xmlURI);
	}

	@Override
	public StatDTO readDTOFromXml() {
		return StatDTO.builder()
				.constitution (readIntegerFromUniqueTagPath("Stats/Con"))
				.agility      (readIntegerFromUniqueTagPath("Stats/Agi"))
				.strength     (readIntegerFromUniqueTagPath("Stats/Str"))
				.intelligence (readIntegerFromUniqueTagPath("Stats/Int"))
				.sex          (readIntegerFromUniqueTagPath("Stats/Sex"))
				.beauty       (readIntegerFromUniqueTagPath("Stats/Beauty"))
				.charisma     (readIntegerFromUniqueTagPath("Stats/Charisma"))
				.obedience    (readIntegerFromUniqueTagPath("Stats/Obedience"))
				.build();
	}

	@Override
	public void updateXmlFromDTO(StatDTO dto) {
		writeIntToUniqueTagPath("Stats/Con",       dto.getConstitution());
		writeIntToUniqueTagPath("Stats/Agi",       dto.getAgility());
		writeIntToUniqueTagPath("Stats/Str",       dto.getStrength());
		writeIntToUniqueTagPath("Stats/Int",       dto.getIntelligence());
		writeIntToUniqueTagPath("Stats/Sex",       dto.getSex());
		writeIntToUniqueTagPath("Stats/Beauty",    dto.getBeauty());
		writeIntToUniqueTagPath("Stats/Charisma",  dto.getCharisma());
		writeIntToUniqueTagPath("Stats/Obedience", dto.getObedience());
		
		persistChangesToDocument();
	}
	
}
