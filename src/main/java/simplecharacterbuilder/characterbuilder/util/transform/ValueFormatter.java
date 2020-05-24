package simplecharacterbuilder.characterbuilder.util.transform;

public class ValueFormatter {
	public static final String MALES = "Males";
	public static final String FEMALES = "Females";
	public static final String BOTH = "Both";
	public static final String NEITHER = "Neither";

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

	public static String formatBoolean(boolean isAsian) {
		return isAsian ? "True" : null;
	}

	public static String formatSkin(boolean darkSkin) {
		return darkSkin ? "Dark" : "Light";
	}

	public static boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
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
