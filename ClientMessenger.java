import java.io.*;
import java.net.*;

public class ClientMessenger implements IMessenger {
	private Socket echoSocket;
	private PrintWriter outSend;
	private BufferedReader inSend;
	private boolean ready = false;
	private String lastMessage = "";

	public ClientMessenger(String hostName, int portNumber) throws IOException {

		try {
			echoSocket = new Socket(hostName, portNumber);
			outSend = new PrintWriter(echoSocket.getOutputStream(), true);
			inSend = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
			ready = true;
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + hostName);
			System.exit(1);
		}
	}

	public void sendMessage(String message) {
		if (ready && Battleship.randomizeSendingMessage()) {
			lastMessage = message;
			outSend.println(message);
		}
	}

	public String receiveMessage() {
		if (ready) {
			String input = "";
			try {
				input = inSend.readLine();
				if (Game.PRINT_RECEIVED_MESSAGES) {
					System.out.println(input);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return input;
		} else {
			return "";
		}
	}

	public void setTimeout(int timeout) {
		try {
			echoSocket.setSoTimeout(timeout * Game.MILLIS_IN_SECOND);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		try {
			ready = false;
			echoSocket.close();
			outSend.close();
			inSend.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void resendLastMessage() {
		sendMessage(lastMessage);
	}
}