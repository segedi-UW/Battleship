import java.util.Hashtable;

import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

/**
 * An object that represents the Home Grid including the user's ships.
 * 
 * @author youngAgFox
 *
 */
public class HomeBoard extends Board {
	
	private Hashtable<Point, Ship> ships;
	private Hashtable<Point, Label> spaces;
	private Encoder.Code lastCode;

	/**
	 * Creates the HomeBoard with no ships.
	 */
	public HomeBoard() {
		ships = new Hashtable<>();
	}

	@Override
	public Label createGridNode(Point point) {
		if (spaces == null)
			spaces = new Hashtable<>();
		final int INT_a = 'a';
		char column = (char) (point.column + INT_a);
		int row = point.row;
		Label label = new Label("" + column + row);
		label.setTextAlignment(TextAlignment.CENTER);
		label.setBackground(BACKGROUND_BLUE);
		label.setBorder(BORDER);
		label.setMinSize(WIDTH, HEIGHT);
		spaces.put(point, label);
		return label;
	}
	
	/**
	 * Adds a ship to the board if there is no ship with an associated point already.
	 * 
	 * @param ship the ship to add.
	 * @throws IllegalArgumentException if any point is associated with a different ship.
	 */
	public void addShip(Ship ship) {
		Point[] points = ship.getPoints();
		for (int i = 0; i < points.length; i++) {
			Point point = points[i];
			if (ships.containsKey(point))
				throw new IllegalArgumentException("Point already associated with a different ship");
		}
		for (int i = 0; i < points.length; i++) {
			Point point = points[i];
			ships.put(point, ship);
			spaces.get(point).setBackground(BACKGROUND_GRAY);
		}
	}
	
	/**
	 * Returns the number of ships in this board.
	 * 
	 * @return the number of ships.
	 */
	public int size() {
		return ships.size();
	}
	
	/**
	 * Removes a ship from the board.
	 * 
	 * @param ship the ship to remove.
	 */
	public void removeShip(Ship ship) {
		Point[] points = ship.getPoints();
		for (int i = 0; i < points.length; i++) {
			Point point = points[i];
			ships.remove(point);
		}
	}
	
	/**
	 * Attacks the board at a given point, updating the code to the
	 * appropriate value (HIT, MISS, or SUNK).
	 * 
	 * @param point the point attacked.
	 */
	public void attack(Point point) {
		Ship ship = ships.get(point);
		Label space = spaces.get(point);
		
		if (space == null)
			throw new IllegalArgumentException("Point does not exist in board: " + point);
		
		if (ship == null) {
			// Change the space to blue
			space.setBackground(BACKGROUND_ROYALBLUE);
			lastCode = Encoder.Code.MISS;
		} else {
			// hit ship -> hit
			// make space red
			space.setBackground(BACKGROUND_RED);
			lastCode = Encoder.Code.HIT;
			ship.attack(point);
			if (ship.isDestroyed())
				lastCode = Encoder.Code.SUNK;
		}
	}
	
	/**
	 * Returns the result of the last call of the attack(Point) method.
	 * 
	 * @return the last code saved as a result of the attack(Point) method call.
	 */
	public Encoder.Code getLastCode() {
		return lastCode;
	}
}
