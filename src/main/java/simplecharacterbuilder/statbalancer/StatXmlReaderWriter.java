package simplecharacterbuilder.statbalancer;

import java.util.HashMap;
import java.util.Map;

import simplecharacterbuilder.statgenerator.Stat;
import simplecharacterbuilder.util.InfoXmlReaderWriter;

public class StatXmlReaderWriter {
	
	private final InfoXmlReaderWriter xmlReader;
	
	StatXmlReaderWriter(String xmlURI){
		xmlReader = new InfoXmlReaderWriter(xmlURI);
	}

	public Map<Stat, Integer> readStatsFromXml() {
		Map<Stat, Integer> stats = new HashMap<>();
		Stat.forAll(stat -> stats.put(stat, xmlReader.readIntegerFromUniqueTagPath("Stats/" + stat.getInfoXmlTag())));
		return stats;
	}

	public void updateXmlFromStats(Map<Stat, Integer> stats) {
		Stat.forAll(stat -> xmlReader.writeIntToUniqueTagPath("Stats/" + stat.getInfoXmlTag(), stats.get(stat)));
		xmlReader.persistChangesToDocument();
	}
	
}
