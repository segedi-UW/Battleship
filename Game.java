import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 * An object that represents a Battleship Game.
 * 
 * @author youngAgFox
 *
 */
public class Game {

	public enum Gamemode {
		STANDARD, SALVO, ADVANCED;
	}

	private Gamemode gamemode;
	private boolean isTurn;
	private Connector connection;
	private TextArea console;
	private HomeBoard board;
	private TargetBoard target;
	private int ships;

	private boolean isOpponentReady;

	/**
	 * Creates a new Game with the Standard gamemode.
	 */
	public Game() {
		this(Gamemode.STANDARD);
	}
	
	/**
	 * Creates a new Game with the given gamemode.
	 * 
	 * @param gamemode the gamemode
	 */
	public Game(Gamemode gamemode) {
		this.gamemode = gamemode;
		isTurn = false;
		console = createConsole();
		board = new HomeBoard();
		target = new TargetBoard();
		ships = 5;
	}

	/**
	 * Creates the TextArea console for notification to the user of game events and
	 * actions.
	 * 
	 * @return TextArea display of the console
	 */
	private TextArea createConsole() {
		final int height = 100;
		final int width = 200;
		final boolean editable = false;
		final boolean wrapText = true;

		TextArea area = new TextArea();
		area.setPrefHeight(height);
		area.setPrefWidth(width);
		area.setEditable(editable);
		area.setWrapText(wrapText);

		return area;
	}

	/**
	 * Fires at the given point.
	 * 
	 * @param point to fire upon.
	 */
	public void fire(Point point) {
		if (!isOpponentReady)
			log("Waiting for Opponent...");
		if (!isTurn)
			log("We are still reloading Commander");
		if (isTurn && isOpponentReady) {
			log("Missles Away!");
			target.waitForFire();
			connection.write(Encoder.encodeAttack(point));
			isTurn = false;
		}
	}

	/**
	 * Returns if it is this users turn.
	 * 
	 * @return true if it is this users turn, false otherwise.
	 */
	public boolean isTurn() {
		return isTurn;
	}

	/**
	 * Processes an enemy attack at a particular point.
	 * 
	 * @param point the point attacked.
	 */
	private void processEnemyAttack(Point point) {
		String text = "The enemy has fired upon us!";
		log(text);
		
		board.attack(point); // Sets the lastCode
		
		Encoder.Code code = board.getLastCode();
		processEnemyAttackCode(code);

		connection.write(Encoder.encodeState(code));
		
		if (ships == 0) {
			String lostMessage = "We have lost Commander...";
			log(lostMessage);
			
			connection.write(Encoder.encodeState(Encoder.Code.LOST));
		}
	}
	
	/**
	 * Processes the state code for an enemy attack.
	 * 
	 * @param code the state code.
	 */
	private void processEnemyAttackCode(Encoder.Code code) {
		if (code == Encoder.Code.MISS)
			log("They missed!");
		else if (code == Encoder.Code.HIT)
			log("One of our ships has been hit!");
		else if (code == Encoder.Code.SUNK) {
			log("Our ship was sunk.");
			ships--;
			log("We have " + ships + " ships left Commander.");
		} else
			throw new IllegalArgumentException("lastCode was invalid: " + code);
	}

	/**
	 * Sets this games connector.
	 * 
	 * @param connector to use.
	 */
	public void setConnector(Connector connector) {
		connection = connector;
		setupConnection();
	}

