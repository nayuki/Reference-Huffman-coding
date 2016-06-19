/* 
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


// Tests AdaptiveHuffmanCompress coupled with AdaptiveHuffmanDecompress.
public class AdaptiveHuffmanCompressTest extends HuffmanCodingTest {
	
	protected byte[] compress(byte[] b) throws IOException {
		InputStream in = new ByteArrayInputStream(b);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BitOutputStream bitOut = new BitOutputStream(out);
		AdaptiveHuffmanCompress.compress(in, bitOut);
		bitOut.close();
		return out.toByteArray();
	}
	
	
	protected byte[] decompress(byte[] b) throws IOException {
		InputStream in = new ByteArrayInputStream(b);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		AdaptiveHuffmanDecompress.decompress(new BitInputStream(in), out);
		return out.toByteArray();
	}
	
}
