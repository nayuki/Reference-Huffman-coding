/*
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 *
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */

import java.util.Objects;


/**
 * An internal node in a code tree. It has two nodes as children. Immutable.
 *
 * @author nayuki, hattoemi
 * @see CodeTree
 */
public final class InternalNode extends Node {

    /**
     * Not null
     */
    public final Node leftChild;

    /**
     * Not null
     */
    public final Node rightChild;


    public InternalNode(Node left, Node right) {
        leftChild = Objects.requireNonNull(left);
        rightChild = Objects.requireNonNull(right);
    }

}
