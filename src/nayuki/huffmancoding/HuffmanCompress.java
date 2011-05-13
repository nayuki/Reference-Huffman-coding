package nayuki.huffmancoding;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


// Uses static Huffman coding to compress an input file to an output file. Use HuffmanDecompress to decompress.
// Uses 257 symbols - 256 for byte values and 1 for EOF. The compressed file format contains the code length of each symbol under a canonical code, followed by the Huffman-coded data.
public final class HuffmanCompress {
	
	public static void main(String[] args) throws IOException {
		// Show what command line arguments to use
		if (args.length == 0) {
			System.err.println("Usage: java HuffmanCompress InputFile OutputFile");
			System.exit(1);
			return;
		}
		
		// Otherwise, compress
		File inputFile = new File(args[0]);
		File outputFile = new File(args[1]);
		
		// Read input file once to compute symbol frequencies
		FrequencyTable freq = getFrequencies(inputFile);
		freq.increment(256);  // EOF symbol gets a frequency of 1
		CodeTree code = freq.buildCodeTree();
		CanonicalCode canonCode = new CanonicalCode(code, 257);
		code = canonCode.toCodeTree();  // Replace code tree. For each symbol, the code value may change but the code length stays the same.
		
		// Read input file again, compress with Huffman coding, and write output file
		InputStream in = new BufferedInputStream(new FileInputStream(inputFile));
		BitOutputStream out = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
		try {
			writeCode(out, canonCode);
			compress(code, out, in);
		} finally {
			out.close();
			in.close();
		}
	}
	
	
	private static FrequencyTable getFrequencies(File file) throws IOException {
		FrequencyTable freq = new FrequencyTable(new int[257]);
		InputStream input = new BufferedInputStream(new FileInputStream(file));
		try {
			while (true) {
				int b = input.read();
				if (b == -1)
					break;
				freq.increment(b);
			}
		} finally {
			input.close();
		}
		return freq;
	}
	
	
	private static void writeCode(BitOutputStream out, CanonicalCode canonCode) throws IOException {
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
	
	
	private static void compress(CodeTree code, BitOutputStream out, InputStream in) throws IOException {
		while (true) {
			int b = in.read();
			if (b == -1)
				break;
			encodeAndWrite(code, b, out);
		}
		encodeAndWrite(code, 256, out);  // EOF
	}
	
	
	private static void encodeAndWrite(CodeTree code, int symbol, BitOutputStream out) throws IOException {
		List<Integer> bits = code.getCode(symbol);
		for (int b : bits)
			out.write(b);
	}
	
}
