public class Player {

	public String name;
	public Ship[] ships;

	public Player() {
		this("Enemy Player");
	}

	public Player(String name) {
		this.name = name;
		ships = new Ship[5];
	}

}
