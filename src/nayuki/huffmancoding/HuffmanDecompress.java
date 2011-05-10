package nayuki.huffmancoding;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;


public class HuffmanDecompress {
	
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.err.println("Usage: java HuffmanDecompress [inputFile] [outputFile]");
			System.exit(1);
		}
		
		File inputFile = new File(args[0]);
		File outputFile = new File(args[1]);
		BitInputStream in = new BitInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
		OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile));
		try {
			CodeTree code = readCodebook(in);
			decompress(in, out, code);
		} finally {
			out.close();
			in.close();
		}
	}
	
	
	private static CodeTree readCodebook(BitInputStream in) throws IOException {
		int[] codeLengths = new int[257];
		Arrays.fill(codeLengths, -1);
		
		for (int i = 0; i < codeLengths.length; i++) {
			// For this file format, we read 8 bits in big endian
			int val = 0;
			for (int j = 0; j < 8; j++) {
				int temp = in.read();
				if (temp == -1)
					throw new EOFException("Unexpected end of stream");
				val = val << 1 | temp;
			}
			codeLengths[i] = val;
		}
		
		return new CanonicalCode(codeLengths).toCodeTree();
	}
	
	
	private static void decompress(BitInputStream in, OutputStream out, CodeTree code) throws IOException {
		InternalNode currentNode = (InternalNode)code.root;
		while (true) {
			int temp = in.read();
			if (temp == -1)
				throw new EOFException("Unexpected end of stream");
			
			Node nextNode;
			if (temp == 0)
				nextNode = currentNode.leftChild;
			else if (temp == 1)
				nextNode = currentNode.rightChild;
			else
				throw new AssertionError();
			
			if (nextNode instanceof Leaf) {
				int symbol = ((Leaf)nextNode).symbol;
				if (symbol == 256)  // EOF symbol
					break;
				out.write(symbol);
				currentNode = (InternalNode)code.root;
			} else {
				currentNode = (InternalNode)nextNode;
			}
		}
	}
	
}
