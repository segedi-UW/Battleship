import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class BattleScene {

	private static final int BATTLE_SIZE = 800;

	private Scene scene;
	private TargetBoard targetBoard;
	private BorderPane pane;

	public static Scene create() {
		BattleScene battle = new BattleScene();
		return battle.scene;
	}

	private BattleScene() {
		createBattleScene();
	}

	private void createBattleScene() {
		pane = new BorderPane();
		scene = new Scene(pane, BATTLE_SIZE, BATTLE_SIZE);

		addBattleTop();
		addBattleCenter();
		addBattleLeft();
		addBattleRight();
	}

	private void addBattleTop() {
		Label label = new Label("Battleship Terminal");
		label.setAlignment(Pos.CENTER);
		pane.setTop(label);
		pane.setAlignment(label, Pos.CENTER);
	}

	private void addBattleCenter() {

		VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);

		Label targetLabel = new Label("Targeting Grid");
		TargetBoard target = new TargetBoard();

		Label boardLabel = new Label("Local Waters Grid");
		HomeBoard board = new HomeBoard();

		ObservableList list = vbox.getChildren();
		list.add(targetLabel);
		list.add(target);
		list.add(boardLabel);
		list.add(board);
		
		pane.setCenter(vbox);
	}

	private void addBattleLeft() {
		Chat chat = new Chat();
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
			int row = targetBoard.getSelectedRow();
			int column = targetBoard.getSelectedColumn();
			Game.attackEnemy(column, row);
		});
		// TODO Add other buttons
		VBox vbox = new VBox(fire);
		final int RIGHT = 40;
		vbox.setMargin(fire, new Insets(0,RIGHT,0,0));
		pane.setRight(vbox);
	}
}
