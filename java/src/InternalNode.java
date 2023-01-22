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
public record InternalNode(Node leftChild, Node rightChild) implements Node {
	
	public InternalNode {
		leftChild = Objects.requireNonNull(leftChild);
		rightChild = Objects.requireNonNull(rightChild);
	}
	
}
