import java.io.IOException;

public interface IMessenger {
	public void sendMessage(String message);
	public String receiveMessage();
	public void close();
}
