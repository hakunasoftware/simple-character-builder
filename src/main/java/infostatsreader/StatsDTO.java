package infostatsreader;

import infostatsreader.util.DTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatsDTO implements DTO {
	private final int constitution;
	private final int agility;
	private final int strength;
	private final int intelligence;
	private final int sex;
	private final int beauty;
	private final int charisma;
	private final int obedience;
}
