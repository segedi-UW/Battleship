import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;

public class Chat {
	
	private final static int COLUMNS = 10;

	private final TextArea output;
	private final Connector connector;

	public Chat(Connector connector) {
		this.connector = connector;
		output = new TextArea();
		output.setEditable(false);
		output.setWrapText(true);
		output.setPrefColumnCount(COLUMNS);
		connector.addListener(text -> {
			boolean isChat = Encoder.parseCode(text) == Encoder.Code.CHAT;
			if (isChat)
				writeln(Encoder.decode(text));
		});
	}

	public void write(String text) {
		final double END = 1.0;
		output.appendText(text);
		output.setScrollTop(END);
	}

	public void writeln(String text) {
		write(text + "\n");
	}

	public void sendln(String text) {
		boolean isBlank = text.isBlank();
		if (!isBlank) {
			writeln(" >> " + text);
			connector.write(Encoder.encodeChat(text));
		}
	}
	
	public void clear() {
		output.setText("");
	}

	public VBox getDisplay() {
		final int LEFT = 10;
		Button button = new Button("Send");
		TextField inputField = new TextField();

		button.setOnAction(event -> {
			String input = inputField.getText();
			inputField.setText("");
			sendln(input);
		});
		HBox input = new HBox(inputField, button);
		VBox vbox = new VBox(output, input);
		VBox.setMargin(output, new Insets(0,0,0,LEFT));
		VBox.setMargin(input, new Insets(0,0,0,LEFT));
		return vbox;
	}
}
