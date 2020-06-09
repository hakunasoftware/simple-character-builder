package simplecharacterbuilder.characterbuilder.util.transform;

import java.util.Random;

public class ValueFormatter {
	public static final String NONE_OPTION = "<None>";
	public static final String MALES = "Males";
	public static final String FEMALES = "Females";
	public static final String BOTH = "Both";
	public static final String NEITHER = "Neither";

	private static final String HEXSTRING = "0123456789ABCDEF";

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
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < 8; i++) {
			builder.append(HEXSTRING.charAt(new Random().nextInt(16)));
		}
		return builder.append(" : ").append(entry).toString();
	}

	private static void appendName(StringBuilder builder, String name) {
		if (!isEmpty(name)) {
			if (builder.length() > 0) {
				builder.append(" ");
			}
			builder.append(name);
		}
	}
}
