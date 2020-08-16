package simplecharacterbuilder.characterbuilder.util.transform;

import java.util.List;
import java.util.Random;

public class ValueFormatter {
	public static final String NONE_OPTION = "<None>";
	public static final String MALES = "Males";
	public static final String FEMALES = "Females";
	public static final String BOTH = "Both";
	public static final String NEITHER = "Neither";
	
	private static final int MAX_ID = 1073741823;


	public static String nullEmptyString(String str) {
		return isEmpty(str) ? null : str;
	}

	public static String formatLikes(String likesSelection) {
		switch (likesSelection) {
		case MALES:
			return MALES;
		case FEMALES:
			return FEMALES;
		case BOTH:
			return MALES + " | " + FEMALES;
		case NEITHER:
			return "None";

		}
		throw new IllegalArgumentException("Invalid selection for Likes");
	}

	public static String formatFullName(String first, String middle, String last, boolean isAsian) {
		StringBuilder output = new StringBuilder();
		if (isAsian) {
			appendName(output, last);
			appendName(output, first);
			appendName(output, middle);
		} else {
			appendName(output, first);
			appendName(output, middle);
			appendName(output, last);
		}
		return output.toString();
	}

	public static String formatSourceType(String primaryType, String secondaryType) {
		return secondaryType != null ? primaryType + " | " + secondaryType : primaryType;
	}

	public static String formatNickname(String nickname, String probability) {
		if (ValueFormatter.isEmpty(nickname)) {
			return null;
		}
		return "100".equals(probability) ? nickname : nickname + " [" + probability + "%]";
	}
	
	public static String formatRelationship(String relationshipType, String fullName) {
		return relationshipType + " ~ " + fullName;
	}

	public static String formatBoolean(boolean value) {
		return value ? "True" : null;
	}

	public static String formatSkin(boolean darkSkin) {
		return darkSkin ? "Dark" : "Light";
	}

	public static boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}
	
	public static String checkStringForNoneOption(String input) {
		return NONE_OPTION.equals(input) ? null : input;
	}
	
	public static String formatListEntry(String entry) {
		return generateRandomId() + " : " + entry;
	}
	
	public static String formatListWithCommas(List<String> list) {
		if(list == null) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < list.size(); i++) {
			builder.append(list.get(i));
			if(i < list.size() - 1) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}

	private static void appendName(StringBuilder builder, String name) {
		if (!isEmpty(name)) {
			if (builder.length() > 0) {
				builder.append(" ");
			}
			builder.append(name);
		}
	}
	
	private static String generateRandomId() {
		String hexId = Integer.toHexString(new Random().nextInt(MAX_ID)).toUpperCase();
		while(hexId.length() < 8) {
			hexId = "0" + hexId;
		}
		return hexId;
	}
	
}
