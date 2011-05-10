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


public class AdaptiveHuffmanCompress {
	
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.err.println("Usage: java AdaptiveHuffmanCompress [inputFile] [outputFile]");
			System.exit(1);
			return;
		}
		
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
		CodeTree code = freqTable.buildCodeTree();
		int count = 0;
		while (true) {
			int b = in.read();
			if (b == -1)
				break;
			encodeAndWrite(b, out, code);
			freqTable.increment(b);
			count++;
			if (count % 65536 == 0) {
				code = freqTable.buildCodeTree();
				freqTable = new FrequencyTable(initFreqs);
			}
		}
		encodeAndWrite(256, out, code);  // EOF
	}
	
	
	private static void encodeAndWrite(int symbol, BitOutputStream out, CodeTree code) throws IOException {
		List<Integer> bits = code.getCode(symbol);
		for (int b : bits)
			out.write(b);
	}
	
}
