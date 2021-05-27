import java.util.Optional;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class Game {
	
	public enum Gamemode {
		STANDARD, SALVO, ADVANCED;
	}
	
	private static Gamemode gamemode = Gamemode.STANDARD;
	private static boolean isTurn = false;
	private static Connector connection;
	private static TextArea console = createConsole();
	private static HomeBoard board = new HomeBoard();
	private static TargetBoard target = new TargetBoard();
	private static int ships;
	
	private static boolean isOpponentReady;
	
	private static TextArea createConsole() {
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

	public static void fire(Point point) {
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
	
	public static boolean isTurn() {
		return isTurn;
	}

	private static void enemyAttack(Point point) {
		String text = "The enemy has fired upon us!";
		log(text);
		board.attack(point);
		Encoder.Code code = board.getLastCode();
		
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
		
		connection.write(Encoder.encodeState(code));
		if (ships == 0) {
			log("We have lost Commander...");
			connection.write(Encoder.encodeState(Encoder.Code.LOST));
		}
	}
	
	public static void setConnector(Connector connector) {
		connection = connector;
		setupConnection();
	}
	
	private static void setupConnection() {
		final boolean HIT = true;
		final boolean MISSED = false;
		isOpponentReady = false;
		
		connection.addListener((message) -> {
			Encoder.Code code = Encoder.parseCode(message);
			if (code == Encoder.Code.ATTACK) {
				Point point = Encoder.decodeAttack(message);
				enemyAttack(point);
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
	
	private static void sendTurn() {
		connection.write(Encoder.encodeState(Encoder.Code.TURN_START));
		String text = "...Waiting on opponent...";
		log(text);
	}
	
	private static void startTurn() {
		isTurn = true;
		String text = "Captain what are your orders!";
		log(text);
	}
	
	private static void setupShips() {
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
	}
	
	private static void setupShip(ShipDialog dialog) throws Exception {
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
	
	public static void log(String text) {
		final double END = 1.0;
		console.appendText(text + "\n");
		console.setScrollTop(END);
	}
	
	public static Connector getConnector() {
		return connection;
	}
	
	public static void startGame(boolean isFirst) {
		setupShips();
		ships = 5;
		if (isFirst)
			startTurn();
	}

	public static VBox getConsole() {
		Button clear = new Button("Clear");
		clear.setOnAction(event -> {
			console.clear();
		});
		return new VBox(console, clear);
	}
	
	public static HomeBoard getBoard() {
		return board;
	}
	
	public static TargetBoard getTarget() {
		return target;
	}
}
