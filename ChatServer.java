import java.util.Random;
import java.net.ServerSocket;
import java.net.SocketException;
import java.io.IOException;
import java.lang.Thread;

/**
 * An object that handles a ChatServer with a ChatClient
 */
public class ChatServer extends ChatSocket implements Runnable {

	private ServerSocket serverSocket;
	private int port;

	private boolean isConnected;

	private final int UNRESERVED_PORTS = 1024; // The start of the unreserved port numbers

	public ChatServer() {
		isConnected = false;
		reservePort();
	}

	private void reservePort() {
		do {
			tryRandomPort();
		} while (serverSocket == null || serverSocket.getLocalPort() == -1);
	}

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

	public int getPort() {
		return port;
	}

	public String getIP() {
		return serverSocket.getInetAddress().getCanonicalHostName();
	}

	public boolean isConnected() {
		return isConnected;
	}

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
