// From the UW
//
// Encode prompts the user for an input file name, a code file name and the
// name to use for the binary (encoded) output file.  It assumes that MakeCode
// has been run to generate a Huffman tree appropriate for encoding the input
// file.
package hw7;
import java.io.*;
import java.util.*;
public class Encode {
	public static final int CHAR_MAX = 256; // max char value to be encoded

	public static void main(String[] args) {
		System.out.println("This program encodes a file with a Huffman code.");
		System.out.println();

		Scanner console = new Scanner(System.in);
		System.out.print("input file name? ");
		String inFile = console.nextLine();
		System.out.print("code file name? ");
		String codeFile = console.nextLine();
		System.out.print("output file name? ");
		String outputFile = console.nextLine();
		console.close();

		// open code file and record codes
		try {
			String[] codes = new String[CHAR_MAX + 1];
			Scanner codeInput = new Scanner(new File(codeFile));
			while (codeInput.hasNextLine()) {
				int n = Integer.parseInt(codeInput.nextLine());
				codes[n] = codeInput.nextLine();
			}
			codeInput.close();

			// open source file, open output, encode
			FileInputStream input = new FileInputStream(inFile);
			BitOutputStream output = new BitOutputStream(outputFile);
			for (;;) {
				int n = input.read();
				if (n == -1)
					break;
				writeString(codes[n], output);
			}
			input.close();
			writeString(codes[CHAR_MAX], output);
			output.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public static void writeString(String s, BitOutputStream output) {
		for (int i = 0; i < s.length(); i++)
			output.writeBit(s.charAt(i) - '0');
	}
}
