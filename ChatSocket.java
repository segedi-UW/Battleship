import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;
import java.lang.Thread;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 * Handles socket input output between two users. Is multi-threaded to allow asynchronous
 * reading and writing. The reading thread notifies all MessageListeners when a new message
 * is read in and gives them the message.
 * 
 * @author youngAgFox
 *
 */
public abstract class Connector {

	private Socket socket;
	private Writer writer;
	private Reader reader;
	private LinkedList<MessageListener> listeners;

	/**
	 * Reader thread that handles the listener notification and reading in from
	 * the socket
	 * 
	 * @author youngAgFox
	 *
	 */
	private class Reader implements Runnable {

		private BufferedReader reader;
		private boolean running;
		private Connector connector;

		/**
		 * Creates a Reader with the passed connector.
		 * 
		 * @param connector the Connector object handling input output
		 */
		public Reader(Connector connector) {
			this.connector = connector;
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (IOException e) {
				System.out.println("Failed to initialize Reader: " + e.getMessage());
			}
		}

		@Override
		public void run() {
			running = true;
			while (running)
				readInput();
		}

		/**
		 * Reads in the input and notifies the Gui if the socket is closed (returns null)
		 */
		private void readInput() {
			try {
				String input = this.reader.readLine();
				if (input == null) {
					running = false;
					return;
				}
				connector.readMessage(input);
			} catch (IOException e) {
				running = false;
				String error = "Error reading: " + e.getMessage();
				System.out.println(error);
				Gui.exitToHome(error);
			}
		}

		/**
		 * Closes this Reader.
		 */
		public void close() {
			try {
				this.reader.close();
			} catch (IOException e) {
				System.out.println("Error closing Reader: " + e.getMessage());
			}
		}

	}

	/**
	 * Object that writes to the Connector.
	 * 
	 * @author youngAgFox
	 *
	 */
	private class Writer {

		private PrintWriter writer;
		private boolean open;

		/**
		 * Creates a Writer with auto flushing, setting open to true.
		 */
		public Writer() {
			final boolean AUTO_FLUSH = true;
			try {
				writer = new PrintWriter(socket.getOutputStream(), AUTO_FLUSH);
				open = true;
			} catch (IOException e) {
				System.out.println("Writer Error: " + e.getMessage());
			} 
		}

		/**
		 * Writes a string to the Connector. 
		 * 
		 * @param str the string to write
		 */
		public void write(String str) {
			if (open)
				writer.println(str);
			else
				throw new IllegalStateException("Cannot write to a closed stream");
		}

		/**
		 * Returns if this Writer is still functional (if socket is open).
		 * 
		 * @return true if the Socket can be written to, false otherwise.
		 */
		public boolean isOpen() {
			return open;
		}

		/**
		 * Closes this Writer.
		 */
		public void close() {
			open = false;
			if (writer != null)
				writer.close();
		}
	}

	/**
	 * Starts a daemon thread.
	 * 
	 * @param runnable the thread to start
	 */
	private void startDaemon(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Creates a new Connector with a blank listeners list.
	 */
	public Connector() {
		listeners = new LinkedList<>();
	}

	/**
	 * Connects the Connector to the other end of the connection using the given ip and port.
	 * Used by Client end subclasses.
	 * 
	 * @param ip the ip address
	 * @param port the port
	 * @throws IOException
	 */
	protected void connect(String ip, int port) throws IOException {
		connect(new Socket(InetAddress.getByName(ip), port));
	}

	/**
	 * Connects the Connector to the other end of the connection using the given socket.
	 * Used by Server side subclasses.
	 * 
	 * @param socket the Socket to connect to.
	 */
	protected void connect(Socket socket) {
		this.socket = socket;
		reader = new Reader(this);
		writer = new Writer();
		startDaemon(reader);
	}

	/**
	 * Notifies all listeners of the string read in.
	 * 
	 * @param str the string read in
	 */
	private void readMessage(String str) {
		for (MessageListener listener : listeners) {
			listener.messageRecieved(str);
		}
	}
	
	/**
	 * Adds a listener
	 * 
	 * @param listener the listener to add.
	 */
	public void addListener(MessageListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes a listener
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(MessageListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Removes all listeners.
	 */
	public void clearListeners() {
		listeners.clear();
	}
	
	/**
	 * Writes a string using the Writer object.
	 * 
	 * @param str the string to write
	 */
	public void write(String str) {
		if (writer.isOpen()) {
			writer.write(str);
		} else {
			throw new IllegalStateException("Writer connection is closed.");
		}
	}

	/**
	 * Closes this Connector.
	 */
	public void close() {
		if (writer != null)
			writer.close();
		if (reader != null)
			reader.close();
		if (socket != null)
			closeSocket();
	}

	/**
	 * Closes the Socket objects.
	 */
	private void closeSocket() {
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("Error closing socket: " + e.getMessage());
		}
	}

	/**
	 * Should utilize one of the connect protected methods to connect this 
	 * Connector to both ends.
	 */
	public abstract void connect();
}
