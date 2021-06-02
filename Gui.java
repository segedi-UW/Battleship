import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * An object that handles the Gui parts of this program.
 * 
 * @author youngAgFox
 *
 */
public class Gui extends Application {

	private static final Scene HOME = HomeScene.create();

	private static Stage stage;
	private static Game game;
	
	/**
	 * Launches the GUI Application
	 * 
	 * @param args the command line arguments (unused).
	 */
	public static void main(String[] args) {
		Application.launch();
	}

	@Override
	public void start(Stage mainStage) {
		stage = mainStage;
		stage.setTitle("Battleship - War at Sea");
		stage.setScene(HOME);
		stage.show();
	}

	/**
	 * Starts a single player game
	 */
	public static void startSingleplayer() {
		// showBattleScreen();
		// TODO for future implementation
	}

	/**
	 * Shows choices for choosing to Host or Join a multiplayer game.
	 */
	public static void showMultiplayerDialog() {
		String hostChoice = "Host Game";
		String joinChoice = "Join Game";
		String title = "Game Connect";
		String header = "Host or Join a Game?";
		ChoiceDialog<Object> dialog = new ChoiceDialog<Object>(hostChoice, hostChoice, joinChoice);
		centerOnStage(dialog);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.showAndWait().ifPresent(response -> {
			if (response.equals(hostChoice))
				hostGame();
			else
				showJoinDialog();
		});
	}
	
	public static <T> void centerOnStage(Dialog<T> dialog) {
		final int divisor = 4; // estimation accounting for size of most dialogs
		
		double stageCenterX = stage.getX() + (stage.getWidth() / divisor);
		double stageCenterY = stage.getY() + (stage.getHeight() / divisor);
		
		dialog.setX(stageCenterX);
		dialog.setY(stageCenterY);
	}

	/**
	 * Shows the join game Dialog.
	 */
	private static void showJoinDialog() {
		JoinDialog dialog = new JoinDialog();
		centerOnStage(dialog);
		dialog.showAndWait().ifPresent(response -> {
			joinGame(response);
		});
	}

	/**
	 * Joins a game at the given address.
	 * 
	 * @param address the address to join at.
	 */
	private static void joinGame(Address address) {
		game = new Game();
		System.out.println("Joining... " + address.port);
		Connector connector = new Client(address.ip, address.port);
		connector.connect();
		game.setConnector(connector);
		System.out.println("Connected to game");
		showBattleScreen();
		game.log("...Waiting on opponent...");
		final boolean IS_FIRST = false;
		game.startGame(IS_FIRST);
	}

	/**
	 * Shows a blocking alert with a passed title and message.
	 * 
	 * @param TITLE the title
	 * @param MESSAGE the content message
	 */
	public static void showMessageAlert(String TITLE, String MESSAGE) {
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			centerOnStage(alert);
			alert.setHeaderText(TITLE);
			TextArea area = new TextArea(MESSAGE);
			area.setWrapText(true);
			alert.getDialogPane().setContent(area);
			alert.showAndWait();
		});
	}
	
	/**
	 * Returns the current Game.
	 * 
	 * @return the current Game
	 */
	public static Game getGame() {
		return game;
	}
	
	public static Window getWindow() {
		return Stage.getWindows().get(0);
	}

	/**
	 * Displays the Battle Screen (game play screen).
	 */
	private static void showBattleScreen() {
		Scene scene = BattleScene.create();
		stage.setScene(scene);
	}

	/**
	 * Hosts a new game.
	 */
	private static void hostGame() {
		game = new Game();
		Server server = new Server();
		game.setConnector(server);
		Alert alert = createHostingAlert(server);
		startConnectToClientTask(server, alert);
		alert.showAndWait();

		if (server.isConnected()) {
			showBattleScreen();
			final boolean IS_FIRST = true;
			game.startGame(IS_FIRST);
		}
	}
	
	/**
	 * Exits the current game and shows the home screen and a message.
	 * 
	 * @param message the message describing the exit.
	 */
	public static void exitToHome(String message) {
		Platform.runLater(() -> {
			Alert.AlertType type = Alert.AlertType.INFORMATION;
			Alert alert = new Alert(type, message);
			alert.showAndWait();
			stage.setScene(HOME);
		});
	}
	
	/**
	 * Closes the Connection and exits the current game, showing the home screen
	 * and an associated message.
	 * 
	 * @param message the message detailing the close and exit.
	 */
	public static void closeToHome(String message) {
		game.getConnector().close();
		exitToHome(message);
	}
	
	/**
	 * Creates an interruptible hosting alert with ip address and port information
	 * 
	 * @param server the ConnectorServer serving as the host.
	 * @return the Alert object.
	 */
	private static Alert createHostingAlert(Server server) {
		final String TITLE = "Hosting Game";
		final String MESSAGE = "Looking for connection on " 
			+ server.getHost() + " at port " + server.getPort();
		final ButtonType CANCEL = ButtonType.CANCEL;
		Alert alert = new Alert(Alert.AlertType.INFORMATION, MESSAGE, CANCEL);
		centerOnStage(alert);
		alert.setHeaderText(TITLE);
		alert.setOnCloseRequest(e -> {
			if (!server.isConnected()) {
				closeServer(server);
				System.out.println("Canceled Hosting");
			}
		});

		System.out.println(MESSAGE);
		return alert;
	}

	/**
	 * Closes the ConnectorServer
	 * 
	 * @param server the server to close.
	 */
	private static void closeServer(Server server) {
		System.out.println("Canceling Connection");
		server.close();
	}

	/**
	 * Starts an interruptible server accept task.
	 * 
	 * @param server the server to use.
	 * @param alert the alert to display.
	 */
	private static void startConnectToClientTask(Server server, Alert alert) {
		Task<Void> task = new Task<>() {
			@Override
			public Void call() {
				server.connect();
				if (server.isConnected()) {
					System.out.println("Connected to game");
					closeAlert(alert);
				}
				return null;
			}
		};
		startTask(task);
	}

	/**
	 * Starts the task.
	 * 
	 * @param task
	 */
	private static void startTask(Task<Void> task) {
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Closes the alert.
	 * 
	 * @param alert
	 */
	private static void closeAlert(Alert alert) {
		Platform.runLater(() -> {
			alert.close();
		});
	}
}
