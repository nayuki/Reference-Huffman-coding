package nayuki.huffmancoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


// Tests HuffmanCompress coupled with HuffmanDecompress.
public class HuffmanCompressTest extends HuffmanCodingTest {
	
	protected byte[] compress(byte[] b) throws IOException {
		InputStream in = new ByteArrayInputStream(b);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BitOutputStream bitOut = new BitOutputStream(out);
		
		FrequencyTable freq = getFrequencies(b);
		CodeTree code = freq.buildCodeTree();
		CanonicalCode canonCode = new CanonicalCode(code, 257);
		code = canonCode.toCodeTree();
		HuffmanCompress.writeCode(bitOut, canonCode);
		HuffmanCompress.compress(code, in, bitOut);
		bitOut.close();
		return out.toByteArray();
	}
	
	
	protected byte[] decompress(byte[] b) throws IOException {
		InputStream in = new ByteArrayInputStream(b);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BitInputStream bitIn = new BitInputStream(in);
		
		CanonicalCode canonCode = HuffmanDecompress.readCode(bitIn);
		CodeTree code = canonCode.toCodeTree();
		HuffmanDecompress.decompress(code, bitIn, out);
		return out.toByteArray();
	}
	
	
	private static FrequencyTable getFrequencies(byte[] b) {
		FrequencyTable freq = new FrequencyTable(new int[257]);
		for (byte x : b)
			freq.increment(x & 0xFF);
		freq.increment(256);  // EOF symbol gets a frequency of 1
		return freq;
	}
	
}
