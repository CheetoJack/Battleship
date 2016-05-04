import java.util.Scanner;

public class Player {
	private Board myBoard; //Current player's board
	private Board theirBoard; //Enemy's board
	private Scanner input;
	private String name;
	private char lastColumnFiredAt;
	private int lastRowFiredAt;

	public Player(Scanner pInput, String name) {
		input = pInput;
		myBoard = new Board();
		theirBoard = new Board();
		this.name = name;
	}

	//Returns a string that corresponds to the enumerated type
	public static String getLocationContentTypeString(LocationContentTypes type) {
		switch (type) {
		case None:
			return "None";
		case Hit:
			return "Hit";
		case Miss:
			return "Miss";
		case Battleship:
			return "Battleship";
		case Destroyer:
			return "Destroyer";
		case Carrier:
			return "Carrier";
		case Submarine:
			return "Submarine";
		case PatrolBoat:
			return "Patrol Boat";
		default:
			return "NULL";
		}
	}

	//Processes the response of an enemy's move to the player's move
	public void processEnemyResponse(FiringResult result) {
		switch (result) {
		case Hit:
			theirBoard.setTileContents(lastRowFiredAt - 1, (int) (lastColumnFiredAt - Game.CHAR_OFFSET),
					LocationContentTypes.Hit);
			break;
		case Miss:
			theirBoard.setTileContents(lastRowFiredAt - 1, (int) (lastColumnFiredAt - Game.CHAR_OFFSET),
					LocationContentTypes.Miss);
			break;
		default:
			break;
		}
	}

	//Processes an enemy's move
	//Returns a response to the enemy's move
	public FiringResponse checkForDamage(int row, int column) {
		LocationContentTypes content = myBoard.getTile(row, column).getContent();
		FiringResult result = myBoard.checkIfHit(row, column);

		return new FiringResponse(result, content);
	}

	//Places each ship on the board with User Input
	public void placeShips() {
		while (!placeShip(LocationContentTypes.Battleship)) {
			System.out.println("Something went wrong... Try again");
		}
		while (!placeShip(LocationContentTypes.Carrier)) {
			System.out.println("Something went wrong... Try again");
		}
		while (!placeShip(LocationContentTypes.Destroyer)) {
			System.out.println("Something went wrong... Try again");
		}
		while (!placeShip(LocationContentTypes.Submarine)) {
			System.out.println("Something went wrong... Try again");
		}
		while (!placeShip(LocationContentTypes.PatrolBoat)) {
			System.out.println("Something went wrong... Try again");
		}
	}

	public String getName() {
		return name;
	}

	//Prints the boards of both the player and enemy
	public void printBoardStates() {
		System.out.println("Enemy's Board");
		System.out.println(theirBoard + "\n");
		System.out.println("Your Board");
		System.out.println(myBoard);
	}

	//Stores where the player last fired
	public void setLastFiredPosition(int row, char column) {
		lastColumnFiredAt = column;
		lastRowFiredAt = row;
	}
	
	//Tries to place a ship with user input
	//Takes a ship type to attempt to place
	//Returns true if it succeeded
	private boolean placeShip(LocationContentTypes ship) {
		System.out.println(myBoard);

		System.out.print("Where do you want to place your " + getLocationContentTypeString(ship) + "? (column row) ");
		String line = input.nextLine();
		String[] tokens = line.split(" ");
		int column = (int) (Character.toUpperCase(tokens[0].charAt(0)) - Game.CHAR_OFFSET);
		int row = Integer.parseInt(tokens[1]) - 1;

		System.out.print("Which direction should it go? (N,E,S,W) ");
		ShipOrientation orientation = getOrientation(input.nextLine());

		return myBoard.placeShip(row, column, orientation, ship);
	}

	//Transforms user inputted string into an Enumeration
	private ShipOrientation getOrientation(String input) {
		switch (input.toUpperCase().charAt(0)) {
		case 'N':
			return ShipOrientation.North;
		case 'E':
			return ShipOrientation.East;
		case 'S':
			return ShipOrientation.South;
		case 'W':
			return ShipOrientation.West;
		}

		return ShipOrientation.None;
	}

}
