
public class Board {

	private static final int BOARD_SIZE = 10;
	private static final String DASHES = new String(new char[88]).replace("\0", "_");
	private Tile[][] board;

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

	public void setTileContents(int row, int column, LocationContentTypes newContents) {
		board[row][column].setContent(newContents);
	}

	public FiringResult checkIfHit(int row, int column) {
		// This is bad code but it works better and is faster to write than an
		// if statement with a bunch of ORs
		if (isShipType(board[row][column].getContent())) {
			return hitShip(row, column, board[row][column].getContent());
		} else {
			// If it is not a ship, then there was nothing hit
			return FiringResult.Miss;
		}
	}

	private FiringResult hitShip(int row, int column, LocationContentTypes ship) {
		board[row][column].setContent(LocationContentTypes.Hit);

		boolean shipSunk = true;
		int shipSpacesAlive = 0;
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
				if (isShipType(board[x][y].getContent())) {
					shipSpacesAlive++;
				}

				if (board[x][y].getContent() == ship) {
					shipSunk = false;
				}
			}
		}

		System.out.println("Ship spaces alive: " + shipSpacesAlive);
		if (shipSpacesAlive == 0) {
			return FiringResult.GameOver;
		}

		if (shipSunk) {
			return FiringResult.Sunk;
		} else {
			return FiringResult.Hit;
		}
	}

	public boolean placeShip(int row, int column, ShipOrientation direction, LocationContentTypes ship) {
		if (direction == ShipOrientation.North || direction == ShipOrientation.South) {
			return placeShipVertically(row, column, direction, ship);
		} else if (direction == ShipOrientation.East || direction == ShipOrientation.West) {
			return placeShipHorizontally(row, column, direction, ship);
		} else {
			return false;
		}
	}

	private boolean isShipType(LocationContentTypes content) {
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

	private boolean placeShipHorizontally(int row, int column, ShipOrientation direction, LocationContentTypes ship) {
		int shipLength = getShipLength(ship);
		int change = 0;

		if (direction == ShipOrientation.East) {
			change = 1;
		} else {
			change = -1;
		}

		int endingColumn = column + (shipLength * change);
		if (endingColumn < 0 || endingColumn > 9) {
			return false;
		}

		for (int i = column; i != endingColumn; i += change) {
			if (board[row][i].getContent() != LocationContentTypes.None) {
				return false;
			}
		}

		for (int i = column; i != endingColumn; i += change) {
			board[row][i].setContent(ship);
		}

		return true;
	}

	private boolean placeShipVertically(int row, int column, ShipOrientation direction, LocationContentTypes ship) {
		int shipLength = getShipLength(ship);
		int change = 0;

		if (direction == ShipOrientation.South) {
			change = 1;
		} else {
			change = -1;
		}

		int endingRow = row + (shipLength * change);
		if (endingRow < 0 || endingRow > 9) {
			return false;
		}

		for (int i = row; i != endingRow; i += change) {
			if (board[i][column].getContent() != LocationContentTypes.None) {
				return false;
			}
		}

		for (int i = row; i != endingRow; i += change) {
			board[i][column].setContent(ship);
		}

		return true;
	}

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
		 default:
		 	return 0;
		 }

		//return 1;
	}

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
