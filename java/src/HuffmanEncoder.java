/* 
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */

import java.io.IOException;
import java.util.List;


public final class HuffmanEncoder {
	
	private BitOutputStream output;
	
	// Must be initialized before calling write().
	// The code tree can be changed after each symbol encoded, as long as the encoder and decoder have the same code tree at the same time.
	public CodeTree codeTree;
	
	
	
	public HuffmanEncoder(BitOutputStream out) {
		if (out == null)
			throw new NullPointerException("Argument is null");
		output = out;
	}
	
	
	
	public void write(int symbol) throws IOException {
		if (codeTree == null)
			throw new NullPointerException("Code tree is null");
		
		List<Integer> bits = codeTree.getCode(symbol);
		for (int b : bits)
			output.write(b);
	}
	
}
