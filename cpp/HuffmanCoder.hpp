/* 
 * Reference Huffman coding
 * 
 * Copyright (c) Project Nayuki
 * MIT License. See readme file.
 * https://www.nayuki.io/page/reference-huffman-coding
 */

#pragma once

#include "BitIoStream.hpp"
#include "CodeTree.hpp"


/* 
 * Reads from a Huffman-coded bit stream and decodes symbols.
 */
class HuffmanDecoder final {
	
	/*---- Fields ----*/
	
	// The underlying bit input stream.
	private: BitInputStream &input;
	
	// The code tree to use in the next read() operation. Must be given a non-null value
	// before calling read(). The tree can be changed after each symbol decoded, as long
	// as the encoder and decoder have the same tree at the same point in the code stream.
	public: const CodeTree *codeTree;
	
	
	/*---- Constructor ----*/
	
	// Constructs a Huffman decoder based on the given bit input stream.
	public: explicit HuffmanDecoder(BitInputStream &in);
	
	
	/*---- Method ----*/
	
	// Reads from the input stream to decode the next Huffman-coded symbol.
	public: int read();
	
};



/* 
 * Encodes symbols and writes to a Huffman-coded bit stream.
 */
class HuffmanEncoder final {
	
	/*---- Fields ----*/
	
	// The underlying bit output stream.
	private: BitOutputStream &output;
	
	// The code tree to use in the next write(uint32_t) operation. Must be given a non-null
	// value before calling write(). The tree can be changed after each symbol encoded, as long
	// as the encoder and decoder have the same tree at the same point in the code stream.
	public: const CodeTree *codeTree;
	
	
	/*---- Constructor ----*/
	
	// Constructs a Huffman encoder based on the given bit output stream.
	public: explicit HuffmanEncoder(BitOutputStream &out);
	
	
	/*---- Method ----*/
	
	// Encodes the given symbol and writes to the Huffman-coded output stream.
	public: void write(std::uint32_t symbol);
	
};
