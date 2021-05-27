import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.control.Button;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * Object that represents the targeting grid.
 * 
 * @author youngAgFox
 *
 */
public class TargetBoard extends Board {

	private CoordButton selected;
	private Color selectedPreviousColor;
	private boolean canSelect = true;

	@Override
	public Node createGridNode(Point p) {
		CoordButton coordButton = new CoordButton(p);
		Button button = coordButton.BUTTON;
		final int INT_a = 'a';
		char column = (char) (p.column + INT_a);
		int row = p.row;
		button.setText("" + column + row);
		button.setBackground(BACKGROUND_WHITE);
		button.setBorder(BORDER);
		button.setMinSize(WIDTH, HEIGHT);
		setTargetAction(coordButton);
		addHighlight(coordButton);
		return button;
	}

	/**
	 * Adds all highlighting to a CoordButton.
	 * 
	 * @param coordButton the button to add to.
	 */
	private void addHighlight(CoordButton coordButton) {
		addMouseEnteredHighlight(coordButton);
		addMouseExitHighlight(coordButton);
	}
	
	/**
	 * Adds the mouse entered highlighting to a CoordButton.
	 * 
	 * @param coordButton the button to add to.
	 */
	private void addMouseEnteredHighlight(CoordButton coordButton) {
		Button button = coordButton.BUTTON;
		
		button.setOnMouseEntered(new EventHandler<MouseEvent>() {
			private CoordButton gridButton = coordButton;

			@Override
			public void handle(MouseEvent event) {
				Color color = gridButton.defaultColor.invert();
				setBackground(color);
			}
			
			private void setBackground(Color color) {
				BackgroundFill fill = new BackgroundFill(color, null, null);
				Background background = new Background(fill);
				button.setBackground(background);
			}
		});
	}
	
	/**
	 * Adds the mouse exit highlighting to a CoordButton.
	 * 
	 * @param coordButton the button to add to.
	 */
	private void addMouseExitHighlight(CoordButton coordButton) {
		Button button = coordButton.BUTTON;
		
		button.setOnMouseExited(new EventHandler<MouseEvent>() {
			private CoordButton gridButton = coordButton;

			@Override
			public void handle(MouseEvent event) {
				Color color = gridButton.defaultColor;
				setBackground(color);
			}
			
			private void setBackground(Color color) {
				BackgroundFill fill = new BackgroundFill(color, null, null);
				Background background = new Background(fill);
				button.setBackground(background);
			}
		});
	}

	/**
	 * Selects the passed CoordButton.
	 * 
	 * @param button the button to select.
	 */
	private void selectButton(CoordButton button) {
		if (canSelect) {
			if (button.isSelectable) {
				if (selected != null) {
					setSelectedBackground(selectedPreviousColor);
				}
				if (!button.equals(selected)) {
					selected = button;
					selectedPreviousColor = selected.defaultColor;
					setSelectedBackground(Color.ORANGE);
				} else {
					selected = null;
				}
			}
		}
	}
	
	/**
	 * Stops the change of selection until the fire method is called.
	 */
	public void waitForFire() {
		canSelect = false;
	}

	/**
	 * Sets the selected CoordButton's background to the passed color.
	 * 
	 * @param color the color to use in the new background.
	 */
	private void setSelectedBackground(Color color) {
		BackgroundFill fill = new BackgroundFill(color, null, null);
		Background background = new Background(fill);
		if (selected != null) {
			selected.defaultColor = color;
			selected.BUTTON.setBackground(background);
		}
	}
	
	/**
	 * Paints the selected space blue if hit is false, red if hit is true.
	 * 
	 * @param hit whether the shot hit an enemy or not.
	 */
	public void fire(boolean hit) {
		selected.isSelectable = false;
		Color color = hit ? Color.RED : Color.AQUAMARINE;
		setSelectedBackground(color);
		selected = null;
		canSelect = true;
	}

	/**
	 * Sets the selection action on a CoordButton click.
	 * 
	 * @param coordButton the CoordButton to add the behavior to.
	 */
	private void setTargetAction(CoordButton coordButton) {
		Button button = coordButton.BUTTON;
		button.setOnAction( e ->  {
			selectButton(coordButton);
		});
	}
	
	/**
	 * Returns the currently selected CoordButton's point.
	 * 
	 * @return the Point of the selected CoordButton.
	 */
	public Point getSelectedPoint() {
		return selected == null ? null : selected.POINT;
	}

	/**
	 * An object representing a Button that has a coordinate (Point), a default color, and 
	 * a field that filters selection.
	 * 
	 * @author youngAgFox
	 *
	 */
	private class CoordButton {
		private final Point POINT;
		private final Button BUTTON;
		private Color defaultColor;
		private boolean isSelectable;
		
		public CoordButton(Point point) {
			this(point.column, point.row);
		}
		
		public CoordButton(int column, int row) {
			BUTTON = new Button();
			POINT = new Point(column, row);
			defaultColor = Color.WHITE;
			isSelectable = true;
		}
	}
}
