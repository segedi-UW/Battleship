import java.util.Random;
import java.net.ServerSocket;
import java.io.IOException;
import java.lang.Thread;

/**
 * An object that handles a ChatServer with a ChatClient
 */
public class Server extends Connector implements Runnable {

	private ServerSocket serverSocket;
	private int port;

	private boolean isConnected;

	private final int UNRESERVED_PORTS = 1024; // The start of the unreserved port numbers

	/**
	 * Creates a ConnectorServer and reserves an unreserved port.
	 */
	public Server() {
		isConnected = false;
		reservePort();
	}

	/**
	 * Finds and reserves a port.
	 */
	private void reservePort() {
		do {
			tryRandomPort();
		} while (serverSocket == null || serverSocket.getLocalPort() == -1);
	}

	/**
	 * Attempts to reserve a random port.
	 */
	private void tryRandomPort() {
			try {
				Random rand = new Random();
				port = rand.nextInt(10000) + UNRESERVED_PORTS;
				serverSocket = new ServerSocket(port);
			} catch (IOException e) {
				System.out.println("Port was reserved. Repicking.");
			}
	}

	@Override
	public void connect() {
		try {
			connect(serverSocket.accept());
			isConnected = true;
		} catch (IOException e) {
			System.out.println("Failed to connect: " + e.getMessage());
		}	
	}

	/**
	 * Returns the reserved port used.
	 * 
	 * @return the used port.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Returns the host ip address
	 * 
	 * @return the used ip address.
	 */
	public String getIP() {
		return serverSocket.getInetAddress().getCanonicalHostName();
	}

	/**
	 * Returns if this ConnectorServer is connected.
	 * 
	 * @return true if connected, false otherwise.
	 */
	public boolean isConnected() {
		return isConnected;
	}

	/**
	 * Starts blocking accepting in a daemon thread.
	 */
	public void startInThread() {
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public void run() {
		connect();
	}

	@Override
	public void close() {
		isConnected = false;
		super.close();
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.out.println("Failed to close server: " + e.getMessage());
		}
	}
}
