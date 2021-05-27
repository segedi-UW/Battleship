import javafx.scene.layout.GridPane;
import javafx.geometry.HPos;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonBar.ButtonData;

/**
 * A custom Dialog that has input fields for ip address and port to allow user input
 * for joining a Host's game.
 * 
 * @author youngAgFox
 *
 */
public class JoinDialog extends Dialog<Address> {

	private TextField ipField;
	private TextField portField;
	private ButtonType joinButton;

	/**
	 * Creates the JoinDialog with formatting and a setupConverter.
	 */
	JoinDialog() {
		super();
		setupDialog();
		setupConverter();
	}

	/**
	 * Formats the dialog.
	 */
	private void setupDialog() {
		setTitle("Join Battleship Game");
		addContent();
		addExpandableContent();
		addButtons();
		setResizable(false);
	}

	/**
	 * Adds the content to the dialog.
	 */
	private void addContent() {
		final double V_GAP = 5.0;
		GridPane grid = new GridPane();
		grid.setVgap(V_GAP);

		addIPLabel(grid);
		addIPField(grid);
		addPortLabel(grid);
		addPortField(grid);

		getDialogPane().setContent(grid);
	}

	/**
	 * Adds the ip label to the grid.
	 * 
	 * @param grid the content grid.
	 */
	private void addIPLabel(GridPane grid) {
		Label ipLabel = new Label("Enter ip address: ");
		final int ipLabelCol = 0;
		final int ipLabelRow = 0;
		GridPane.setHalignment(ipLabel, HPos.RIGHT);
		grid.add(ipLabel, ipLabelCol, ipLabelRow);
	}

	/**
	 * Adds the port label to the grid.
	 * 
	 * @param grid the content grid.
	 */
	private void addPortLabel(GridPane grid) {
		Label portLabel = new Label("Enter port: ");
		final int portLabelCol = 0;
		final int portLabelRow = 1;
		GridPane.setHalignment(portLabel, HPos.RIGHT); 
		grid.add(portLabel, portLabelCol, portLabelRow);
	}

	/**
	 * Adds the ip field to the grid.
	 * 
	 * @param grid the content grid.
	 */
	private void addIPField(GridPane grid) {
		ipField = new TextField();
		final int ipFieldCol = 1;
		final int ipFieldRow = 0;
		grid.add(ipField, ipFieldCol, ipFieldRow);
	}

	/**
	 * Adds the port field to the grid.
	 * 
	 * @param grid the content grid.
	 */
	private void addPortField(GridPane grid) {
		portField = new TextField();
		final int portFieldCol = 1;
		final int portFieldRow = 1;
		grid.add(portField, portFieldCol, portFieldRow);
	}

	/**
	 * Adds the buttons to the dialog.
	 */
	private void addButtons() {
		joinButton = new ButtonType("Join", ButtonData.OK_DONE);
		ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().addAll(joinButton, cancelButton);
	}

	/**
	 * Adds the expandable content to the dialog.
	 */
	private void addExpandableContent() {
		final String details = "Ask the host of the game to provide you their info.\n" + 
			"Their ip address and port are displayed after initially hosting a game.\n" +
			"Note that games can only be joined if they are on the same network.";
		Label label = new Label(details);
		getDialogPane().setExpandableContent(label);
	}

	/**
	 * Creates the setup converter for the dialog.
	 */
	private void setupConverter() {
		setResultConverter(dialogButton -> {
			if (dialogButton == joinButton) {
				Address address = new Address();
				address.ip = ipField.getText();
				try {
					address.port = Integer.parseInt(portField.getText());
				} catch (NumberFormatException e) {
					return null;
				}
				return address;
			}
			return null;
		});
	}
}
