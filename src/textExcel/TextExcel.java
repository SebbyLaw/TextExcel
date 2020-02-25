// Sebastian Law
// 2020.2.24

package textExcel;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class TextExcel {
	private static final Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		Spreadsheet sheet = new Spreadsheet();
		
		String input = "";
		while (!input.equalsIgnoreCase("quit")) {
			sheet.processCommand(input);
			input = scanner.nextLine();
		}
		
		scanner.close();
	}
}
