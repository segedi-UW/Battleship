import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.control.Button;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;

public class TargetBoard extends Board {

	private CoordButton selected;
	private boolean isActive;

	private Background orangeBackground; 

	public TargetBoard() {
		super();
		BackgroundFill orangeFill = new BackgroundFill(Color.rgb(130, 40, 120, 0.3), null, null);
		orangeBackground = new Background(orangeFill);
	}

	@Override
	public Node createGridNode(int column, int row) {
		CoordButton button = new CoordButton(column, row);
		button.setBackground(whiteBackground);
		button.setBorder(border);
		button.setMinSize(WIDTH, HEIGHT);
		setTargetAction(button);
		addHighlight(button);
		return button;
	}

	private void addHighlight(Button button) {

		BackgroundFill highlightFill = new BackgroundFill(Color.rgb(100, 200, 200, 0.3), null, null);
		Background highlightBackground = new Background(highlightFill);
		BackgroundFill inactiveHighlightFill = new BackgroundFill(Color.rgb(100, 100, 100, 0.3), null, null);
		Background inactiveHighlightBackground = new Background(inactiveHighlightFill);
		BackgroundFill whiteFill = new BackgroundFill(Color.WHITE, null, null);
		Background whiteBackground = new Background(whiteFill);

		button.setOnMouseEntered(new EventHandler<MouseEvent>() {
			private Button gridButton = button;
			private Background inactiveHighlight = inactiveHighlightBackground;
			private Background activeHighlight = highlightBackground;

			@Override
			public void handle(MouseEvent event) {
				if (isActive)
					gridButton.setBackground(activeHighlight);
				else
					gridButton.setBackground(inactiveHighlight);
			}
		});

		button.setOnMouseExited(new EventHandler<MouseEvent>() {
			private Button gridButton = button;
			private Background white = whiteBackground;

			@Override
			public void handle(MouseEvent event) {
				if (!gridButton.equals(selected))
					gridButton.setBackground(white);
			}
		});
	}

	private void selectButton(CoordButton button) {
		if (selected != null)
			setSelectedBackground(whiteBackground);
		if (!button.equals(selected)) {
			selected = button;
			setSelectedBackground(orangeBackground);
		} else {
			selected = null;
		}
	}

	private void setSelectedBackground(Background background) {
		if (selected != null)
			selected.setBackground(background);
	}

	private void setTargetAction(CoordButton button) {
		button.setOnAction( e ->  {
			selectButton(button);
		});
	}

	public int getSelectedColumn() {
		return selected.column;
	}

	public int getSelectedRow() {
		return selected.row;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	private class CoordButton extends Button {
		public final int column;
		public final int row;

		public CoordButton(int column, int row) {
			super();
			this.column = column;
			this.row = row;
		}
	}
}
