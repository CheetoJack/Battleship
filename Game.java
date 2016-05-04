import java.io.IOException;
import java.util.Scanner;

public class Game {
	public static final int CHAR_OFFSET = 65;
	private static final boolean PRINT_RECEIVED_MESSAGES = true;

	private Scanner input;
	private boolean isServer;
	private String ipAddress;
	private int port;

	private Player player;
	private IMessenger messenger;

	public Game(Scanner pInput, boolean isServer) {
		input = pInput;
		player = new Player(input, "Player 1");
		this.isServer = isServer;

		try {
			port = getPort();
			if (this.isServer) {
				messenger = new ServerMessenger(port);
			} else {
				ipAddress = getIp();
				messenger = new ClientMessenger(ipAddress, port);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		player.placeShips();
	}

	public void mainGameLoop() {
		if (isServer) {
			mainServerGameLoop();
		} else {
			mainClientGameLoop();
		}
	}

	public void closeNetworkConnection() {
		messenger.close();
	}

	private void mainServerGameLoop() {
		boolean gameContinues = true;
		String networkInput = "";
		String userInput = "";
		FiringResult latestResult;

		ready();
		System.out.println("Waiting for other player...");
		waitForReady();

		while (gameContinues) {
			player.printBoardStates();

			// Send Move
			takeShot();

			// Receive response to move
			networkInput = messenger.receiveMessage();
			latestResult = processReceivedResponse(networkInput);
			player.printBoardStates();

			if (latestResult == FiringResult.GameOver) {
				gameContinues = false;
				System.out.println("You win!");
				break;
			}

			// Receive move
			networkInput = messenger.receiveMessage();

			// Send response to move
			latestResult = processReceivedMove(networkInput);
			if (latestResult == FiringResult.GameOver) {
				gameContinues = false;
				System.out.println("You lost");
				break;
			}
		}
	}

	private void mainClientGameLoop() {
		boolean gameContinues = true;
		String networkInput = "";
		String userInput = "";
		FiringResult latestResult;

		System.out.println("Waiting for other player...");
		waitForReady();
		ready();

		while (gameContinues) {
			// Receive move
			networkInput = messenger.receiveMessage();

			// Send response to move
			latestResult = processReceivedMove(networkInput);
			if (latestResult == FiringResult.GameOver) {
				gameContinues = false;
				System.out.println("You lost");
				break;
			}

			player.printBoardStates();

			// Send a move
			takeShot();

			// Receive response to move
			networkInput = messenger.receiveMessage();
			latestResult = processReceivedResponse(networkInput);
			player.printBoardStates();

			if (latestResult == FiringResult.GameOver) {
				gameContinues = false;
				System.out.println("You win!");
				break;
			}
		}
	}

	private FiringResult processReceivedMove(String message) {
		if (PRINT_RECEIVED_MESSAGES) {
			System.out.println(message);
		}

		String[] tokens = message.split(" ");
		char column = tokens[1].charAt(0);
		int row = Integer.parseInt(tokens[2]);

		FiringResponse response = player.checkForDamage(row - 1, (int) (column - CHAR_OFFSET));
		FiringResult result = response.getResult();
		switch (result) {
		case Miss:
			miss();
			break;
		case Hit:
			hit();
			break;
		case Sunk:
			hit(response.getContent());
			break;
		case GameOver:
			gameOver();
			break;
		}
		return result;
	}

	private FiringResult processReceivedResponse(String message) {
		FiringResult result = FiringResult.Miss;
		if (PRINT_RECEIVED_MESSAGES) {
			System.out.println(message);
		}

		if (message.startsWith("HIT")) {
			player.processEnemyResponse(FiringResult.Hit);
			result = FiringResult.Hit;
		} else if (message.equals("MISS")) {
			player.processEnemyResponse(FiringResult.Miss);
			result = FiringResult.Miss;
		} else if (message.startsWith("GAME OVER")) {
			result = FiringResult.GameOver;
		}

		return result;
	}

	private String getIp() {
		System.out.println("\nEnter ip:\t");
		return input.nextLine();
	}

	private int getPort() {
		System.out.println("Enter port:\t");
		int port = Integer.parseInt(input.nextLine());
		return port;
	}

	private void waitForReady() {
		while (!messenger.receiveMessage().equals("READY")) {

		}
	}

	private void ready() {
		messenger.sendMessage("READY");
	}

	private void move(char column, int row) {
		String message = "MOVE " + column + " " + row;
		messenger.sendMessage(message.toUpperCase());
	}

	private void miss() {
		messenger.sendMessage("MISS");
	}

	private void hit() {
		messenger.sendMessage("HIT");
	}

	private void hit(LocationContentTypes ship) {
		String message = "HIT, YOU SUNK MY ";
		message += Player.getLocationContentTypeString(ship).toUpperCase();
		messenger.sendMessage(message);
	}

	private void gameOver() {
		String message = "GAME OVER. YOU WIN";
		messenger.sendMessage(message);
	}

	private void takeShot() {
		boolean completed = false;

		while (!completed) {
			try {
				System.out.println("Choose column and row to fire at (a-j 1-10): ");
				String line = input.nextLine();
				String[] tokens = line.split(" ");
				char column = Character.toUpperCase(tokens[0].charAt(0));
				int row = Integer.parseInt(tokens[1]);

				player.setLastFiredPosition(row, column);

				move(column, row);

				completed = true;
			} catch (Exception e) {
				completed = false;
			}
		}

	}

}