	/**
	 * Adds the connection message listener.
	 */
	private void setupConnection() {
		final boolean HIT = true;
		final boolean MISSED = false;
		isOpponentReady = false;

		connection.addListener((message) -> {
			Encoder.Code code = Encoder.parseCode(message);
			if (code == Encoder.Code.ATTACK) {
				Point point = Encoder.decodeAttack(message);
				processEnemyAttack(point);
			} else if (code == Encoder.Code.HIT) {
				String text = "Enemy ship hit!";
				log(text);
				// if salvo turn continues
				target.fire(HIT);
				if (gamemode == Gamemode.SALVO)
					startTurn();
				else
					sendTurn();
			} else if (code == Encoder.Code.MISS) {
				String text = "No effect on target.";
				log(text);
				target.fire(MISSED);
				sendTurn();
			} else if (code == Encoder.Code.SUNK) {
				String text = "Target ship sunk Commander!";
				log(text);
				target.fire(HIT);
				if (gamemode == Gamemode.SALVO)
					startTurn();
				else
					sendTurn();
			} else if (code == Encoder.Code.TURN_START) {
				startTurn();
			} else if (code == Encoder.Code.READY) {
				isOpponentReady = true;
			} else if (code == Encoder.Code.LOST) {
				connection.write(Encoder.encodeState(Encoder.Code.WON));
				Gui.closeToHome("Congratulations you won!");
			} else if (code == Encoder.Code.WON) {
				Gui.closeToHome("Commander, all of our ships have been sunk. The battle is lost.");
			}
		});
	}

	/**
	 * Notifies the opponent it is their turn.
	 */
	private void sendTurn() {
		connection.write(Encoder.encodeState(Encoder.Code.TURN_START));
		String text = "...Waiting on opponent...";
		log(text);
	}

	/**
	 * Starts this users turn.
	 */
	private void startTurn() {
		isTurn = true;
		String text = "Captain what are your orders!";
		log(text);
	}

	/**
	 * Starts the Gui process for setup of ships.
	 */
	private void setupShips() {
		Platform.runLater(() -> {
			ShipDialog dialog = null;
			try {
				AircraftCarrier carrier = new AircraftCarrier();
				dialog = new ShipDialog(carrier);
				setupShip(dialog);

				Battleship battle = new Battleship();
				dialog = new ShipDialog(battle);
				setupShip(dialog);

				Destroyer destroyer = new Destroyer();
				dialog = new ShipDialog(destroyer);
				setupShip(dialog);

				Submarine sub = new Submarine();
				dialog = new ShipDialog(sub);
				setupShip(dialog);

				Uboat uboat = new Uboat();
				dialog = new ShipDialog(uboat);
				setupShip(dialog);
			} catch (Exception e) {
				if (dialog != null)
					dialog.close();
				Gui.closeToHome(e.getMessage());
				return;
			}

			connection.write(Encoder.encodeState(Encoder.Code.READY));
		});
	}

	/**
	 * Sets up a ship using a ShipDialog.
	 * 
	 * @param dialog the dialog to use.
	 * @throws Exception if the setup is canceled.
	 */
	private void setupShip(ShipDialog dialog) throws Exception {
		Optional<Boolean> response = dialog.showAndWait();
		if (!response.isPresent()) {
			throw new Exception("Canceled Selection");
		} else {
			boolean success = response.get();
			if (!success) {
				setupShip(dialog);
			}
		}
	}

	/**
	 * Logs to the console.
	 * 
	 * @param text the text to log.
	 */
	public void log(String text) {
		final double END = 1.0;
		console.appendText(text + "\n");
		console.setScrollTop(END);
	}

	/**
	 * Returns the in use Connector.
	 * 
	 * @return the Connector currently used by the Game.
	 */
	public Connector getConnector() {
		return connection;
	}

	/**
	 * Starts a new Game, if isFirst is true then this user is then prompted to start their turn.
	 * 
	 * @param isFirst if this user is first.
	 */
	public void startGame(boolean isFirst) {
		setupShips();
		ships = 5;
		if (isFirst)
			startTurn();
	}

	/**
	 * Returns the console display.
	 * 
	 * @return the VBox display of the console.
	 */
	public VBox getConsole() {
		Button clear = new Button("Clear");
		clear.setOnAction(event -> {
			console.clear();
		});
		return new VBox(console, clear);
	}

	/**
	 * Returns the in use HomeBoard.
	 * 
	 * @return the current HomeBoard.
	 */
	public HomeBoard getBoard() {
		return board;
	}

	/**
	 * Returns the in use TargetBoard.
	 * 
	 * @return the current TargetBoard.
	 */
	public TargetBoard getTarget() {
		return target;
	}
}
