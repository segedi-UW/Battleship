public class Encoder {

	private static String REGEX = ";";

	public enum Code {
		ATTACK("$ATTACK#"), HIT("$HIT#"), MISS("$MISS#"), SUNK("$SUNK#"), 
		CHAT("$:"), TURN_START("$TURN#"), READY("$READY#"), LOST("$LOST#"),
		WON("$WON#");

		public final String code;

		private Code(String code) {
			this.code = code;
		}
	}

	public static String encodeAttack(Point point) {
		return Code.ATTACK.code + point.column + REGEX + point.row;
	}
	
	public static Point decodeAttack(String message) {
		String decoded = decode(message);
		String[] split = decoded.split(REGEX);
		int column = Integer.parseInt(split[0]);
		int row = Integer.parseInt(split[1]);
		return new Point(column, row);
	}

	public static String encodeState(Code code) {
		if (code == Code.ATTACK || code == Code.CHAT)
			throw new IllegalArgumentException("The Chat or Attack code is not a state: " + code);
		return code.code;
	}

	public static String encodeChat(String message) {
		return Code.CHAT.code + message;
	}

	public static Code parseCode(String message) {
		Code[] codes = Code.values();
		for (int i = 0; i < codes.length; i++) {
			Code code = codes[i];
			if (message.startsWith(code.code))
				return code;
		}
		
		throw new NullPointerException("Could not parse code");
	}

	public static String decode(String message) {
		Code code = parseCode(message);
		return message.substring(code.code.length());
	}
}
