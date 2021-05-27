import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.lang.Thread;
public class TestSuite {

	@Test
	public void testConnection() {
		ConnectorServer server = new ConnectorServer();
		ConnectorClient client = new ConnectorClient(server.getIP(), server.getPort());
		connect(server, client);
		assertEquals(true, server.isConnected());
	}

	@Test
	public void testWriteRead() {
		ConnectorServer server = new ConnectorServer();
		ConnectorClient client = new ConnectorClient(server.getIP(), server.getPort());
		connect(server, client);
		final String test = "TestABC123";
		server.write(test);
		pause(250); // Need to wait for thread
	}

	@Test
	public void testTurnStart() {
		fail("Not Implemented");
	}

	@Test
	public void testChat() {
		fail("Not Implemented");
	}

	@Test
	public void testAttack() {
		fail("Not Implemented");
	}

	@Test
	public void testAttacked() {
		fail("Not Implemented");
	}

	@Test
	public void testSpecialAttack() {
		fail("Not Implemented");
	}

	@Test
	public void testEnemyAttack() {
		fail("Not Implemented");
	}

	private synchronized void pause(int ms) {
		try {
			wait(ms);
		} catch (InterruptedException e) {
			// Waiting complete - no action
		}
	}

	private void connect(ConnectorServer server, ConnectorClient client) {
		Client threadClient = new Client(client);
		Thread thread = new Thread(threadClient);
		thread.start();
		server.connect();
	}

	private class Client implements Runnable {
		
		ConnectorClient client;

		public Client(ConnectorClient client) {
			this.client = client;
		}

		@Override
		public void run() {
			client.connect();
		}
	}
}
