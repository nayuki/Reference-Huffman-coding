package nayuki.huffmancoding;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;


public final class AdaptiveHuffmanDecompress {
	
	public static void main(String[] args) throws IOException {
		// Show what command line arguments to use
		if (args.length == 0) {
			System.err.println("Usage: java AdaptiveHuffmanDecompress InputFile OutputFile");
			System.exit(1);
			return;
		}
		
		// Otherwise, decompress
		File inputFile = new File(args[0]);
		File outputFile = new File(args[1]);
		
		BitInputStream in = new BitInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
		OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile));
		try {
			decompress(in, out);
		} finally {
			out.close();
			in.close();
		}
	}
	
	
	static void decompress(BitInputStream in, OutputStream out) throws IOException {
		int[] initFreqs = new int[257];
		Arrays.fill(initFreqs, 1);
		
		FrequencyTable freqTable = new FrequencyTable(initFreqs);
		CodeTree code = freqTable.buildCodeTree();
		InternalNode currentNode = code.root;
		int count = 0;
		while (true) {
			int temp = in.readNoEof();
			Node nextNode;
			if      (temp == 0) nextNode = currentNode.leftChild;
			else if (temp == 1) nextNode = currentNode.rightChild;
			else throw new AssertionError();
			
			if (nextNode instanceof Leaf) {
				int symbol = ((Leaf)nextNode).symbol;
				if (symbol == 256)  // EOF symbol
					break;
				out.write(symbol);
				
				freqTable.increment(symbol);
				count++;
				if (count % 65536 == 0) {  // Update code tree
					code = freqTable.buildCodeTree();
					freqTable = new FrequencyTable(initFreqs);
				}
				currentNode = code.root;
			} else if (nextNode instanceof InternalNode) {
				currentNode = (InternalNode)nextNode;
			} else {
				throw new AssertionError();
			}
		}
	}
	
}
