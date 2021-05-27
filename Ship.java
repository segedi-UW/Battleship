/**
 * An object representing a Ship in Battleship. Has points, and a boolean array of where
 * this ship has been hit.
 * 
 * @author youngAgFox
 *
 */
public abstract class Ship {
	
	/**
	 * The Type of ship, containing information on ship length and name.	
	 * 
	 * @author youngAgFox
	 *
	 */
	public enum Type {
		AIRCRAFT_CARRIER(5, "Aircraft Carrier"), BATTLESHIP(4, "Battleship"), 
		DESTROYER(3, "Destroyer"), SUBMARINE(3, "Submarine"), UBOAT(2, "U-Boat");
		
		private final int LENGTH;
		private final String NAME;
		
		private Type(int length, String name) {
			LENGTH = length;
			NAME = name;
		}
	}
	
	private Point[] points;
	private boolean[] hits;
	private Type type;
	
	/**
	 * Creates a new Ship of a particular Type.
	 * 
	 * @param type the type of Ship.
	 */
	public Ship(Type type) {
		this.type = type;
		points = new Point[type.LENGTH];
		hits = new boolean[type.LENGTH];
	}
	
	/**
	 * Sets up the ship with a start Point and end Point.
	 * 
	 * @param start the point to start at.
	 * @param end the point to end at.
	 * @throws IllegalArgumentException if the ship is not placed soley horizontally
	 * or vertically, or if the ship length does no match the difference between the
	 * start and end Points.
	 */
	public void setup(Point start, Point end) {
		int length = points.length;
		int range = Math.abs(end.row - start.row);
		int domain = Math.abs(end.column - start.column);
		int max = Math.max(range,  domain);
		int min = Math.min(range,  domain);
		boolean isHorizontal = max == domain ? true : false;
		if (max != length - 1 || min != 0)
			throw new IllegalArgumentException("Cannot setup ship of length "
					+ length + " in " + max + " spaces");
		boolean incRight = end.column - start.column > 0;
		boolean incDown = end.row - start.row > 0;
		final int modc = isHorizontal ? (incRight ? 1 : -1) : 0;
		final int modr = !isHorizontal ? (incDown ? 1 : -1) : 0;
		int c = start.column, r = start.row;
		for (int i = 0; i < length; i++) {
			points[i] = new Point(c, r);
			c += modc;
			r += modr;
		}
	}
	
	/**
	 * Attacks the ship at a particular Point.
	 * 
	 * @param point the Point to attack
	 */
	public void attack(Point point) {
		for (int i = 0; i < points.length; i++) {
			if (points[i].equals(point))
				hits[i] = true;
		}
	}

	/**
	 * Returns if this ship has been sunk
	 * 
	 * @return true if every Point has been hit, false otherwise.
	 */
	public boolean isDestroyed() {
		for (int i = 0; i < hits.length; i++) {
			boolean hit = hits[i];
			if (!hit)
				return false;
		}
		return true;
	}
	
	/**
	 * Returns this ship's Point's.
	 * 
	 * @return all Point objects for this ship in an array.
	 */
	public Point[] getPoints() {
		return points;
	}
	
	/**
	 * Returns the number of Point objects this ship has.
	 * 
	 * @return the number of Point objects.
	 */
	public int getLength() {
		return points.length;
	}
	
	/**
	 * Returns the name of the ship.
	 * 
	 * @return the ship name.
	 */
	public String getName() {
		return type.NAME;
	}
}
