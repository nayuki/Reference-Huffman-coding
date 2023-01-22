/* 
 * Reference Huffman coding
 * 
 * Copyright (c) Project Nayuki
 * MIT License. See readme file.
 * https://www.nayuki.io/page/reference-huffman-coding
 */


/**
 * A node in a code tree.
 * @see CodeTree
 */
public abstract sealed class Node permits InternalNode, Leaf {}
