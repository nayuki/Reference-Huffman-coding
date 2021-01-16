/*
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 *
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */


/**
 * A leaf node in a code tree. It has a symbol value. Immutable.
 *
 * @author nayuki, hattoemi
 * @see CodeTree
 */
public final class Leaf extends Node {

    /**
     * Always non-negative
     */
    public final int symbol;


    public Leaf(int sym) {
        if (sym < 0) {
            throw new IllegalArgumentException("Symbol value must be non-negative");
        }
        symbol = sym;
    }

}
