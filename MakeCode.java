// From the UW
//
// MakeCode prompts the user for an input file name and a code file name.  It
// examines the input file for the frequencies of characters and then uses a
// Huffman tree to generate codes to use for each character so as to compress
// the original file.  This program does not the actual encoding or decoding.
package hw7;
import java.io.*;
import java.util.*;

public class MakeCode {
	public static final int CHAR_MAX = 256; // max char value to be encoded

	public static void main(String[] args) /* throws IOException */{
		System.out.println("This program makes a Huffman code for a file.");
		System.out.println();

		// get file names from user
		Scanner console = new Scanner(System.in);
		System.out.print("input file name? ");
		String inFile = console.nextLine();
		System.out.print("code file name? ");
		String codeFile = console.nextLine();
		console.close();

		// open input file and count character frequencies
		try {
			FileInputStream input = new FileInputStream(inFile);
			int[] count = new int[CHAR_MAX];
			for (;;) {
				int n = input.read();
				if (n == -1)
					break;
				count[n]++;
			}
			input.close();

			// build tree, open output file, print codes
			HuffmanTree t = new HuffmanTree(count);
			PrintStream output = new PrintStream(new File(codeFile));
			t.write(output);
			output.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
