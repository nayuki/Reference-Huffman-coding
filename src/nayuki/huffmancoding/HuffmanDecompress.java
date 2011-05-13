package nayuki.huffmancoding;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


// Decompresses an input file that was compressed with HuffmanCompress, to an output file.
public final class HuffmanDecompress {
	
	public static void main(String[] args) throws IOException {
		// Show what command line arguments to use
		if (args.length == 0) {
			System.err.println("Usage: java HuffmanDecompress InputFile OutputFile");
			System.exit(1);
			return;
		}
		
		// Otherwise, decompress
		File inputFile = new File(args[0]);
		File outputFile = new File(args[1]);
		
		BitInputStream in = new BitInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
		OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile));
		try {
			CanonicalCode canonCode = readCode(in);
			CodeTree code = canonCode.toCodeTree();
			decompress(code, in, out);
		} finally {
			out.close();
			in.close();
		}
	}
	
	
	private static CanonicalCode readCode(BitInputStream in) throws IOException {
		int[] codeLengths = new int[257];
		for (int i = 0; i < codeLengths.length; i++) {
			// For this file format, we read 8 bits in big endian
			int val = 0;
			for (int j = 0; j < 8; j++) {
				int temp = in.readNoEof();
				val = val << 1 | temp;
			}
			codeLengths[i] = val;
		}
		return new CanonicalCode(codeLengths);
	}
	
	
	private static void decompress(CodeTree code, BitInputStream in, OutputStream out) throws IOException {
		InternalNode currentNode = code.root;
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
				currentNode = code.root;
			} else if (nextNode instanceof InternalNode) {
				currentNode = (InternalNode)nextNode;
			} else {
				throw new AssertionError();
			}
		}
	}
	
}
