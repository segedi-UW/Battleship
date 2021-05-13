import java.net.Socket;
import java.net.InetAddress;
import java.net.SocketException;
import java.io.IOException;
import java.lang.Thread;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class ChatSocket {

	private Socket socket;
	private Writer writer;
	private Reader reader;
	private LinkedBlockingQueue<String> queue;

	private class Reader implements Runnable {

		private BufferedReader reader;
		private boolean running;
		private ChatSocket chatSocket;

		public Reader(ChatSocket chatSocket) {
			this.chatSocket = chatSocket;
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

		private void readInput() {
			try {
				String input = this.reader.readLine();
				if (input == null) {
					running = false;
					return;
				}
				chatSocket.addMessage(input);
			} catch (IOException e) {
				running = false;
				System.out.println("Error reading: " + e.getMessage());
			}
		}

		public void close() {
			try {
				this.reader.close();
			} catch (IOException e) {
				System.out.println("Error closing Reader: " + e.getMessage());
			}
		}

		public boolean isRunning() {
			return running;
		}
	}

	private class Writer {

		private PrintWriter writer;
		private boolean open;

		public Writer() {
			final boolean AUTO_FLUSH = true;
			try {
				writer = new PrintWriter(socket.getOutputStream(), AUTO_FLUSH);
				open = true;
			} catch (IOException e) {
				System.out.println("Writer Error: " + e.getMessage());
			} 
		}

		public void write(String str) {
			if (open)
				writer.println(str);
			else
				throw new IllegalStateException("Cannot write to a closed stream");
		}

		public boolean isOpen() {
			return open;
		}

		public void close() {
			open = false;
			if (writer != null)
				writer.close();
		}
	}

	private void startDaemon(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.setDaemon(true);
		thread.start();
	}

	public ChatSocket() {
		queue = new LinkedBlockingQueue<>();
	}

	protected void connect(String ip, int port) throws IOException {
		connect(new Socket(InetAddress.getByName(ip), port));
	}

	protected void connect(Socket socket) {
		this.socket = socket;
		reader = new Reader(this);
		writer = new Writer();
		startDaemon(reader);
	}

	public void addMessage(String str) {
		queue.add(str);
	}

	public String getMessage() {
		try {
			return queue.take();
		} catch (InterruptedException e) {
			System.out.println("Error taking message: " + e.getMessage());
		}
		return null;
	}

	public boolean hasMessage() {
		return queue.size() > 0;
	}

	public void write(String str) {
		if (writer.isOpen()) {
			writer.write(str);
		} else {
			throw new IllegalStateException("Writer connection is closed.");
		}
	}

	public void close() {
		if (writer != null)
			writer.close();
		if (reader != null)
			reader.close();
		if (socket != null)
			closeSocket();
	}

	private void closeSocket() {
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("Error closing socket: " + e.getMessage());
		}
	}

	public abstract void connect();
}
