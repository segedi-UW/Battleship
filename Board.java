import javafx.scene.layout.GridPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.Node;
import javafx.geometry.Pos;

public abstract class Board {
	
	private GridPane pane;

	protected static final double WIDTH = 35.0;
	protected static final double HEIGHT = 35.0;
	private static final int LENGTH = 10;

	private static final BorderStrokeStyle SOLID = BorderStrokeStyle.SOLID;
	private static final BorderStroke borderStroke = new BorderStroke(Color.BLACK, SOLID, null, null);
	protected static final Border BORDER = new Border(borderStroke);

	private static final BackgroundFill FILL_WHITE = new BackgroundFill(Color.WHITE, null, null);
	protected static final Background BACKGROUND_WHITE = new Background(FILL_WHITE);
	
	private static final BackgroundFill FILL_BLUE = new BackgroundFill(Color.AZURE, null, null);
	protected static final Background BACKGROUND_BLUE = new Background(FILL_BLUE);
	
	private static final BackgroundFill FILL_RED = new BackgroundFill(Color.MAROON, null, null);
	protected static final Background BACKGROUND_RED = new Background(FILL_RED);
	
	private static final BackgroundFill FILL_GRAY = new BackgroundFill(Color.GRAY, null, null);
	protected static final Background BACKGROUND_GRAY = new Background(FILL_GRAY);
	
	private static final BackgroundFill FILL_ROYALBLUE = new BackgroundFill(Color.ROYALBLUE, null, null);
	protected static final Background BACKGROUND_ROYALBLUE = new Background(FILL_ROYALBLUE);
	
	public Board() {
		pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setBackground(null);
		makeBoard();
	}

	private void makeBoard() {
		for (int column = 0; column < LENGTH; column++) {
			for (int row = 0; row < LENGTH; row++) {
				Point p = new Point(column, row);
				Node node = createGridNode(p);
				pane.add(node, column, row);
			}
		}
	}

	public abstract Node createGridNode(Point p);
	
	public GridPane getPane() {
		return pane;
	}
}
