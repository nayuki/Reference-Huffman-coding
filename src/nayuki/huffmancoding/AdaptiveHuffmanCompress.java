package nayuki.huffmancoding;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;


public final class AdaptiveHuffmanCompress {
	
	public static void main(String[] args) throws IOException {
		// Show what command line arguments to use
		if (args.length == 0) {
			System.err.println("Usage: java AdaptiveHuffmanCompress InputFile OutputFile");
			System.exit(1);
			return;
		}
		
		// Otherwise, compress
		File inputFile = new File(args[0]);
		File outputFile = new File(args[1]);
		
		InputStream in = new BufferedInputStream(new FileInputStream(inputFile));
		BitOutputStream out = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
		try {
			compress(in, out);
		} finally {
			out.close();
			in.close();
		}
	}
	
	
	private static void compress(InputStream in, BitOutputStream out) throws IOException {
		int[] initFreqs = new int[257];
		Arrays.fill(initFreqs, 1);
		
		FrequencyTable freqTable = new FrequencyTable(initFreqs);
		CodeTree code = freqTable.buildCodeTree();  // We don't need to make a canonical code since we don't transmit the code tree
		int count = 0;
		while (true) {
			int b = in.read();
			if (b == -1)
				break;
			encodeAndWrite(code, b, out);
			freqTable.increment(b);
			count++;
			if (count % 65536 == 0) {  // Occasionally rebuild the code tree based on recent statistics
				code = freqTable.buildCodeTree();
				freqTable = new FrequencyTable(initFreqs);
			}
		}
		encodeAndWrite(code, 256, out);  // EOF
	}
	
	
	private static void encodeAndWrite(CodeTree code, int symbol, BitOutputStream out) throws IOException {
		List<Integer> bits = code.getCode(symbol);
		for (int b : bits)
			out.write(b);
	}
	
}
