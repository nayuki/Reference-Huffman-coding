package nayuki.huffmancoding;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class HuffmanCompress {
	
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.err.println("Usage: java HuffmanCompress [inputFile] [outputFile]");
			System.exit(1);
			return;
		}
		
		File inputFile = new File(args[0]);
		File outputFile = new File(args[1]);
		
		FrequencyTable freqs = buildFrequencyTable(inputFile);
		CodeTree code = freqs.buildCodeTree();
		CanonicalCode canonCode = new CanonicalCode(code, 257);
		code = canonCode.toCodeTree();
		
		InputStream in = new BufferedInputStream(new FileInputStream(inputFile));
		BitOutputStream out = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
		try {
			writeCode(out, canonCode);
			compress(in, out, code);
		} finally {
			out.close();
			in.close();
		}
	}
	
	
	private static FrequencyTable buildFrequencyTable(File file) throws IOException {
		FrequencyTable freq = new FrequencyTable(new int[257]);
		freq.increment(256);  // EOF symbol gets a frequency of 1
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
			if (val == -1)
				val = 0;
			
			// For this file format, we only support codes up to 255 bits long
			if (val >= 256)
				throw new RuntimeException("The code for a symbol is too long");
			
			// Write in big endian
			for (int j = 7; j >= 0; j--)
				out.write((val >>> j) & 1);
		}
	}
	
	
	private static void compress(InputStream in, BitOutputStream out, CodeTree code) throws IOException {
		while (true) {
			int b = in.read();
			if (b == -1)
				break;
			encodeAndWrite(b, out, code);
		}
		encodeAndWrite(256, out, code);  // EOF
	}
	
	
	private static void encodeAndWrite(int symbol, BitOutputStream out, CodeTree code) throws IOException {
		List<Integer> bits = code.getCode(symbol);
		for (int b : bits)
			out.write(b);
	}
	
}
