/* 
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * A canonical Huffman code, which only describes the code length of
 * each symbol. Immutable. Code length 0 means no code for the symbol.
 * <p>The binary codes for each symbol can be reconstructed from the length information.
 * In this implementation, lexicographically lower binary codes are assigned to symbols
 * with lower code lengths, breaking ties by lower symbol values. For example:</p>
 * <pre>  Code lengths (canonical code):
 *    Symbol A: 1
 *    Symbol B: 3
 *    Symbol C: 0 (no code)
 *    Symbol D: 2
 *    Symbol E: 3
 *  
 *  Sorted lengths and symbols:
 *    Symbol A: 1
 *    Symbol D: 2
 *    Symbol B: 3
 *    Symbol E: 3
 *    Symbol C: 0 (no code)
 *  
 *  Generated Huffman codes:
 *    Symbol A: 0
 *    Symbol D: 10
 *    Symbol B: 110
 *    Symbol E: 111
 *    Symbol C: None
 *  
 *  Huffman codes sorted by symbol:
 *    Symbol A: 0
 *    Symbol B: 110
 *    Symbol C: None
 *    Symbol D: 10
 *    Symbol E: 111</pre>
 * @see CodeTree
 */
public final class CanonicalCode {
	
	/* Fields and constructors */
	
	private int[] codeLengths;
	
	
	
	/**
	 * Constructs a canonical Huffman code from the specified array of symbol code lengths.
	 * Each code length must be non-negative. Code length 0 means no code for the symbol.
	 * The collection of code lengths must represent a proper full Huffman code tree.
	 * <p>Examples of code lengths that result in under-full Huffman code trees:</p>
	 * <ul>
	 *   <li>[1]</li>
	 *   <li>[3, 0, 3]</li>
	 *   <li>[1, 2, 3]</li>
	 * </ul>
	 * <p>Examples of code lengths that result in correct full Huffman code trees:</p>
	 * <ul>
	 *   <li>[1, 1]</li>
	 *   <li>[2, 2, 1, 0, 0, 0]</li>
	 *   <li>[3, 3, 3, 3, 3, 3, 3, 3]</li>
	 * </ul>
	 * <p>Examples of code lengths that result in over-full Huffman code trees:</p>
	 * <ul>
	 *   <li>[1, 1, 1]</li>
	 *   <li>[1, 1, 2, 2, 3, 3, 3, 3]</li>
	 * </ul>
	 * @param codeLens array of symbol code lengths
	 * @throws NullPointerException if the array is {@code null}
	 * @throws IllegalArgumentException if the array length is less than 2, any element is negative,
	 * or the collection of code lengths would yield an under-full or over-full Huffman code tree
	 */
	public CanonicalCode(int[] codeLens) {
		// Check basic validity
		Objects.requireNonNull(codeLens);
		if (codeLens.length < 2)
			throw new IllegalArgumentException("At least 2 symbols needed");
		for (int cl : codeLens) {
			if (cl < 0)
				throw new IllegalArgumentException("Illegal code length");
		}
		
		// Copy once and check for tree validity
		codeLengths = codeLens.clone();
		Arrays.sort(codeLengths);
		int currentLevel = codeLengths[codeLengths.length - 1];
		int numNodesAtLevel = 0;
		for (int i = codeLengths.length - 1; i >= 0 && codeLengths[i] > 0; i--) {
			int cl = codeLengths[i];
			while (cl < currentLevel) {
				if (numNodesAtLevel % 2 != 0)
					throw new IllegalArgumentException("Under-full Huffman code tree");
				numNodesAtLevel /= 2;
				currentLevel--;
			}
			numNodesAtLevel++;
		}
		while (currentLevel > 0) {
			if (numNodesAtLevel % 2 != 0)
				throw new IllegalArgumentException("Under-full Huffman code tree");
			numNodesAtLevel /= 2;
			currentLevel--;
		}
		if (numNodesAtLevel < 1)
			throw new IllegalArgumentException("Under-full Huffman code tree");
		if (numNodesAtLevel > 1)
			throw new IllegalArgumentException("Over-full Huffman code tree");
		
		// Copy again
		System.arraycopy(codeLens, 0, codeLengths, 0, codeLens.length);
	}
	
	
	/**
	 * Builds a canonical Huffman code from the specified code tree.
	 * @param tree the code tree to analyze
	 * @param symbolLimit a number greater than the maximum symbol value in the tree
	 * @throws NullPointerException if the tree is {@code null}
	 * @throws IllegalArgumentException if the symbol limit is less than 2, or a
	 * leaf node in the tree has symbol value greater or equal to the symbol limit
	 */
	public CanonicalCode(CodeTree tree, int symbolLimit) {
		Objects.requireNonNull(tree);
		if (symbolLimit < 2)
			throw new IllegalArgumentException("At least 2 symbols needed");
		codeLengths = new int[symbolLimit];
		buildCodeLengths(tree.root, 0);
	}
	
	
	// Recursive helper method for the above constructor.
	private void buildCodeLengths(Node node, int depth) {
		if (node instanceof InternalNode) {
			InternalNode internalNode = (InternalNode)node;
			buildCodeLengths(internalNode.leftChild , depth + 1);
			buildCodeLengths(internalNode.rightChild, depth + 1);
		} else if (node instanceof Leaf) {
			int symbol = ((Leaf)node).symbol;
			// Note: CodeTree already has a checked constraint that disallows a symbol in multiple leaves
			if (codeLengths[symbol] != 0)
				throw new AssertionError("Symbol has more than one code");
			if (symbol >= codeLengths.length)
				throw new IllegalArgumentException("Symbol exceeds symbol limit");
			codeLengths[symbol] = depth;
		} else {
			throw new AssertionError("Illegal node type");
		}
	}
	
	
	
