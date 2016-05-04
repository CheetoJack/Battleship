
public class Board {

	private static final int BOARD_SIZE = 10;
	//private static final String DASHES = new String(new char[88]).replace("\0", "_");
	private Tile[][] board; //This actually stores the values of the ship

	public Board() {
		board = new Tile[BOARD_SIZE][BOARD_SIZE];
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
				board[x][y] = new Tile();
			}
		}
	}

	public Tile getTile(int row, char column) {
		int columnIndex = ((int) column) - 65;
		return getTile(row, columnIndex);
	}

	public Tile getTile(int row, int column) {
		return board[row][column];
	}

	//Sets the tile specified by row, column to be newContents
	public void setTileContents(int row, int column, LocationContentTypes newContents) {
		board[row][column].setContent(newContents);
	}

	//Determines if a shot fired at (row, column) hit or not, and processes the result
	//Returns the FiringResult of the shot
	public FiringResult checkIfHit(int row, int column) {
		if (isShipType(board[row][column].getContent())) {
			return hitShip(row, column, board[row][column].getContent());
		} else {
			// If it is not a ship, then there was nothing hit
			return FiringResult.Miss;
		}
	}

	//Processes result of a ship being hit
	//Requires position of hit, current contents of location
	//Returns if a ship was hit, sunk, or triggered a game over
	private FiringResult hitShip(int row, int column, LocationContentTypes ship) {
		board[row][column].setContent(LocationContentTypes.Hit);

		boolean shipSunk = true;
		int battleshipSpacesAlive = 0;
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
				if (board[x][y].getContent()==LocationContentTypes.Battleship) {
					battleshipSpacesAlive++;
				}

				if (board[x][y].getContent() == ship) {
					shipSunk = false;
				}
			}
		}

		if (battleshipSpacesAlive == 0) {
			return FiringResult.GameOver;
		}

		if (shipSunk) {
			return FiringResult.Sunk;
		} else {
			return FiringResult.Hit;
		}
	}

	//Places the specified ship after validating that the given input is valid for the location
	//Requires the row, column, direction the ship should be placed starting from that point, and the ship to place
	//Returns true if it succeeded, false if it did not
	public boolean placeShip(int row, int column, ShipOrientation direction, LocationContentTypes ship) {
		if (direction == ShipOrientation.North || direction == ShipOrientation.South) {
			return placeShipVertically(row, column, direction, ship);
		} else if (direction == ShipOrientation.East || direction == ShipOrientation.West) {
			return placeShipHorizontally(row, column, direction, ship);
		} else {
			return false;
		}
	}

	//Returns true if content is a Ship
	private boolean isShipType(LocationContentTypes content) {
		//This code isn't great, but it works a lot better than a series of ORs in an if statement
		switch (content) {
		case Battleship:
		case Carrier:
		case Destroyer:
		case Submarine:
		case PatrolBoat:
			return true;
		default:
			// If it is not a ship, then there was nothing hit
			return false;
		}
	}

	//Places a ship on the board horizontally
	//Returns true if successful
	private boolean placeShipHorizontally(int row, int column, ShipOrientation direction, LocationContentTypes ship) {
		int shipLength = getShipLength(ship);
		int change = 0;

		//Determine if the column should increase or decrease based on the given Direction		
		if (direction == ShipOrientation.East) {
			change = 1;
		} else {
			change = -1;
		}

		//Determines where the last space of the ship should be placed
		int endingColumn = column + (shipLength * change);
		if (endingColumn < 0 || endingColumn > 9) {
			return false;
		}

		//Checks if the path for the ship to be placed is clear
		for (int i = column; i != endingColumn; i += change) {
			if (board[row][i].getContent() != LocationContentTypes.None) {
				return false;
			}
		}

		//Actually places the ship
		for (int i = column; i != endingColumn; i += change) {
			board[row][i].setContent(ship);
		}

		return true;
	}

	//Places a ship on the board vertically
	//Returns true if successful
	private boolean placeShipVertically(int row, int column, ShipOrientation direction, LocationContentTypes ship) {
		int shipLength = getShipLength(ship);
		int change = 0;

		//Determine if the row should increase or decrease based on the given Direction
		if (direction == ShipOrientation.South) {
			change = 1;
		} else {
			change = -1;
		}

		//Determines where the last space of the ship should be placed
		int endingRow = row + (shipLength * change);
		if (endingRow < 0 || endingRow > 9) {
			return false;
		}

		//Checks if the path for the ship to be placed is clear
		for (int i = row; i != endingRow; i += change) {
			if (board[i][column].getContent() != LocationContentTypes.None) {
				return false;
			}
		}

		//Actually places the ship
		for (int i = row; i != endingRow; i += change) {
			board[i][column].setContent(ship);
		}

		return true;
	}

	//Determines the length of each ship type
	public int getShipLength(LocationContentTypes ship) {
		switch (ship) {
		case Battleship:
			return 6;
		case Carrier:
			return 5;
		case Destroyer:
			return 4;
		case Submarine:
			return 3;
		case PatrolBoat:
			return 2;
		default: //Anything else is not a ship, and thus gets a length of zero
			return 0;
		}
	}

	//Returns a string containing the board state
	public String toString() {
		String output = "|\t|";

		char current = 'A';
		for (int x = 0; x < board.length; x++) {
			output += ((char) (current + x)) + "\t|";
		}
		output += "\n";
		// output += "\n" + DASHES + "\n";

		for (int x = 0; x < board.length; x++) {
			output += "|" + (x + 1) + "\t|";
			for (int y = 0; y < board[x].length; y++) {
				output += board[x][y];
				output += "\t|";
			}
			output += "\n";
			// output += "\n" + DASHES + "\n";;
		}

		return output;
	}

}
