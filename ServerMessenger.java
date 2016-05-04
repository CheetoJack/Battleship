import java.net.*;
import java.io.*;

public class ServerMessenger implements IMessenger {
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private BufferedReader in;
	private PrintWriter out;
	private String lastMessage = "";
	private boolean ready = false;

	public ServerMessenger(int portNumber) throws IOException {
		try {
			serverSocket = new ServerSocket(portNumber);
			clientSocket = serverSocket.accept();
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			ready = true;
		} catch (IOException e) {
			System.out.println(
					"Excpetion caught when trying to listen on port " + portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
		}
	}

	public void sendMessage(String message) {
		if (ready && Battleship.randomizeSendingMessage()) {
			lastMessage = message;
			out.println(message);
		}
	}

	public String receiveMessage() {
		if (ready) {
			String input = "";
			try {
				input = in.readLine();
				if (Game.PRINT_RECEIVED_MESSAGES) {
					System.out.println(input);
				}
			} catch (SocketException e) {
				System.out.println("No message received - TIMEOUT");
				input="";
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
			serverSocket.setSoTimeout(timeout * Game.MILLIS_IN_SECOND);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		try {
			ready = false;
			clientSocket.close();
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void resendLastMessage() {
		sendMessage(lastMessage);
	}
}