	/* Various methods */
	
	/**
	 * Returns the symbol limit for this canonical Huffman code.
	 * Thus this code covers symbol values from 0 to symbolLimit&minus;1.
	 * @return the symbol limit, which is at least 2
	 */
	public int getSymbolLimit() {
		return codeLengths.length;
	}
	
	
	/**
	 * Returns the code length of the specified symbol value. The result is 0
	 * if the symbol has node code; otherwise the result is a positive number.
	 * @param symbol the symbol value to query
	 * @return the code length of the symbol, which is non-negative
	 * @throws IllegalArgumentException if the symbol is out of range
	 */
	public int getCodeLength(int symbol) {
		if (symbol < 0 || symbol >= codeLengths.length)
			throw new IllegalArgumentException("Symbol out of range");
		return codeLengths[symbol];
	}
	
	
	/**
	 * Returns the canonical code tree for this canonical Huffman code.
	 * @return the canonical code tree
	 */
	public CodeTree toCodeTree() {
		List<Node> nodes = new ArrayList<Node>();
		for (int i = max(codeLengths); i >= 0; i--) {  // Descend through code lengths
			if (nodes.size() % 2 != 0)
				throw new AssertionError("Violation of canonical code invariants");
			List<Node> newNodes = new ArrayList<Node>();
			
			// Add leaves for symbols with positive code length i
			if (i > 0) {
				for (int j = 0; j < codeLengths.length; j++) {
					if (codeLengths[j] == i)
						newNodes.add(new Leaf(j));
				}
			}
			
			// Merge pairs of nodes from the previous deeper layer
			for (int j = 0; j < nodes.size(); j += 2)
				newNodes.add(new InternalNode(nodes.get(j), nodes.get(j + 1)));
			nodes = newNodes;
		}
		
		if (nodes.size() != 1)
			throw new AssertionError("Violation of canonical code invariants");
		return new CodeTree((InternalNode)nodes.get(0), codeLengths.length);
	}
	
	
	// Returns the maximum value in the given array, which must have at least 1 element.
	private static int max(int[] array) {
		int result = array[0];
		for (int x : array)
			result = Math.max(x, result);
		return result;
	}
	
}
