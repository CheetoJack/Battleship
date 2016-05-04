
public class Tile {

	private LocationContentTypes content;

	public Tile() {
		content = LocationContentTypes.None;
	}

	public Tile(LocationContentTypes pContent) {
		content = pContent;
	}

	public LocationContentTypes getContent() {
		return content;
	}

	public void setContent(LocationContentTypes content) {
		this.content = content;
	}

	public String toString() {
		String output = "";

		switch (content) {
		case None:
			output = " ";
			break;
		case Hit:
			output = "H";
			break;
		case Miss:
			output = "M";
			break;
		case Battleship:
			output = "B";
			break;
		case Carrier:
			output = "C";
			break;
		case Destroyer:
			output = "D";
			break;
		case PatrolBoat:
			output = "P";
			break;
		case Submarine:
			output = "S";
			break;
		default:
			output = "X";
		}

		return output;
	}
}
