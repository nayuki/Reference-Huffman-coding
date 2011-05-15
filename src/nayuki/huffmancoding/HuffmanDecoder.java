package nayuki.huffmancoding;

import java.io.IOException;


public final class HuffmanDecoder {
	
	private BitInputStream input;
	
	public CodeTree codeTree;
	
	
	
	public HuffmanDecoder(BitInputStream in) {
		input = in;
	}
	
	
	
	public int read() throws IOException {
		if (codeTree == null)
			throw new NullPointerException("Code tree is null");
		
		InternalNode currentNode = codeTree.root;
		while (true) {
			int temp = input.readNoEof();
			Node nextNode;
			if      (temp == 0) nextNode = currentNode.leftChild;
			else if (temp == 1) nextNode = currentNode.rightChild;
			else throw new AssertionError();
			
			if (nextNode instanceof Leaf)
				return ((Leaf)nextNode).symbol;
			else if (nextNode instanceof InternalNode)
				currentNode = (InternalNode)nextNode;
			else
				throw new AssertionError();
		}
	}
	
}
