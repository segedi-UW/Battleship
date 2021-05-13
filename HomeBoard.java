import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.Node;
public class HomeBoard extends Board {

	public HomeBoard() {
		super();
	}

	@Override
	public Node createGridNode(int column, int row) {
		Label label = new Label();
		label.setBackground(whiteBackground);
		label.setBorder(border);
		label.setMinSize(WIDTH, HEIGHT);
		return label;
	}
}
