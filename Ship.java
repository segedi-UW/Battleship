public class Ship {
	
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
	
	public Ship(Type type) {
		this.type = type;
		points = new Point[type.LENGTH];
		hits = new boolean[type.LENGTH];
	}
	
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
	
	public void attack(Point p) {
		for (int i = 0; i < points.length; i++) {
			if (points[i].equals(p))
				hits[i] = true;
		}
	}

	public boolean isDestroyed() {
		for (int i = 0; i < hits.length; i++) {
			boolean hit = hits[i];
			if (!hit)
				return false;
		}
		return true;
	}
	
	public Point[] getPoints() {
		return points;
	}
	
	public int getLength() {
		return points.length;
	}
	
	public String getName() {
		return type.NAME;
	}
}
