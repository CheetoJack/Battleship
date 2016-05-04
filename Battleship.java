import java.util.Scanner;

public class Battleship {
	private static Scanner input;

	public static void main(String[] arghs) {
		int timeout = 0;

		for (int i = 0; i < arghs.length; i++) {
			if (arghs[i].equals("-t")) {
				timeout = 10;
				if (i + 1 < arghs.length) {
					try {
						timeout = Integer.parseInt(arghs[i + 1]);
					} catch (NumberFormatException ex) {
						// Do nothing, argument was not a number, and thus does
						// not need processing
					}
				}

			}
		}

		input = new Scanner(System.in);
		showTitleScreen();

		Game game;
		// If true, this is the server
		if (isServer()) {
			game = new Game(input, true);
		} else {
			// This is the client
			game = new Game(input, false);
		}

		if (game.connectGames(timeout)) {
			game.placeShips();
			game.mainGameLoop();
		} else {
			System.out.println("Something went wrong while trying to connect the server and client.");
		}

		// Clean up resources
		game.closeNetworkConnection();
		input.close();

		System.out.println("Shutting down...");
	}

	private static void showTitleScreen() {
		System.out.println(new String(new char[79]).replace("\0", "-"));
		System.out.println("Welcome to");
		System.out.println("__________         __    __  .__                .__    .__        ");
		System.out.println("\\______   \\_____ _/  |__/  |_|  |   ____   _____|  |__ |__|_____  ");
		System.out.println(" |    |  _/\\__  \\\\   __\\   __\\  | _/ __ \\ /  ___/  |  \\|  \\____ \\");
		System.out.println(" |    |   \\ / __ \\|  |  |  | |  |_\\  ___/ \\___ \\|   Y  \\  |  |_> >");
		System.out.println(" |______  /(____  /__|  |__| |____/\\___  >____  >___|  /__|   __/ ");
		System.out.println("        \\/      \\/                     \\/     \\/     \\/   |__| ");

	}

	public static boolean randomizeSendingMessage() {
		int random = (int) (Math.random() * 10);

		return random < 9;
	}

	private static boolean isServer() {
		System.out.println("Is this the server or client? ");
		String response = input.nextLine();
		if (response.equalsIgnoreCase("server") || response.equalsIgnoreCase("s")) {
			return true;
		} else {
			return false;
		}
	}

}
