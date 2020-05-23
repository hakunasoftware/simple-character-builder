package simplecharacterbuilder.common.statgenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public enum Stat {
	CONSTITUTION("Constitution", "CON", "Con"),
	AGILITY("Agility", "AGI", "Agi"),
	STRENGTH("Strength", "STR", "Str"),
	INTELLIGENCE("Intelligence", "INT", "Int"),
	CHARISMA("Charisma", "CHA", "Charisma"),
	OBEDIENCE("Obedience", "OBE", "Obedience"),
	SEX("Sex", "SEX", "Sex"),
	BEAUTY("Beauty", "BEA", "Beauty");

	private static final List<Stat> allStats;
	private static final List<Stat> regStats;
	static {
		allStats = Arrays.asList(Stat.values());
		regStats = allStats.stream().filter(stat -> !stat.equals(Stat.BEAUTY)).collect(Collectors.toList());
	}
	
	private final String name;
	private final String abbreviation;
	private final String infoXmlTag;
	
	private Stat(String name, String abbreviation, String infoXmlTag){
		this.name         = name;
		this.abbreviation = abbreviation;
		this.infoXmlTag   = infoXmlTag;
	}
	
	public static List<Stat> getAll() {
		return allStats;
	}
	
	public static void forAll(Consumer<? super Stat> action) {
		Stat.getAll().stream().forEach(action);
	}
	
	public static List<Stat> getRegStats() {
		return regStats;
	}
	
	public static void forRegStats(Consumer<? super Stat> action) {
		Stat.getRegStats().stream().forEach(action);
	}
	
	public static List<Stat> getAllstats() {
		return allStats;
	}

	public static List<Stat> getRegstats() {
		return regStats;
	}

	public String getName() {
		return name;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public String getInfoXmlTag() {
		return infoXmlTag;
	}
}
