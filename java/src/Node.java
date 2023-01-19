/* 
 * Reference Huffman coding
 * 
 * Copyright (c) Project Nayuki
 * MIT License. See readme file.
 * https://www.nayuki.io/page/reference-huffman-coding
 */


/**
 * A node in a code tree. This class has exactly two subclasses: InternalNode, Leaf.
 * @see CodeTree
 */
public abstract class Node {
	
	// This constructor is package-private to prevent accidental subclassing outside of this package.
	Node() {}
	
}
