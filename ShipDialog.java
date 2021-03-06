import java.util.ArrayList;
import java.util.Random;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;

/**
 * A Dialog that allows coordinate input for setup of ships.
 * 
 * @author youngAgFox
 *
 */
public class ShipDialog extends Dialog<Boolean> {
	private TextField startPoint;
	private TextField endPoint;
	private ButtonType add;
	private ButtonType randomize;
	private Ship ship;

	/**
	 * Creates a new ShipDialog with appropriate content based off the ship being setup.
	 * @param ship
	 */
	ShipDialog(Ship ship) {
		super();
		this.ship = ship;
		setupDialog();
		setupConverter();
	}

	/**
	 * Formats the Dialog.
	 */
	private void setupDialog() {
		setTitle("Add Ship");
		initModality(Modality.NONE);
		initOwner(Gui.getWindow());
		addHeader();
		addContent();
		addExpandableContent();
		addButtons();
		Gui.centerOnStage(this);
		setResizable(false);
	}
	
	/**
	 * Adds the header.
	 */
	private void addHeader() {
		setHeaderText("Input coordinates for your " + ship.getName() + " (" + ship.getPoints().length + ")");
	}

	/**
	 * Adds the content.
	 */
	private void addContent() {
		final double V_GAP = 5.0;
		GridPane grid = new GridPane();
		grid.setVgap(V_GAP);

		addStartField(grid);
		addEndField(grid);

		getDialogPane().setContent(grid);
	}

	/**
	 * Adds the startField to the grid.
	 * 
	 * @param grid the display grid.
	 */
	private void addStartField(GridPane grid) {
		startPoint = new TextField();
		startPoint.setPromptText("Enter the start point: (a-j)(0-9)");
		
		final int ipFieldCol = 0;
		final int ipFieldRow = 0;
		grid.add(startPoint, ipFieldCol, ipFieldRow);
	}

	/**
	 * Adds the endField to the grid.
	 * 
	 * @param grid the display grid.
	 */
	private void addEndField(GridPane grid) {
		endPoint = new TextField();
		endPoint.setPromptText("Enter the end point: (a-j)(0-9)");
		final int portFieldCol = 0;
		final int portFieldRow = 1;
		grid.add(endPoint, portFieldCol, portFieldRow);
	}

	/**
	 * Adds the buttons.
	 */
	private void addButtons() {
		add = new ButtonType("Add", ButtonData.OK_DONE);
		randomize = new ButtonType("Randomize");
		ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		

		getDialogPane().getButtonTypes().addAll(add, cancel, randomize);
	}

	/**
	 * Adds the expandable content.
	 */
	private void addExpandableContent() {
		final String details = ""; // TODO
		Label label = new Label(details);
		getDialogPane().setExpandableContent(label);
	}

	/**
	 * Sets up the converter.
	 */
	private void setupConverter() {
		setResultConverter(dialogButton -> {
			if (dialogButton == add) {
				try {
					String startText = startPoint.getText();
					String endText = endPoint.getText();
					
					Point start = parsePoint(startText);
					Point end = parsePoint(endText);
					addShip(start, end);
				} catch (IllegalArgumentException e) {
					Gui.showMessageAlert("Ship Setup Error", "There was an error adding the ship. " + e.getMessage());
					return false;
				} 
				return true;
			} else if (dialogButton == randomize) {
				boolean successful = false;
				while (!successful)
					successful = attemptToAddShip();
				return true;
			}
			return null;
		});
	}
	
	private boolean attemptToAddShip() {
		try {
			Random random = new Random();
			final int max = 10;
			Point start = new Point(random.nextInt(max), random.nextInt(max));
			int spaces = ship.getLength() - 1;

			boolean isOpenLeft = start.column - spaces >= 0;
			boolean isOpenRight = start.column + spaces < max;
			boolean isOpenUp = start.row - spaces >= 0;
			boolean isOpenDown = start.row - spaces < max;

			final int left = 0;
			final int right = 1;
			final int up = 2;
			final int down = 3;
			ArrayList<Integer> indexes = new ArrayList<>();
			indexes.add(left);
			indexes.add(right);
			indexes.add(up);
			indexes.add(down);

			boolean[] options = { isOpenLeft, isOpenRight, isOpenUp, isOpenDown };

			while (!indexes.isEmpty()) {
				Integer index = indexes.get(random.nextInt(indexes.size()));

				Point end = new Point(start.column, start.row); // same point will fail
				if (options[index])
					switch (index) {
					case left:
						end = new Point(start.column - spaces, start.row);
						break;
					case right:
						end = new Point(start.column + spaces, start.row);
						break;
					case up:
						end = new Point(start.column, start.row - spaces);
						break;
					case down:
						end = new Point(start.column, start.row + spaces);
						break;
					default:
						throw new IllegalArgumentException("Error determining randomized boolean from index");
					}
				try {
					addShip(start, end);
					return true;
				} catch (IllegalArgumentException e) {
					indexes.remove(index);
				}
			}

			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
	
	private void addShip(Point start, Point end) {
		ship.setup(start, end);
		
		Game game = Gui.getGame();
		game.getBoard().addShip(ship);
	}
	
	/**
	 * Parses the Point from user input.
	 * 
	 * @param string the user input
	 * @return the Point
	 */
	private Point parsePoint(String string) {
		if (string.length() != 2)
			throw new IllegalArgumentException("Too many characters in coordinate");
		char letter = string.toLowerCase().charAt(0);
		final char a = 'a';
		int letterInt = letter - a;
		if (letterInt < 0 || letterInt > 9)
			throw new IllegalArgumentException("Letter needs to be between a-j");
		int digit = Integer.parseInt(string.substring(1));
		return new Point(letterInt, digit);
	}
}
