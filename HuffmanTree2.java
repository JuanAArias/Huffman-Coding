package hw7;
import java.io.*;
import java.util.*;

/**
 * A HuffmanTree is a binary tree with a PriorityQueue, that compresses text
 * using a coding scheme based on the frequency of characters (Huffman coding)
 * 
 * @author Juan.Arias
 *
 */
public class HuffmanTree2 {
	private PriorityQueue<HuffmanNode> q = new PriorityQueue<HuffmanNode>();

	/**
	 * Constructs a Huffman tree using the given array of frequencies
	 * 
	 * @param count
	 *            The given array of frequencies
	 */
	public HuffmanTree2(int[] count) {
		construct1(count);
	}

	/**
	 * Constructs a Huffman tree from the Scanner. Assumes the Scanner contains a
	 * tree description in standard format.
	 * 
	 * @param input
	 *            ??????
	 */
	public HuffmanTree2(Scanner input) {
		construct2(input);
	}

	/**
	 * Constructs a Huffman tree from the given input stream. Assumes that the
	 * standard bit representation has been used for the tree.
	 *
	 * @param input
	 *            The BitStream
	 */
	public HuffmanTree2(BitInputStream input) {
		construct3(input);
	}

	/**
	 * Writes the current tree to the given output stream in standard format.
	 * 
	 * @param output
	 *            The stream to print
	 */
	public void write(PrintStream output) {
		if (!q.isEmpty()) {
			// print the codes starting from the top
			output.print(getCode(q.peek(), ""));
		}
	}

	/**
	 * Reads bits from the given input stream, and writes the corresponding
	 * characters to the output. Stops reading when it encounters a character with
	 * value equal to eof. Assumes the input stream contains a legal encoding of
	 * characters.
	 * 
	 * @param input
	 *            The stream of encodings of characters
	 * @param output
	 *            The stream to write the character to
	 * @param eof
	 *            The end of file character
	 */
	public void decode(BitInputStream input, PrintStream output, int eof) {
		int bit;
		while ((bit = input.readBit()) != -1) {
			int character = getCharacter(q.peek(), input, bit);// get each character
			if (character == eof) {// file is over
				break;
			}
			output.write(character);// write the character
		}
	}

	/**
	 * Assigns codes for each character of the tree. Assumes the array has null
	 * values before the method is called. Fills in a String for each character in
	 * the tree indicating its code
	 * 
	 * @param codes
	 *            The array of Strings to fill
	 */
	public void assign(String[] codes) {
		// get line by line output with character and code
		String s = getCode(q.peek(), "");
		Scanner scan = new Scanner(s);
		while (scan.hasNextLine()) {
			// character value will be index of array
			int index = Integer.parseInt(scan.nextLine());
			// code of character will be value at index
			String code = scan.nextLine();
			codes[index] = code;
		}
		scan.close();
	}

	/**
	 * Writes the current tree to the output stream using the standard bit
	 * representation.
	 * 
	 * @param output
	 *            The bit stream to write to
	 * 
	 */
	public void writeHeader(BitOutputStream output) {
		writeBits(output, q.peek());// write the bits starting from the top of the tree
	}

	/**
	 * Constructs HuffmanTree with the given array of frequencies for characters,
	 * creates a leaf for each character, then builds the tree from the bottom up
	 * 
	 * @param frequencies
	 *            The array containing the frequencies for each character(index)
	 */
	private void construct1(int[] frequencies) {
		for (int i = 0; i < frequencies.length; i++) {
			if (frequencies[i] > 0) {// if character has a frequency, add a leaf
				q.offer(new HuffmanNode(frequencies[i], i));
			}
		}
		q.offer(new HuffmanNode(1, frequencies.length)); // add eof character
		buildTreeUp();// build the Tree from bottom up
	}

