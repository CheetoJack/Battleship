import java.net.*;
import java.io.*;

public class ServerMessenger implements IMessenger {
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private BufferedReader in;
	private PrintWriter out;
	boolean ready = false;

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
		out.println(message);
	}

	public String receiveMessage() {
		if (ready) {
			String input = "";
			try {
				input = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return input;
		} else {
			return "";
		}
	}

	public void closeServer() {
		
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
}
