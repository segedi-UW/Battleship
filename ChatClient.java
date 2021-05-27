import java.io.IOException;
import java.net.Socket;

public class ConnectorClient extends Connector {

	private String ip;
	private int port;

	public ConnectorClient(String ip, int port) {
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
