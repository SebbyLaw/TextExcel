// Sebastian Law
// 2020.2.24

package textExcel;

// Update this file with your own code.

public class Spreadsheet implements Grid {

	@Override
	public String processCommand(String command) {
		return "";
	}

	@Override
	public int getRows() {
		return 20;
	}

	@Override
	public int getCols() {
		return 12;
	}

	@Override
	public Cell getCell(Location loc) {
		return null;
	}

	@Override
	public String getGridText() {
		return "";
	}
}
