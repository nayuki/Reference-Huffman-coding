/* 
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */

import java.io.IOException;
import java.util.List;


/**
 * Encodes symbols and writes to a Huffman-coded bit stream. Not thread-safe.
 * @see HuffmanDecoder
 */
public final class HuffmanEncoder {
	
	// The underlying bit output stream (not null).
	private BitOutputStream output;
	
	/**
	 * The code tree to use in the next {@link#write(int)} operation. Must be given a non-{@code null}
	 * value before calling write(). The tree can be changed after each symbol encoded, as long
	 * as the encoder and decoder have the same tree at the same point in the code stream.
	 */
	public CodeTree codeTree;
	
	
	
	/**
	 * Constructs a Huffman encoder based on the specified bit output stream.
	 * @param in the bit output stream to write to
	 * @throws NullPointerException if the output stream is {@code null}
	 */
	public HuffmanEncoder(BitOutputStream out) {
		if (out == null)
			throw new NullPointerException();
		output = out;
	}
	
	
	
	/**
	 * Encodes the specified symbol and writes to the Huffman-coded output stream.
	 * @param symbol the symbol to encode, which is non-negative and must be in the range of the code tree
	 * @throws IOException if an I/O exception occurred
	 * @throws NullPointerException if the current code tree is {@code null}
	 */
	public void write(int symbol) throws IOException {
		if (codeTree == null)
			throw new NullPointerException("Code tree is null");
		
		List<Integer> bits = codeTree.getCode(symbol);
		for (int b : bits)
			output.write(b);
	}
	
}
