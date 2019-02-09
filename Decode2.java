// Stuart Reges
// 5/25/05
//
// Decode2 prompts the user for the name of a binary (encoded) input file and a
// the name to use for the output file.  It reproduces the original file.  It
// assumes that Encode2 was run to produce the binary file.
package hw7;
import java.io.*;
import java.util.*;

public class Decode2 {
	public static final int CHAR_MAX = 256; // max char value to be encoded

	public static void main(String[] args) {
		System.out.println("This program decodes a file with a Huffman code.");
		System.out.println();

		Scanner console = new Scanner(System.in);
		System.out.print("encoded file name? ");
		String inFile = console.nextLine();
		System.out.print("output file name? ");
		String outputFile = console.nextLine();
		console.close();

		// open encoded file, open output, build tree, decode
		try {
			BitInputStream input = new BitInputStream(inFile);
			PrintStream output = new PrintStream(new File(outputFile));
			HuffmanTree2 t = new HuffmanTree2(input);
			t.decode(input, output, CHAR_MAX);
			output.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
