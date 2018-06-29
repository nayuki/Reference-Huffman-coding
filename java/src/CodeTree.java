/* 
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A binary tree that represents a mapping between symbols and binary strings.
 * The data structure is immutable. There are two main uses of a code tree:
 * <ul>
 *   <li>Read the root field and walk through the tree to extract the desired information.</li>
 *   <li>Call getCode() to get the binary code for a particular encodable symbol.</li>
 * </ul>
 * <p>The path to a leaf node determines the leaf's symbol's code. Starting from the root, going
 * to the left child represents a 0, and going to the right child represents a 1. Constraints:</p>
 * <ul>
 *   <li>The root must be an internal node, and the tree is finite.</li>
 *   <li>No symbol value is found in more than one leaf.</li>
 *   <li>Not every possible symbol value needs to be in the tree.</li>
 * </ul>
 * <p>Illustrated example:</p>
 * <pre>  Huffman codes:
 *    0: Symbol A
 *    10: Symbol B
 *    110: Symbol C
 *    111: Symbol D
 *  
 *  Code tree:
 *      .
 *     / \
 *    A   .
 *       / \
 *      B   .
 *         / \
 *        C   D</pre>
 * @see FrequencyTable
 * @see CanonicalCode
 */
public final class CodeTree {
	
	/*---- Fields and constructor ----*/
	
	/**
	 * The root node of this code tree (not {@code null}).
	 */
	public final InternalNode root;
	
	// Stores the code for each symbol, or null if the symbol has no code.
	// For example, if symbol 5 has code 10011, then codes.get(5) is the list [1,0,0,1,1].
	private List<List<Integer>> codes;
	
	
	
	/**
	 * Constructs a code tree from the specified tree of nodes and specified symbol limit.
	 * Each symbol in the tree must have value strictly less than the symbol limit.
	 * @param root the root of the tree
	 * @param symbolLimit the symbol limit
	 * @throws NullPointerException if tree root is {@code null}
	 * @throws IllegalArgumentException if the symbol limit is less than 2, any symbol in the tree has
	 * a value greater or equal to the symbol limit, or a symbol value appears more than once in the tree
	 */
	public CodeTree(InternalNode root, int symbolLimit) {
		Objects.requireNonNull(root);
		if (symbolLimit < 2)
			throw new IllegalArgumentException("At least 2 symbols needed");
		
		this.root = root;
		codes = new ArrayList<List<Integer>>();  // Initially all null
		for (int i = 0; i < symbolLimit; i++)
			codes.add(null);
		buildCodeList(root, new ArrayList<Integer>());  // Fill 'codes' with appropriate data
	}
	
	
	// Recursive helper function for the constructor
	private void buildCodeList(Node node, List<Integer> prefix) {
		if (node instanceof InternalNode) {
			InternalNode internalNode = (InternalNode)node;
			
			prefix.add(0);
			buildCodeList(internalNode.leftChild , prefix);
			prefix.remove(prefix.size() - 1);
			
			prefix.add(1);
			buildCodeList(internalNode.rightChild, prefix);
			prefix.remove(prefix.size() - 1);
			
		} else if (node instanceof Leaf) {
			Leaf leaf = (Leaf)node;
			if (leaf.symbol >= codes.size())
				throw new IllegalArgumentException("Symbol exceeds symbol limit");
			if (codes.get(leaf.symbol) != null)
				throw new IllegalArgumentException("Symbol has more than one code");
			codes.set(leaf.symbol, new ArrayList<Integer>(prefix));
			
		} else {
			throw new AssertionError("Illegal node type");
		}
	}
	
	
	
	/*---- Various methods ----*/
	
	/**
	 * Returns the Huffman code for the specified symbol, which is a list of 0s and 1s.
	 * @param symbol the symbol to query
	 * @return a list of 0s and 1s, of length at least 1
	 * @throws IllegalArgumentException if the symbol is negative, or no
	 * Huffman code exists for it (e.g. because it had a zero frequency)
	 */
	public List<Integer> getCode(int symbol) {
		if (symbol < 0)
			throw new IllegalArgumentException("Illegal symbol");
		else if (codes.get(symbol) == null)
			throw new IllegalArgumentException("No code for given symbol");
		else
			return codes.get(symbol);
	}
	
	
	/**
	 * Returns a string representation of this code tree,
	 * useful for debugging only, and the format is subject to change.
	 * @return a string representation of this code tree
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString("", root, sb);
		return sb.toString();
	}
	
	
	// Recursive helper function for toString()
	private static void toString(String prefix, Node node, StringBuilder sb) {
		if (node instanceof InternalNode) {
			InternalNode internalNode = (InternalNode)node;
			toString(prefix + "0", internalNode.leftChild , sb);
			toString(prefix + "1", internalNode.rightChild, sb);
		} else if (node instanceof Leaf) {
			sb.append(String.format("Code %s: Symbol %d%n", prefix, ((Leaf)node).symbol));
		} else {
			throw new AssertionError("Illegal node type");
		}
	}
	
}
