import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.concurrent.Task;

public class Gui extends Application {

	private final Scene HOME;

	private Stage stage;
	
	public static void main(String[] args) {
		Application.launch();
	}

	public Gui() {
		HOME = HomeScene.create(this);
	}

	@Override
	public void start(Stage stage) {
		this.stage = stage;
		stage.setTitle("Battleship - War at Sea");
		stage.setScene(HOME);
		stage.show();
	}

	public void startSingleplayer() {
		// showBattleScreen();
		// TODO
	}

	public void showMultiplayerDialog() {
		String hostChoice = "Host Game";
		String joinChoice = "Join Game";
		String title = "Game Connect";
		String header = "Host or Join a Game?";
		ChoiceDialog dialog = new ChoiceDialog(hostChoice, hostChoice, joinChoice);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.showAndWait().ifPresent(response -> {
			if (response.equals(hostChoice))
				hostGame();
			else
				showJoinDialog();
		});
	}

	private void showJoinDialog() {
		JoinDialog dialog = new JoinDialog();
		dialog.showAndWait().ifPresent(response -> {
			joinGame(response);
		});
	}

	private void joinGame(Address address) {
		System.out.println("Joining... " + address.port);
		ChatClient client = new ChatClient(address.ip, address.port);
		client.connect();
		// Encoder.setSocket(client); TODO
		System.out.println("Connected to game");
		showBattleScreen();
	}

	public void showMessageAlert(String TITLE, String MESSAGE) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setHeaderText(TITLE);
		TextArea area = new TextArea(MESSAGE);
		area.setWrapText(true);
		alert.getDialogPane().setContent(area);
		alert.show();
	}

	private void showBattleScreen() {
		Scene scene = BattleScene.create();
		stage.setScene(scene);
	}

	private void hostGame() {
		ChatServer server = new ChatServer();
		Alert alert = createHostingAlert(server);
		startConnectToClientTask(server, alert);
		alert.showAndWait();

		if (server.isConnected()) {
			//Encoder.setSocket(server); // TODO
			showBattleScreen();
		}
	}

	private Alert createHostingAlert(ChatServer server) {
		final String TITLE = "Hosting Game";
		final String MESSAGE = "Looking for connection on " 
			+ server.getIP() + " at port " + server.getPort();
		final ButtonType CANCEL = ButtonType.CANCEL;
		Alert alert = new Alert(Alert.AlertType.INFORMATION, MESSAGE, CANCEL);
		alert.setHeaderText(TITLE);
		alert.setOnCloseRequest(e -> {
			if (!server.isConnected()) 
				closeServer(server);
		});

		System.out.println(MESSAGE);
		return alert;
	}

	private void closeServer(ChatServer server) {
		System.out.println("Canceling Connection");
		server.close();
	}

	private void startConnectToClientTask(ChatServer server, Alert alert) {
		Task<Void> task = new Task<>() {
			@Override
			public Void call() {
				server.connect();
				closeAlert(alert);
				return null;
			}
		};
		startTask(task);
	}

	private void startTask(Task task) {
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}

	private void closeAlert(Alert alert) {
		Platform.runLater(() -> {
			alert.close();
		});
	}
}
