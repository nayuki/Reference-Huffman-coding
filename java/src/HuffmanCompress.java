/* 
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


// Uses static Huffman coding to compress an input file to an output file. Use HuffmanDecompress to decompress.
// Uses 257 symbols - 256 for byte values and 1 for EOF. The compressed file format contains the code length of each symbol under a canonical code, followed by the Huffman-coded data.
public final class HuffmanCompress {
	
	public static void main(String[] args) throws IOException {
		// Handle command line arguments
		if (args.length != 2) {
			System.err.println("Usage: java HuffmanCompress InputFile OutputFile");
			System.exit(1);
			return;
		}
		File inputFile  = new File(args[0]);
		File outputFile = new File(args[1]);
		
		// Read input file once to compute symbol frequencies
		// The resulting generated code is optimal for static Huffman coding and also canonical
		FrequencyTable freqs = getFrequencies(inputFile);
		freqs.increment(256);  // EOF symbol gets a frequency of 1
		CodeTree code = freqs.buildCodeTree();
		CanonicalCode canonCode = new CanonicalCode(code, 257);
		code = canonCode.toCodeTree();  // Replace code tree with canonical one. For each symbol, the code value may change but the code length stays the same.
		
		// Read input file again, compress with Huffman coding, and write output file
		InputStream in = new BufferedInputStream(new FileInputStream(inputFile));
		BitOutputStream out = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
		try {
			writeCode(out, canonCode);
			compress(code, in, out);
		} finally {
			out.close();
			in.close();
		}
	}
	
	
	// Returns a frequency table based on the bytes in the given file.
	// Also contains an extra entry for symbol 256, whose frequency is set to 0.
	private static FrequencyTable getFrequencies(File file) throws IOException {
		FrequencyTable freqs = new FrequencyTable(new int[257]);
		InputStream input = new BufferedInputStream(new FileInputStream(file));
		try {
			while (true) {
				int b = input.read();
				if (b == -1)
					break;
				freqs.increment(b);
			}
		} finally {
			input.close();
		}
		return freqs;
	}
	
	
	// To allow unit testing, this method is package-private instead of private.
	static void writeCode(BitOutputStream out, CanonicalCode canonCode) throws IOException {
		for (int i = 0; i < canonCode.getSymbolLimit(); i++) {
			int val = canonCode.getCodeLength(i);
			// For this file format, we only support codes up to 255 bits long
			if (val >= 256)
				throw new RuntimeException("The code for a symbol is too long");
			
			// Write value as 8 bits in big endian
			for (int j = 7; j >= 0; j--)
				out.write((val >>> j) & 1);
		}
	}
	
	
	// To allow unit testing, this method is package-private instead of private.
	static void compress(CodeTree code, InputStream in, BitOutputStream out) throws IOException {
		HuffmanEncoder enc = new HuffmanEncoder(out);
		enc.codeTree = code;
		while (true) {
			int b = in.read();
			if (b == -1)
				break;
			enc.write(b);
		}
		enc.write(256);  // EOF
	}
	
}
