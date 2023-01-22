/* 
 * Reference Huffman coding
 * 
 * Copyright (c) Project Nayuki
 * MIT License. See readme file.
 * https://www.nayuki.io/page/reference-huffman-coding
 */


/**
 * A leaf node in a code tree. It has a symbol value. Immutable.
 * @see CodeTree
 */
public final class Leaf implements Node {
	
	public final int symbol;  // Always non-negative
	
	
	
	public Leaf(int sym) {
		if (sym < 0)
			throw new IllegalArgumentException("Symbol value must be non-negative");
		symbol = sym;
	}
	
}
