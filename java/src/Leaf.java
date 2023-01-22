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
public record Leaf(int symbol) implements Node {
	
	public Leaf {
		if (symbol < 0)
			throw new IllegalArgumentException("Symbol value must be non-negative");
	}
	
}
