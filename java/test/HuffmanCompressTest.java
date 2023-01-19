/* 
 * Reference Huffman coding
 * 
 * Copyright (c) Project Nayuki
 * MIT License. See readme file.
 * https://www.nayuki.io/page/reference-huffman-coding
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Tests {@link HuffmanCompress} coupled with {@link HuffmanDecompress}.
 */
public class HuffmanCompressTest extends HuffmanCodingTest {
	
	protected byte[] compress(byte[] b) throws IOException {
		FrequencyTable freqs = new FrequencyTable(new int[257]);
		for (byte x : b)
			freqs.increment(x & 0xFF);
		freqs.increment(256);  // EOF symbol gets a frequency of 1
		CodeTree code = freqs.buildCodeTree();
		CanonicalCode canonCode = new CanonicalCode(code, 257);
		code = canonCode.toCodeTree();
		
		InputStream in = new ByteArrayInputStream(b);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BitOutputStream bitOut = new BitOutputStream(out);
		
		HuffmanCompress.writeCodeLengthTable(bitOut, canonCode);
		HuffmanCompress.compress(code, in, bitOut);
		bitOut.close();
		return out.toByteArray();
	}
	
	
	protected byte[] decompress(byte[] b) throws IOException {
		InputStream in = new ByteArrayInputStream(b);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BitInputStream bitIn = new BitInputStream(in);
		
		CanonicalCode canonCode = HuffmanDecompress.readCodeLengthTable(bitIn);
		CodeTree code = canonCode.toCodeTree();
		HuffmanDecompress.decompress(code, bitIn, out);
		return out.toByteArray();
	}
	
}
