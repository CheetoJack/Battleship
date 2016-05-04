//Used to differentiate the Server and Client
public interface IMessenger {
	public void sendMessage(String message);

	public void resendLastMessage();
	
	public String receiveMessage();
	
	public void setTimeout(int timeout);
	
	public void close();
}
