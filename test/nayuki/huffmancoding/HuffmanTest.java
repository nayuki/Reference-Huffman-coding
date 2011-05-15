package nayuki.huffmancoding;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.junit.Test;


// Tests HuffmanCompress coupled with HuffmanDecompress.
public class HuffmanTest {
	
	@Test
	public void testEmpty() {
		test(new byte[0]);
	}
	
	
	@Test
	public void testOneSymbol() {
		test(new byte[10]);
	}
	
	
	@Test
	public void testSimple() {
		test(new byte[]{0, 3, 1, 2});
	}
	
	
	@Test
	public void testEveryByteValue() {
		byte[] b = new byte[256];
		for (int i = 0; i < b.length; i++)
			b[i] = (byte)i;
		test(b);
	}
	
	
	@Test
	public void testFibonacciFrequencies() {
		byte[] b = new byte[87];
		int i = 0;
		for (int j = 0; j <  1; j++, i++) b[i] = 0;
		for (int j = 0; j <  2; j++, i++) b[i] = 1;
		for (int j = 0; j <  3; j++, i++) b[i] = 2;
		for (int j = 0; j <  5; j++, i++) b[i] = 3;
		for (int j = 0; j <  8; j++, i++) b[i] = 4;
		for (int j = 0; j < 13; j++, i++) b[i] = 5;
		for (int j = 0; j < 21; j++, i++) b[i] = 6;
		for (int j = 0; j < 34; j++, i++) b[i] = 7;
		test(b);
	}
	
	
	@Test
	public void testRandom() {
		for (int i = 0; i < 100; i++) {
			byte[] b = new byte[random.nextInt(1000)];
			random.nextBytes(b);
			test(b);
		}
	}
	
	
	
	private static Random random = new Random();
	
	
	private static void test(byte[] b) {
		try {
			CodeTree code = makeCode(b);
			
			InputStream original = new ByteArrayInputStream(b);
			ByteArrayOutputStream compressedOut = new ByteArrayOutputStream();
			BitOutputStream compressedOutBit = new BitOutputStream(compressedOut);
			HuffmanCompress.compress(code, original, compressedOutBit);
			compressedOutBit.close();
			byte[] compressed = compressedOut.toByteArray();
			
			InputStream compressedIn = new ByteArrayInputStream(compressed);
			BitInputStream compressedInBit = new BitInputStream(compressedIn);
			ByteArrayOutputStream decompressed = new ByteArrayOutputStream();
			HuffmanDecompress.decompress(code, compressedInBit, decompressed);
			
			assertArrayEquals(b, decompressed.toByteArray());
		} catch (EOFException e) {
			fail("Unexpected EOF");
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}
	
	
	// Makes the canonical optimal static code for the given bytes
	private static CodeTree makeCode(byte[] b) {
		FrequencyTable freq = new FrequencyTable(new int[257]);
		freq.increment(256);  // EOF symbol
		for (byte x : b)
			freq.increment(x & 0xFF);
		CodeTree code = freq.buildCodeTree();
		CanonicalCode canonCode = new CanonicalCode(code, 257);
		return canonCode.toCodeTree();
	}
	
}