	/**
	 * Constructs HuffmanTree with the given Scanner with Huffman coding
	 * 
	 * @param scan
	 *            The Scanner with the Huffman code file
	 */
	private void construct2(Scanner scan) {
		// create top of tree for traversing
		q.add(new HuffmanNode());
		while (scan.hasNextLine()) {
			int character = Integer.parseInt(scan.nextLine());// get character
			String code = scan.nextLine();// get Huffman Code
			// build the Tree from the top down
			buildTreeDown(q.peek(), character, code, 0);
		}
	}

	/**
	 * Adds the overall root of the Tree to the PriorityQueue
	 * 
	 * @param input
	 *            The stream of bits
	 */
	private void construct3(BitInputStream input) {// SUPPOSE TO HAVE FREQUENCIES?
		q.add(buildTree3(null, input, input.readBit()));
	}

	/**
	 * Builds HuffmanTree up starting with the leaves in the Queue, ends once there
	 * is one final Node in the Queue for the top of Tree
	 */
	private void buildTreeUp() {
		// while not one single root
		while (q.size() > 1) {
			HuffmanNode left = q.poll();// get the first Node, store its frequency
			int freq = left.frequency;
			HuffmanNode right = null;
			// if there is still a second Node,
			if (!q.isEmpty()) {
				right = q.poll();// get it and add its frequency to the first
				freq += right.frequency;
			}
			// add the branch node back into the Queue
			q.add(new HuffmanNode(freq, left, right));
		}
	}

	/**
	 * Builds HuffmanTree down with the current HuffmanNode, ASCII character with
	 * its code and current index of the String code
	 * 
	 * @param current
	 *            The current HuffmanNode
	 * @param character
	 *            The ASCII character to placed in the leaf
	 * @param code
	 *            The Huffman Code of the given character
	 * @param i
	 *            The current index of the String code
	 */
	private void buildTreeDown(HuffmanNode current, int character, String code, int i) {
		// current digit in code
		char digit = code.charAt(i);
		// if last digit in code, create leaf
		if (i == code.length() - 1) {
			if (digit == '0') {
				current.left = new HuffmanNode(0, character);// leaf goes on left of current
			} else {
				current.right = new HuffmanNode(0, character);// leaf goes on right of current
			}
			// keep going down tree
		} else {
			if (digit == '0') {// go left
				if (current.left == null) {// create branch if not there already
					current.left = new HuffmanNode();
				}
				buildTreeDown(current.left, character, code, i + 1);
			} else { // go right
				if (current.right == null) {// create branch if not there already
					current.right = new HuffmanNode();
				}
				buildTreeDown(current.right, character, code, i + 1);
			}
		}
	}

	/**
	 * Builds the tree with preorder traversal and returns the overall root
	 * 
	 * @param current
	 *            The current HuffmanNode
	 * @param input
	 *            The stream of bits
	 * @param bit
	 *            The current bit im the stream
	 * @return
	 */
	private HuffmanNode buildTree3(HuffmanNode current, BitInputStream input, int bit) {
		//stream is over
		if (bit == -1) {
			return null;
		}
		//create leaf
		if (bit == 1) {
			current = new HuffmanNode(1, read9(input));
		//create branch and continue traversal
		} else {
			//left first
			current = new HuffmanNode();
			current.left = buildTree3(current.left, input, input.readBit());
			int nextBit = input.readBit();
			//if stream is not over, go right
			if (nextBit != -1) {
				current.right = buildTree3(current.right, input, nextBit);
			}

		}
		return current;
	}

	/**
	 * Returns all the Huffman codes for characters in the tree
	 * 
	 * @param current
	 *            The current HuffmanNode
	 * @param code
	 *            The code to be assigned to the character
	 * @return The Huffman codes for all characters
	 */
	private String getCode(HuffmanNode current, String code) {
		if (current != null) {
			// if at leaf, return character & code
			if (current.left == null && current.right == null) {
				return current.character + "\n" + code + "\n";
			} else {// keep going
				String s = getCode(current.left, code + "0");// add 0 for going left
				return s + getCode(current.right, code + "1");// add 1 for going right
			}
		}
		return "";
	}

