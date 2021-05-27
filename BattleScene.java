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

public class BattleScene {

	private static final int BATTLE_SIZE = 800;

	private Scene scene;
	private BorderPane pane;

	public static Scene create() {
		BattleScene battle = new BattleScene();
		return battle.scene;
	}

	private BattleScene() {
		pane = new BorderPane();
		scene = new Scene(pane, BATTLE_SIZE, BATTLE_SIZE);

		createBattleScene();
	}

	private void createBattleScene() {
		addBattleTop();
		addBattleCenter();
		addBattleLeft();
		addBattleRight();
	}

	private void addBattleTop() {
		Label label = new Label("Battleship Terminal");
		label.setAlignment(Pos.CENTER);
		pane.setTop(label);
		BorderPane.setAlignment(label, Pos.CENTER);
	}

	private void addBattleCenter() {

		VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.setBackground(null);

		Label targetLabel = new Label("Targeting Grid");

		Label boardLabel = new Label("Local Waters Grid");
		HomeBoard board = Game.getBoard();

		ObservableList<Node> list = vbox.getChildren();
		list.add(targetLabel);
		TargetBoard target = Game.getTarget();
		list.add(target.getPane());
		list.add(boardLabel);
		list.add(board.getPane());
		
		pane.setCenter(vbox);
	}

	private void addBattleLeft() {
		Chat chat = new Chat(Game.getConnector());
		pane.setLeft(chat.getDisplay());
	}

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
			TargetBoard target = Game.getTarget();
			Point point = target.getSelectedPoint();
			if (point != null)
				Game.fire(point);
		});
		// TODO Add other buttons
		
		VBox console = Game.getConsole();
		VBox vbox = new VBox(fire, console);
		final int RIGHT = 40;
		VBox.setMargin(fire, new Insets(0,RIGHT,0,0));
		fire.setAlignment(Pos.BASELINE_CENTER);
		pane.setRight(vbox);
	}
}
