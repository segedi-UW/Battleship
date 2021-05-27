import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.concurrent.Task;

public class Gui extends Application {

	private static final Scene HOME = HomeScene.create();

	private static Stage mainStage;
	
	public static void main(String[] args) {
		Application.launch();
	}

	@Override
	public void start(Stage stage) {
		mainStage = stage;
		stage.setTitle("Battleship - War at Sea");
		stage.setScene(HOME);
		stage.show();
	}

	public static void startSingleplayer() {
		// showBattleScreen();
		// TODO
	}

	public static void showMultiplayerDialog() {
		String hostChoice = "Host Game";
		String joinChoice = "Join Game";
		String title = "Game Connect";
		String header = "Host or Join a Game?";
		ChoiceDialog<Object> dialog = new ChoiceDialog<Object>(hostChoice, hostChoice, joinChoice);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.showAndWait().ifPresent(response -> {
			if (response.equals(hostChoice))
				hostGame();
			else
				showJoinDialog();
		});
	}

	private static void showJoinDialog() {
		JoinDialog dialog = new JoinDialog();
		dialog.showAndWait().ifPresent(response -> {
			joinGame(response);
		});
	}

	private static void joinGame(Address address) {
		System.out.println("Joining... " + address.port);
		Connector connector = new ConnectorClient(address.ip, address.port);
		connector.connect();
		Game.setConnector(connector);
		System.out.println("Connected to game");
		showBattleScreen();
		Game.log("...Waiting on opponent...");
		final boolean IS_FIRST = false;
		Game.startGame(IS_FIRST);
	}

	public static void showMessageAlert(String TITLE, String MESSAGE) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setHeaderText(TITLE);
		TextArea area = new TextArea(MESSAGE);
		area.setWrapText(true);
		alert.getDialogPane().setContent(area);
		alert.show();
	}

	private static void showBattleScreen() {
		Scene scene = BattleScene.create();
		mainStage.setScene(scene);
	}

	private static void hostGame() {
		ConnectorServer server = new ConnectorServer();
		Game.setConnector(server);
		Alert alert = createHostingAlert(server);
		startConnectToClientTask(server, alert);
		alert.showAndWait();

		if (server.isConnected()) {
			showBattleScreen();
			final boolean IS_FIRST = true;
			Game.startGame(IS_FIRST);
		}
	}
	
	public static void exitToHome(String message) {
		Platform.runLater(() -> {
			Alert.AlertType type = Alert.AlertType.ERROR;
			Alert alert = new Alert(type, message);
			alert.showAndWait();
			mainStage.setScene(HOME);
		});
	}
	
	public static void closeToHome(String message) {
		Game.getConnector().close();
		exitToHome(message);
	}
	
	private static Alert createHostingAlert(ConnectorServer server) {
		final String TITLE = "Hosting Game";
		final String MESSAGE = "Looking for connection on " 
			+ server.getIP() + " at port " + server.getPort();
		final ButtonType CANCEL = ButtonType.CANCEL;
		Alert alert = new Alert(Alert.AlertType.INFORMATION, MESSAGE, CANCEL);
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

	private static void closeServer(ConnectorServer server) {
		System.out.println("Canceling Connection");
		server.close();
	}

	private static void startConnectToClientTask(ConnectorServer server, Alert alert) {
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

	private static void startTask(Task<Void> task) {
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}

	private static void closeAlert(Alert alert) {
		Platform.runLater(() -> {
			alert.close();
		});
	}
}
