import java.io.IOException;
import java.util.Scanner;

public class Game {
	public static final int CHAR_OFFSET = 65;
	public static final boolean PRINT_RECEIVED_MESSAGES = true;
	public static final int MILLIS_IN_SECOND = 1000;

	private Scanner input;
	private boolean isServer;
	private String ipAddress;
	private int port;

	private Player player;
	private IMessenger messenger;
	private GameState state;

	public Game(Scanner pInput, boolean isServer) {
		input = pInput;
		player = new Player(input, "Player 1");
		this.isServer = isServer;
		state = GameState.Setup;
	}

	// Connects the Server and Client together
	public boolean connectGames(int timeout) {
		boolean success = true;
		try {
			port = getPort();
			if (this.isServer) {
				messenger = new ServerMessenger(port);
			} else {
				ipAddress = getIp();
				messenger = new ClientMessenger(ipAddress, port);
			}

			messenger.setTimeout(timeout);
		} catch (IOException e) {
			e.printStackTrace();
			success = false;
		}

		return success;
	}

	// Triggers the dialogs for placing ships
	public void placeShips() {
		player.placeShips();
	}

	// Starts the main game, shooting the battleship
	public void mainGameLoop() {
		boolean gameContinues = true;
		String networkInput = "";
		FiringResult latestResult;

		if (isServer) {
			ready();
			System.out.println("Waiting for other player...");
			waitForReady();
			state = GameState.SendingMove;
		} else {
			System.out.println("Waiting for other player...");
			waitForReady();
			ready();
			state = GameState.ReceivingEnemyMove;
		}

		/*
		 * This loop works on the idea of the game progressing in "States" based
		 * on if the program instance is a server or client, it chooses the
		 * starting state. From there, the program works linearly, progressing
		 * to each state in the order here.
		 * 
		 * The only way to leave the loop is by reaching a GameOver.
		 * 
		 * Each block that could send or receive a timeout deals with the
		 * consequences of the timeout. It will resend the message as needed
		 */
		while (gameContinues) {
			switch (state) {
			case GameOver:
				gameContinues = false;
				break;
			case SendingMove:
				player.printBoardStates();
				takeShot();
				state = GameState.ReceivingMoveResponse;
				break;
			case ReceivingMoveResponse:
				// Receive response to move
				networkInput = messenger.receiveMessage();
				if (isTimeout(networkInput)) {
					messenger.resendLastMessage();
					state = GameState.ReceivingMoveResponse;
				} else if (networkInput.equals("")) {
					timeout();
					state = GameState.ReceivingMoveResponse;
				} else {
					latestResult = processReceivedResponse(networkInput);
					player.printBoardStates();

					if (latestResult == FiringResult.GameOver) {
						state = GameState.GameOver;
						System.out.println("You win!");
						break;
					} else {
						state = GameState.ReceivingEnemyMove;
					}
				}

				break;
			case ReceivingEnemyMove:
				// Receive move
				networkInput = messenger.receiveMessage();
				if (isTimeout(networkInput)) {
					messenger.resendLastMessage();
					state = GameState.ReceivingEnemyMove;
				} else if (networkInput.equals("")) {
					timeout();
					state = GameState.ReceivingEnemyMove;
				} else {
					state = GameState.SendingEnemyMoveResponse;
				}
				break;
			case SendingEnemyMoveResponse:
				// Send response to move
				latestResult = processReceivedMove(networkInput);
				if (latestResult == FiringResult.GameOver) {
					state = GameState.GameOver;
					System.out.println("You lost");
					break;
				} else {
					state = GameState.SendingMove;
				}
				break;
			default:
				gameContinues = false;
				break;
			}
		}
	}

	// Disconnect server and client
	public void closeNetworkConnection() {
		messenger.close();
	}

	private boolean isTimeout(String message) {
		if (message.startsWith("TIMEOUT")) {
			return true;
		} else {
			return false;
		}
	}

	// Processes the move of the enemy
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
		default:
			break;
		}
		return result;
	}

	// Processes enemy's response to current player's move
	private FiringResult processReceivedResponse(String message) {
		FiringResult result = FiringResult.None;

		if (message.startsWith("TIMEOUT")) {
			result = FiringResult.None;
		} else if (message.startsWith("HIT")) {
			player.processEnemyResponse(FiringResult.Hit);
			result = FiringResult.Hit;

			if (message.contains("BATTLESHIP")) {
				result = FiringResult.GameOver;
			}
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

	private void timeout() {
		String message = "TIMEOUT - EXPECTING A MOVE";
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
