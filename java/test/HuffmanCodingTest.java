/* 
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */

import java.io.EOFException;
import java.io.IOException;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the compression and decompression of a complete Huffman coding application, using the JUnit test framework.
 */
public abstract class HuffmanCodingTest {
	
	/* Test cases */
	
	@Test public void testEmpty() {
		test(new byte[0]);
	}
	
	
	@Test public void testOneSymbol() {
		test(new byte[10]);
	}
	
	
	@Test public void testSimple() {
		test(new byte[]{0, 3, 1, 2});
	}
	
	
	@Test public void testEveryByteValue() {
		byte[] b = new byte[256];
		for (int i = 0; i < b.length; i++)
			b[i] = (byte)i;
		test(b);
	}
	
	
	@Test public void testFibonacciFrequencies() {
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
	
	
	@Test public void testRandomShort() {
		for (int i = 0; i < 100; i++) {
			byte[] b = new byte[random.nextInt(1000)];
			random.nextBytes(b);
			test(b);
		}
	}
	
	
	@Test public void testRandomLong() {
		for (int i = 0; i < 3; i++) {
			byte[] b = new byte[random.nextInt(1000000)];
			random.nextBytes(b);
			test(b);
		}
	}
	
	
	/* Utilities */
	
	// Tests that the given byte array can be compressed and decompressed to the same data, and not throw any exceptions.
	private void test(byte[] b) {
		try {
			byte[] compressed = compress(b);
			byte[] decompressed = decompress(compressed);
			Assert.assertArrayEquals(b, decompressed);
		} catch (EOFException e) {
			Assert.fail("Unexpected EOF");
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}
	
	
	private static final Random random = new Random();
	
	
	/* Abstract methods */
	
	// Compression method that needs to be supplied by a subclass.
	protected abstract byte[] compress(byte[] b) throws IOException;
	
	// Decompression method that needs to be supplied by a subclass.
	protected abstract byte[] decompress(byte[] b) throws IOException;
	
}
