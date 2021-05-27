import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;

/**
 * Provides input output chat using a provided connector. Also provides
 * a display for gui interaction.
 * 
 * @author youngAgFox
 *
 */
public class Chat {
	
	private final static int COLUMNS = 10;

	private final TextArea output;
	private final Connector connector;

	/**
	 * Creates a Chat object using a given connector to communicate the messages
	 * sent and received.
	 * 
	 * @param connector the Connector object associated with the current
	 * game connection.
	 */
	public Chat(Connector connector) {
		this.connector = connector;
		output = new TextArea();
		
		setupOutput();
		setupListener();
	}
	
	/**
	 * Formats the output TextArea.
	 */
	private void setupOutput() {
		output.setEditable(false);
		output.setWrapText(true);
		output.setPrefColumnCount(COLUMNS);
	}
	
	/**
	 * Adds the message listener.
	 */
	private void setupListener() {
		connector.addListener(text -> {
			boolean isChat = Encoder.parseCode(text) == Encoder.Code.CHAT;
			if (isChat)
				writeln(Encoder.decode(text));
		});
	}

	/**
	 * Writes a string to output.
	 * 
	 * @param text the text to write
	 */
	public void write(String text) {
		final double END = 1.0;
		output.appendText(text);
		output.setScrollTop(END);
	}

	/**
	 * Writes a string to output followed by a newline.
	 * 
	 * @param text the text to write
	 */
	public void writeln(String text) {
		write(text + "\n");
	}

	/**
	 * Passes a message to the Connector to be received by the other
	 * end of the connection.
	 * 
	 * @param text the text to send
	 */
	public void sendln(String text) {
		boolean isBlank = text.isBlank();
		if (!isBlank) {
			writeln(" >> " + text);
			connector.write(Encoder.encodeChat(text));
		}
	}
	
	/**
	 * Clears the output.
	 */
	public void clear() {
		output.setText("");
	}

	/**
	 * Returns a display of the output with an input field and send button.
	 * 
	 * @return VBox representing the display of this Chat object.
	 */
	public VBox getDisplay() {
		final int LEFT = 10;
		final String BUTTON_TEXT = "Send";
		
		Button button = new Button(BUTTON_TEXT);
		TextField inputField = new TextField();
		
		setupButton(button, inputField);
		
		HBox input = new HBox(inputField, button);
		VBox vbox = new VBox(output, input);
		VBox.setMargin(output, new Insets(0,0,0,LEFT));
		VBox.setMargin(input, new Insets(0,0,0,LEFT));
		return vbox;
	}
	
	/**
	 * Sets up the given button to read from the passed TextField, then send its
	 * contents and clear it when clicked.
	 * 
	 * @param button the send button
	 * @param inputField the field to read
	 */
	private void setupButton(Button button, TextField inputField) {
		button.setOnAction(event -> {
			String input = inputField.getText();
			inputField.setText("");
			sendln(input);
		});
	}
}
