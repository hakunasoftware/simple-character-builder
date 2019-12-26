package simplecharacterbuilder.statgenerator;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import simplecharacterbuilder.DTO;

@Getter
@EqualsAndHashCode
@ToString
@Builder
public class StatDTO implements DTO {
	private int constitution;
	private int agility;
	private int strength;
	private int intelligence;
	private int charisma;
	private int obedience;
	private int sex;
	private int beauty;
}