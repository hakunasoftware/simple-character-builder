package infostatsreader;

public class TestMain {

	public static void main(String[] args) {
		StatsReaderWriter statsReader = new StatsReaderWriter("src/Info.xml");
		StatsDTO testDTO = statsReader.readDTOFromXml();
		System.out.println(testDTO);

//		statsReader.updateXmlFromDTO(StatsDTO.builder()
//				.constitution(5)
//				.agility(10)
//				.strength(1)
//				.intelligence(2)
//				.sex(4)
//				.beauty(5)
//				.charisma(20)
//				.obedience(12)
//				.build());

		System.out.println(statsReader.readDTOFromXml());
	}

}
