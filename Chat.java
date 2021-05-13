import java.lang.StringBuilder;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;

public class Chat {

	private final static int COLUMNS = 10;

	private final TextArea output;
	private final StringBuilder buffer;

	public Chat() {
		output = new TextArea();
		output.setEditable(false);
		output.setWrapText(true);
		output.setPrefColumnCount(COLUMNS);
		buffer = new StringBuilder();
	}

	private void updateOutput() {
		output.setText(buffer.toString());
	}

	public void write(String text) {
		buffer.append(text);
		updateOutput();
	}

	public void writeln(String text) {
		buffer.append(text + "\n");
		updateOutput();
	}

	public void clearBuffer() {
		buffer.setLength(0);
	}

	public VBox getDisplay() {
		final int LEFT = 10;
		Button button = new Button("Send");
		TextField inputField = new TextField();
		HBox input = new HBox(inputField, button);
		VBox vbox = new VBox(output, input);
		vbox.setMargin(output, new Insets(0,0,0,LEFT));
		vbox.setMargin(input, new Insets(0,0,0,LEFT));
		return vbox;
	}
}
