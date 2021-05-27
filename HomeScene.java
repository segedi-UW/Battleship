import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * An object that creates the Home Screen for the Gui.
 * 
 * @author youngAgFox
 *
 */
public class HomeScene {

	private static final int HOME_SIZE = 500;

	private Scene scene;

	/**
	 * Creates the Home Screen Scene.
	 * 
	 * @return the Home Screen Scene.
	 */
	public static Scene create() {
		HomeScene home = new HomeScene();
		return home.scene;
	}

	/**
	 * Creates the Home Screen Scene.
	 */
	private HomeScene() {
		BorderPane borderPane = new BorderPane();
		scene = new Scene(borderPane, HOME_SIZE, HOME_SIZE);

		addCenter(borderPane);
	}
	
	/**
	 * Adds the center panel to the borderPane.
	 * 
	 * @param borderPane the BorderPane to add to.
	 */
	private void addCenter(BorderPane borderPane) {
		final double SPACING = 15.0;

		VBox vbox =  new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.setSpacing(SPACING);

		addTitle(vbox);
		addButtons(vbox);

		borderPane.setCenter(vbox);
	}

	/**
	 * Adds the title text to the VBox.
	 * 
	 * @param vbox the VBox to add to.
	 */
	private void addTitle(VBox vbox) {
		Label label = new Label("BATTLESHIP");
		final int FONT_SIZE = 50;
		final int INSET_BOTTOM = 50;
		label.setStyle("-fx-font-size: " + FONT_SIZE + "px;");
		VBox.setMargin(label, new Insets(0, 0, INSET_BOTTOM, 0));

		ObservableList<Node> list = vbox.getChildren();
		list.add(label);
	}

	/**
	 * Adds the button to the VBox.
	 * 
	 * @param vbox the VBox to add to.
	 */
	private void addButtons(VBox vbox) {
		final double WIDTH = 150.0;
		final double HEIGHT = 50.0;
		Button singleplayer= new Button("Singleplayer");
		singleplayer.setPrefSize(WIDTH, HEIGHT);
		singleplayer.setOnAction(e -> {
			Gui.startSingleplayer();
		});
		Button multiplayer= new Button("Play"); // TODO After adding single, rename
		multiplayer.setPrefSize(WIDTH, HEIGHT);
		multiplayer.setOnAction(e -> {
			Gui.showMultiplayerDialog();
		});
		Button help = new Button("Help");
		help.setPrefSize(WIDTH, HEIGHT);
		help.setOnAction(e -> {
			showHelp();
		});
		Button exit= new Button("Exit");
		exit.setPrefSize(WIDTH, HEIGHT);
		exit.setOnAction(e -> {
			Platform.exit();
		});

		ObservableList<Node> list = vbox.getChildren();
		// list.add(singleplayer); // TODO Add back in after implementing
		list.add(multiplayer);
		list.add(help);
		list.add(exit);
	}

	/**
	 * Shows the Help Screen.
	 */
	private void showHelp() {
		String title = "Help";
		String message = "Welcome to Battleship!\n" + 
			"The main objective of the game is to sink all of the enemies ships. " +
			"In all game modes there is the standard fire command which can be " + 
			"triggered by selecting a space on the target board (the top one) when " +
			"it is your turn, and then clicking the fire button the right hand side. " +
			"In salvo mode, you will be able to continously fire " +
			"if your previous shot connected with a target. In advanced weapons mode " +
			"you will have additional weapon systems available to you to unleash on " +
			"enemies.";
		// TODO Add advanced weapon systems tutorial
		Gui.showMessageAlert(title, message);
	}
}
