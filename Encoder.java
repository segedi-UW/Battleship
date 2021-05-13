public class Encoder {

	private enum Regex {
		ROW("#R"), COLUMN("#C");

		private String regex;

		private Regex(String regex) {
			this.regex = regex;
		}

		@Override
		public String toString() {
			return regex;
		}
	}

	public enum Code {
		ATTACK("$ATTACK#"), HIT("$HIT#"), MISS("$MISS#"), SUNK("$SUNK#"), CHAT("$:");

		public final String code;

		private Code(String code) {
			this.code = code;
		}
	}

	public static String encodeAttack(int column, int row) {
		final String COLUMN_REGEX = Regex.COLUMN.regex;
		final String ROW_REGEX = Regex.ROW.regex;
		return Code.ATTACK.code + COLUMN_REGEX + column + ROW_REGEX + row;
	}

	public static String encodeHit() {
		return Code.HIT.code;
	}

	public static String encodeMiss() {
		return Code.MISS.code;
	}

	public static String encodeSunk() {
		return Code.SUNK.code;
	}

	public static String encodeChat(String message) {
		return Code.CHAT.code + message;
	}

	public static Code parseCode(String message) {
		if (message.startsWith(Code.ATTACK.code)) {
			return Code.ATTACK;
		} else if (message.startsWith(Code.HIT.code)) {
			return Code.HIT;
		} else if (message.startsWith(Code.MISS.code)) {
			return Code.MISS;
		} else if (message.startsWith(Code.CHAT.code)) {
			return Code.CHAT;
		} else if (message.startsWith(Code.SUNK.code)) {
			return Code.SUNK;
		}
		return null;
	}

	public static String decode(String message) {
		Code code = parseCode(message);
		return message.substring(code.code.length() - 1);
	}
}
