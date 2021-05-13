import javafx.scene.layout.GridPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
public abstract class Board extends GridPane {

	protected static final double WIDTH = 35.0;
	protected static final double HEIGHT = 35.0;
	private static BorderStrokeStyle SOLID = BorderStrokeStyle.SOLID;
	private static BorderStroke borderStroke = new BorderStroke(Color.BLACK, SOLID, null, null);
	protected static Border border = new Border(borderStroke);

	private static final int LENGTH = 10;
		
	private static BackgroundFill whiteFill = new BackgroundFill(Color.WHITE, null, null);
	protected static Background whiteBackground = new Background(whiteFill);

	public Board() {
		setAlignment(Pos.CENTER);
		makeBoard();
	}

	private void makeBoard() {
		for (int column = 0; column < LENGTH; column++) {
			for (int row = 0; row < LENGTH; row++) {
				add(createGridNode(column, row), column, row);
			}
		}
	}

	public abstract Node createGridNode(int column, int row);
}
