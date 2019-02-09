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
public class HuffmanTree {
	private PriorityQueue<HuffmanNode> q = new PriorityQueue<HuffmanNode>();

	/**
	 * Constructs a Huffman tree using the given array of frequencies
	 * 
	 * @param count
	 *            The given array of frequencies
	 */
	public HuffmanTree(int[] count) {
		construct1(count);
	}

	/**
	 * Constructs a Huffman tree from the Scanner. Assumes the Scanner contains a
	 * tree description in standard format.
	 * 
	 * @param input
	 *            ??????
	 */
	public HuffmanTree(Scanner input) {
		construct2(input);
	}

	/**
	 * Writes the current tree to the given output stream in standard format.
	 * 
	 * @param output
	 *            The stream to print
	 */
	public void write(PrintStream output) {
		if (!q.isEmpty()) {
			output.print(getCode(q.peek(), ""));// print the codes starting from the top
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
		q.add(new HuffmanNode());// create top of tree for traversing
		while (scan.hasNextLine()) {
			int character = Integer.parseInt(scan.nextLine()); // get character
			String code = scan.nextLine(); // get Huffman Code
			buildTreeDown(q.peek(), character, code, 0); // build the Tree from the top down
		}
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
		// else keep going with next digit (bit)
		return getCharacter(current, input, input.readBit());
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
