/* 
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */


/**
 * An internal node in a code tree. It has two nodes as children. Immutable.
 */
public final class InternalNode extends Node {
	
	public final Node leftChild;  // Not null
	
	public final Node rightChild;  // Not null
	
	
	
	public InternalNode(Node leftChild, Node rightChild) {
		if (leftChild == null || rightChild == null)
			throw new NullPointerException("Argument is null");
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}
	
}
