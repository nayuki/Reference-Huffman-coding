/* 
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */


/**
 * A leaf node in a code tree. It has a symbol value. Immutable.
 */
public final class Leaf extends Node {
	
	public final int symbol;
	
	
	
	public Leaf(int symbol) {
		if (symbol < 0)
			throw new IllegalArgumentException("Illegal symbol value");
		this.symbol = symbol;
	}
	
}
