/**
 * An object that handles game states, attacks, and chats communication with Strings.
 * Can be used as both an Encoder and Decoder. 
 * 
 * @author youngAgFox
 *
 */
public class Encoder {

	private static String REGEX = ";";

	/**
	 * Codes used to signify different game states, attacks, and chats.
	 * 
	 * @author youngAgFox
	 *
	 */
	public enum Code {
		ATTACK("$ATTACK#"), HIT("$HIT#"), MISS("$MISS#"), SUNK("$SUNK#"), 
		CHAT("$:"), TURN_START("$TURN#"), READY("$READY#"), LOST("$LOST#"),
		WON("$WON#");

		public final String code;

		private Code(String code) {
			this.code = code;
		}
	}

	/**
	 * Returns an encoded attack.
	 * 
	 * @param point the point to attack
	 * @return an Encoded String detailing an attack at the passed point.
	 */
	public static String encodeAttack(Point point) {
		return Code.ATTACK.code + point.column + REGEX + point.row;
	}
	
	/**
	 * Returns the Point that was attacked.
	 * 
	 * @param message the message containing the encoded attack.
	 * @return the Point attacked.
	 */
	public static Point decodeAttack(String message) {
		String decoded = decode(message);
		String[] split = decoded.split(REGEX);
		int column = Integer.parseInt(split[0]);
		int row = Integer.parseInt(split[1]);
		return new Point(column, row);
	}

	/**
	 * Returns an encoded game state.
	 * 
	 * @param code the state code to encode.
	 * @return The String encoded state.
	 * @throws IllegalArgumentException if the code provided is a Code.ATTACK or Code.CHAT
	 */
	public static String encodeState(Code code) {
		if (code == Code.ATTACK || code == Code.CHAT)
			throw new IllegalArgumentException("The Chat or Attack code is not a state: " + code);
		return code.code;
	}

	/**
	 * Returns an encoded chat.
	 * 
	 * @param message the chat message to encode.
	 * @return the encoded chat message.
	 */
	public static String encodeChat(String message) {
		return Code.CHAT.code + message;
	}

	/**
	 * Parses a message for a Code.
	 * 
	 * @param message the message to parse.
	 * @return the Code parsed from the message.
	 */
	public static Code parseCode(String message) {
		Code[] codes = Code.values();
		for (int i = 0; i < codes.length; i++) {
			Code code = codes[i];
			if (message.startsWith(code.code))
				return code;
		}
		
		throw new NullPointerException("Could not parse code");
	}

	/**
	 * Returns the message without the encoding parts.
	 * 
	 * @param message the encoded message.
	 * @return the plain message not encoded.
	 */
	public static String decode(String message) {
		Code code = parseCode(message);
		return message.substring(code.code.length());
	}
}
