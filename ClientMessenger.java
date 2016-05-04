import java.io.*;
import java.net.*;

public class ClientMessenger implements IMessenger{
	private Socket echoSocket;
	private PrintWriter outSend;
	private BufferedReader inSend;
	boolean ready = false;

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
		if (ready) {
			// POINT A

			// User Input is withing the userInput variable at this point
			// The below function is the variable that is sent to the server
			outSend.println(message);
		}
	}

	public String receiveMessage() {
		if (ready) {
			String input = "";
			try {
				input = inSend.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return input;
		} else {
			return "";
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
}