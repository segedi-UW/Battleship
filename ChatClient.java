import java.io.IOException;
import java.net.Socket;

/**
 * An object representing the Client Connector
 * 
 * @author youngAgFox
 *
 */
public class Client extends Connector {

	private String ip;
	private int port;

	/**
	 * Connects this Client Connector to the Host Connector.
	 * 
	 * @param ip the ip address to connect to
	 * @param port the port to connect to
	 */
	public Client(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	@Override
	public void connect() {
		try {
			connect(new Socket(ip, port));
		} catch (IOException e) {
			System.out.println("Failed to connect: " + e.getMessage());
		}
	}

}
