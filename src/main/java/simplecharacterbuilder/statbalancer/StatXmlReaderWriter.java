package simplecharacterbuilder.statbalancer;

import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import simplecharacterbuilder.characterbuilder.util.holder.JAXBContextHolder;
import simplecharacterbuilder.common.generated.Actor;
import simplecharacterbuilder.common.resourceaccess.InfoXmlReaderWriter;
import simplecharacterbuilder.common.statgenerator.Stat;

public class StatXmlReaderWriter {
	
	private final InfoXmlReaderWriter xmlReader;
	
	private final String xmlURI;
	
	StatXmlReaderWriter(String xmlURI){
		this.xmlURI = xmlURI;
		
		xmlReader = new InfoXmlReaderWriter(xmlURI);
	}

	public Map<Stat, Integer> readStatsFromXml() {
		try {
			Actor actor = (Actor) JAXBContextHolder.createUnmarshaller().unmarshal(new File(xmlURI));
			Map<Stat, Integer> stats = new HashMap<>();
			stats.put(Stat.AGILITY, mapStatToInteger(actor.getStats().getAgi()));
			stats.put(Stat.BEAUTY, mapStatToInteger(actor.getStats().getBeauty()));
			stats.put(Stat.CHARISMA, mapStatToInteger(actor.getStats().getCharisma()));
			stats.put(Stat.CONSTITUTION, mapStatToInteger(actor.getStats().getCon()));
			stats.put(Stat.INTELLIGENCE, mapStatToInteger(actor.getStats().getInt()));
			stats.put(Stat.OBEDIENCE, mapStatToInteger(actor.getStats().getObedience()));
			stats.put(Stat.SEX, mapStatToInteger(actor.getStats().getSex()));
			stats.put(Stat.STRENGTH, mapStatToInteger(actor.getStats().getStr()));
			return stats;
		} catch (JAXBException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void updateXmlFromStats(Map<Stat, Integer> stats) {
		Stat.forAll(stat -> xmlReader.writeIntToUniqueTagPath("Stats/" + stat.getInfoXmlTag(), stats.get(stat)));
		xmlReader.persistChangesToDocument();
	}
	
	private Integer mapStatToInteger(BigInteger bigInt) {
		return bigInt == null ? 0 : bigInt.intValue();
	}
	
}
