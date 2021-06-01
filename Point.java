/**
 * A Data object containing the column and row coordinate pair.
 * Also contains an overridden equals, toString, and hashCode methods to allow equivalence
 * using java comparison and java Hashtable's.
 * 
 * @author youngAgFox
 *
 */
public class Point {

	public int column,row;
	
	public Point(int column, int row) {
		this.column = column;
		this.row = row;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Point) {
			Point p = (Point) o;
			boolean sameColumn = p.column == column;
			boolean sameRow = p.row == row;
			if (sameColumn && sameRow) 
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (column * 10) + row;
	}

	public String toString() {
		int a = 'a';
		char c = (char) (column + a);
		return "" + c + row;
	}
}
