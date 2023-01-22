/* 
 * Reference Huffman coding
 * 
 * Copyright (c) Project Nayuki
 * MIT License. See readme file.
 * https://www.nayuki.io/page/reference-huffman-coding
 */

import java.util.Objects;


/**
 * An internal node in a code tree. It has two nodes as children. Immutable.
 * @see CodeTree
 */
public final class InternalNode implements Node {
	
	public final Node leftChild;  // Not null
	
	public final Node rightChild;  // Not null
	
	
	
	public InternalNode(Node left, Node right) {
		leftChild = Objects.requireNonNull(left);
		rightChild = Objects.requireNonNull(right);
	}
	
}
