import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Creates the Scene object that is used for Battleship game play.
 * 
 * @author youngAgFox
 *
 */
public class BattleScene {

	private static final int BATTLE_SIZE = 800;

	private Scene scene;
	private BorderPane pane;
	private Game game;

	/**
	 * Creates the battle Scene and returns it to the caller
	 * 
	 * @return the battle (game play) Scene object
	 */
	public static Scene create() {
		BattleScene battle = new BattleScene();
		return battle.scene;
	}

	/**
	 * Constructs the BattleScene with the current game
	 */
	private BattleScene() {
		pane = new BorderPane();
		scene = new Scene(pane, BATTLE_SIZE, BATTLE_SIZE);
		game = Gui.getGame();

		createBattleScene();
	}

	/**
	 * Adds all panels to the Scene object
	 */
	private void createBattleScene() {
		addBattleTop();
		addBattleCenter();
		addBattleLeft();
		addBattleRight();
	}

	/**
	 * Adds the top panel to the Scene
	 */
	private void addBattleTop() {
		Label label = new Label("Battleship Terminal");
		label.setAlignment(Pos.CENTER);
		pane.setTop(label);
		BorderPane.setAlignment(label, Pos.CENTER);
	}

	/**
	 * Adds the center panel to the Scene
	 */
	private void addBattleCenter() {
		
		VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.setBackground(null);

		Label targetLabel = new Label("Targeting Grid");

		Label boardLabel = new Label("Local Waters Grid");
		HomeBoard board = game.getBoard();

		ObservableList<Node> list = vbox.getChildren();
		list.add(targetLabel);
		TargetBoard target = game.getTarget();
		list.add(target.getPane());
		list.add(boardLabel);
		list.add(board.getPane());
		
		pane.setCenter(vbox);
	}

	/**
	 * Adds the left panel to the Scene
	 */
	private void addBattleLeft() {
		Chat chat = new Chat(game.getConnector());
		pane.setLeft(chat.getDisplay());
	}

	/**
	 * Adds the right panel to the Scene
	 */
	private void addBattleRight() {
		// TODO
		BackgroundFill redFill = new BackgroundFill(Color.RED, null, null);
		Background red = new Background(redFill);
		Button fire = new Button("Fire");
		fire.setBackground(red);
		final double WIDTH = 100.0;
		final double HEIGHT = 40.0;
		fire.setPrefSize(WIDTH, HEIGHT);
		fire.setOnAction(e -> {
			TargetBoard target = game.getTarget();
			Point point = target.getSelectedPoint();
			if (point != null)
				game.fire(point);
		});
		// TODO Add other buttons
		
		VBox console = game.getConsole();
		VBox vbox = new VBox(fire, console);
		final int RIGHT = 40;
		VBox.setMargin(fire, new Insets(0,RIGHT,0,0));
		fire.setAlignment(Pos.BASELINE_CENTER);
		pane.setRight(vbox);
	}
}