	/**
	 * Traverses down the tree until the leaf is found and the ASCII character is
	 * returned
	 * 
	 * @param current
	 *            The current HuffmanNode
	 * @param input
	 *            The input stream of Huffman coding
	 * @param digit
	 *            The current digit in the sequence of Huffman codoing
	 * 
	 * @return The character associated with the code
	 */
	private int getCharacter(HuffmanNode current, BitInputStream input, int digit) {
		// start by going left or right
		current = (digit == 0) ? current.left : current.right;
		// if at leaf, return character
		if (current.left == null && current.right == null) {
			return current.character;
		}
		// keep going with next digit (bit)
		return getCharacter(current, input, input.readBit());
	}

	/**
	 * Writes to the bit stream a representation of the tree using preorder
	 * traversal
	 * * 0 is written for branch
	 * * 1 is written for leaf
	 * * helper methods to write its character in bits
	 * 
	 * @param output
	 *            The bit stream to write to
	 * @param current
	 *            The current HuffmanNode in the tree
	 */
	private void writeBits(BitOutputStream output, HuffmanNode current) {
		if (current != null) {
			// if at leaf write 1 and character in bits
			if (current.left == null && current.right == null) {
				output.writeBit(1);
				write9(output, current.character);
				// write 0 for branch and continue down tree
			} else {
				output.writeBit(0);
				writeBits(output, current.left);
				writeBits(output, current.right);
			}
		}
	}

	// pre : 0 <= n < 512
	// post: writes a 9-bit representation of n to the given output stream
	private void write9(BitOutputStream output, int n) {
		for (int i = 0; i < 9; i++) {
			output.writeBit(n % 2);
			n /= 2;
		}
	}

	// pre : an integer n has been encoded using write9 or its equivalent
	// post: reads 9 bits to reconstruct the original integer
	private int read9(BitInputStream input) {
		int multiplier = 1;
		int sum = 0;
		for (int i = 0; i < 9; i++) {
			sum += multiplier * input.readBit();
			multiplier *= 2;
		}
		return sum;
	}

	/**
	 * A node for a HuffmanTree
	 *
	 */
	private class HuffmanNode implements Comparable<HuffmanNode> {
		private int character;
		private int frequency;
		private HuffmanNode left;
		private HuffmanNode right;

		/**
		 * For branches only: Constructs an empty HuffmanNode
		 * 
		 * @param freq
		 *            The data number
		 */
		private HuffmanNode() {
			this(0);
		}

		/**
		 * Constructs a HuffmanNode with given number
		 * 
		 * @param freq
		 *            The data number
		 */
		private HuffmanNode(int freq) {
			this(freq, null, null);
		}

		/**
		 * Constructs a HuffmanNode with given number, left and right HuffmanNode
		 * children
		 * 
		 * @param freq
		 *            The data number
		 * @param left
		 *            The child HuffmanNode
		 * 
		 * @param right
		 *            The child HuffmanNode
		 * 
		 */
		private HuffmanNode(int freq, HuffmanNode left, HuffmanNode right) {
			frequency = freq;
			this.left = left;
			this.right = right;
		}

		/**
		 * For leaves only: Constructs a HuffmanNode with given number, and ASCII
		 * character
		 * 
		 * @param freq
		 *            The data number
		 * @param character
		 *            The ASCII character
		 */
		private HuffmanNode(int freq, int character) {
			this(freq);
			this.character = character;
		}

		/**
		 * Compares this HuffmanNode to another by the data fields
		 * 
		 * @param o
		 *            The other HuffmanNode
		 * @return int comparison
		 * 
		 */
		@Override
		public int compareTo(HuffmanNode o) {
			return frequency - o.frequency;
		}
	}
}
