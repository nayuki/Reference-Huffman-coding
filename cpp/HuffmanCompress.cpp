/* 
 * Compression application using static Huffman coding
 * 
 * Usage: HuffmanCompress InputFile OutputFile
 * Then use the corresponding "HuffmanDecompress" application to recreate the original input file.
 * Note that the application uses an alphabet of 257 symbols - 256 symbols for the byte values
 * and 1 symbol for the EOF marker. The compressed file format starts with a list of 257
 * code lengths, treated as a canonical code, and then followed by the Huffman-coded data.
 * 
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */

#include <cstdint>
#include <cstdlib>
#include <fstream>
#include <iostream>
#include <vector>
#include "BitIoStream.hpp"
#include "CanonicalCode.hpp"
#include "FrequencyTable.hpp"
#include "HuffmanCoder.hpp"

using std::uint32_t;


int main(int argc, char *argv[]) {
	// Handle command line arguments
	if (argc != 3) {
		std::cerr << "Usage: " << argv[0] << " InputFile OutputFile" << std::endl;
		return EXIT_FAILURE;
	}
	const char *inputFile  = argv[1];
	const char *outputFile = argv[2];
	
	// Read input file once to compute symbol frequencies.
	// The resulting generated code is optimal for static Huffman coding and also canonical.
	std::ifstream in(inputFile, std::ios::binary);
	FrequencyTable freqs(std::vector<uint32_t>(257));
	while (true) {
		int b = in.get();
		if (b == EOF)
			break;
		if (b < 0 || b > 255)
			throw "Assertion error";
		freqs.increment(static_cast<uint32_t>(b));
	}
	freqs.increment(256);  // EOF symbol gets a frequency of 1
	CodeTree code = freqs.buildCodeTree();
	CanonicalCode canonCode(code, 257);
	// Replace code tree with canonical one. For each symbol,
	// the code value may change but the code length stays the same.
	code = canonCode.toCodeTree();
	
	// Read input file again, compress with Huffman coding, and write output file
	in.clear();
	in.seekg(0);
	std::ofstream out(outputFile, std::ios::binary);
	BitOutputStream bout(out);
	try {
		
		// Write code length table
		for (uint32_t i = 0; i < canonCode.getSymbolLimit(); i++) {
			uint32_t val = canonCode.getCodeLength(i);
			// For this file format, we only support codes up to 255 bits long
			if (val >= 256)
				throw "The code for a symbol is too long";
			// Write value as 8 bits in big endian
			for (int j = 7; j >= 0; j--)
				bout.write((val >> j) & 1);
		}
		
		HuffmanEncoder enc(bout);
		enc.codeTree = &code;
		while (true) {
			// Read and encode one byte
			int symbol = in.get();
			if (symbol == EOF)
				break;
			if (symbol < 0 || symbol > 255)
				throw "Assertion error";
			enc.write(static_cast<uint32_t>(symbol));
		}
		enc.write(256);  // EOF
		bout.finish();
		return EXIT_SUCCESS;
		
	} catch (const char *msg) {
		std::cerr << msg << std::endl;
		return EXIT_FAILURE;
	}
}
