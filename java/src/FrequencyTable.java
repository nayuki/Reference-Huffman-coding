/*
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 *
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */

import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;


/**
 * A table of symbol frequencies. Mutable and not thread-safe. Symbols values are
 * numbered from 0 to symbolLimit&minus;1. A frequency table is mainly used like this:
 * <ol>
 *   <li>Collect the frequencies of symbols in the stream that we want to compress.</li>
 *   <li>Build a code tree that is statically optimal for the current frequencies.</li>
 * </ol>
 * <p>This implementation is designed to avoid arithmetic overflow - it correctly builds
 * an optimal code tree for any legal number of symbols (2 to {@code Integer.MAX_VALUE}),
 * with each symbol having a legal frequency (0 to {@code Integer.MAX_VALUE}).</p>
 *
 * @see CodeTree
 */
public final class FrequencyTable {

    /*---- Field and constructor ----*/

    // Length at least 2, and every element is non-negative.
    private final int[] frequencies;


    /**
     * Constructs a frequency table from the specified array of frequencies.
     * The array length must be at least 2, and each value must be non-negative.
     *
     * @param freqs the array of frequencies
     * @throws NullPointerException     if the array is {@code null}
     * @throws IllegalArgumentException if the array length is less than 2 or any value is negative
     */
    public FrequencyTable(int[] freqs) {
        Objects.requireNonNull(freqs);
        if (freqs.length < 2) {
            throw new IllegalArgumentException("At least 2 symbols needed");
        }
        frequencies = freqs.clone();  // Defensive copy
        for (int x : frequencies) {
            if (x < 0) {
                throw new IllegalArgumentException("Negative frequency");
            }
        }
    }



    /*---- Basic methods ----*/

    /**
     * Returns the number of symbols in this frequency table. The result is always at least 2.
     *
     * @return the number of symbols in this frequency table
     */
    public int getSymbolLimit() {
        return frequencies.length;
    }


    /**
     * Returns the frequency of the specified symbol in this frequency table. The result is always non-negative.
     *
     * @param symbol the symbol to query
     * @return the frequency of the specified symbol
     * @throws IllegalArgumentException if the symbol is out of range
     */
    public int get(int symbol) {
        checkSymbol(symbol);
        return frequencies[symbol];
    }


    /**
     * Sets the frequency of the specified symbol in this frequency table to the specified value.
     *
     * @param symbol the symbol whose frequency will be modified
     * @param freq   the frequency to set it to, which must be non-negative
     * @throws IllegalArgumentException if the symbol is out of range or the frequency is negative
     */
    public void set(int symbol, int freq) {
        checkSymbol(symbol);
        if (freq < 0) {
            throw new IllegalArgumentException("Negative frequency");
        }
        frequencies[symbol] = freq;
    }


    /**
     * Increments the frequency of the specified symbol in this frequency table.
     *
     * @param symbol the symbol whose frequency will be incremented
     * @throws IllegalArgumentException if the symbol is out of range
     * @throws IllegalStateException    if the symbol already has
     *                                  the maximum allowed frequency of {@code Integer.MAX_VALUE}
     */
    public void increment(int symbol) {
        checkSymbol(symbol);
        if (frequencies[symbol] == Integer.MAX_VALUE) {
            throw new IllegalStateException("Maximum frequency reached");
        }
        frequencies[symbol]++;
    }


    // Returns silently if 0 <= symbol < frequencies.length, otherwise throws an exception.
    private void checkSymbol(int symbol) {
        if (symbol < 0 || symbol >= frequencies.length) {
            throw new IllegalArgumentException("Symbol out of range");
        }
    }


    /**
     * Returns a string representation of this frequency table,
     * useful for debugging only, and the format is subject to change.
     *
     * @return a string representation of this frequency table
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < frequencies.length; i++) {
            sb.append(String.format("%d\t%d%n", i, frequencies[i]));
        }
        return sb.toString();
    }



    /*---- Advanced methods ----*/

    /**
     * Returns a code tree that is optimal for the symbol frequencies in this table.
     * The tree always contains at least 2 leaves (even if they come from symbols with
     * 0 frequency), to avoid degenerate trees. Note that optimal trees are not unique.
     *
     * @return an optimal code tree for this frequency table
     */
    public CodeTree buildCodeTree() {
        // Note that if two nodes have the same frequency, then the tie is broken
        // by which tree contains the lowest symbol. Thus the algorithm has a
        // deterministic output and does not rely on the queue to break ties.
        Queue<NodeWithFrequency> pqueue = new PriorityQueue<>();

        // Add leaves for symbols with non-zero frequency
        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] > 0) {
                pqueue.add(new NodeWithFrequency(new Leaf(i), i, frequencies[i]));
            }
        }

        // Pad with zero-frequency symbols until queue has at least 2 items
        for (int i = 0; i < frequencies.length && pqueue.size() < 2; i++) {
            if (frequencies[i] == 0) {
                pqueue.add(new NodeWithFrequency(new Leaf(i), i, 0));
            }
        }
        if (pqueue.size() < 2) {
            throw new AssertionError();
        }

        // Repeatedly tie together two nodes with the lowest frequency
        while (pqueue.size() > 1) {
            NodeWithFrequency x = pqueue.remove();
            NodeWithFrequency y = pqueue.remove();
            pqueue.add(new NodeWithFrequency(
                    new InternalNode(x.node, y.node),
                    Math.min(x.lowestSymbol, y.lowestSymbol),
                    x.frequency + y.frequency));
        }

        // Return the remaining node
        return new CodeTree((InternalNode) pqueue.remove().node, frequencies.length);
    }


    // Helper structure for buildCodeTree()
    private static class NodeWithFrequency implements Comparable<NodeWithFrequency> {

        public final Node node;
        public final int lowestSymbol;
        public final long frequency;  // Using wider type prevents overflow


        public NodeWithFrequency(Node nd, int lowSym, long freq) {
            node = nd;
            lowestSymbol = lowSym;
            frequency = freq;
        }


        // Sort by ascending frequency, breaking ties by ascending symbol value.
        @Override
        public int compareTo(NodeWithFrequency other) {
            if (frequency < other.frequency) {
                return -1;
            } else if (frequency > other.frequency) {
                return 1;
            } else {
                return Integer.compare(lowestSymbol, other.lowestSymbol);
            }
        }

    }

}
