/*
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 *
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */

import java.io.*;
import java.util.Arrays;


/**
 * Compression application using adaptive Huffman coding.
 * <p>Usage: java AdaptiveHuffmanCompress InputFile OutputFile</p>
 * <p>Then use the corresponding "AdaptiveHuffmanDecompress" application to recreate the original input file.</p>
 * <p>Note that the application starts with a flat frequency table of 257 symbols (all set to a frequency of 1),
 * collects statistics while bytes are being encoded, and regenerates the Huffman code periodically. The
 * corresponding decompressor program also starts with a flat frequency table, updates it while bytes are being
 * decoded, and regenerates the Huffman code periodically at the exact same points in time. It is by design that
 * the compressor and decompressor have synchronized states, so that the data can be decompressed properly.</p>
 *
 * @author nayuki, hattoemi
 */
public final class AdaptiveHuffmanCompress {

    static final int VIRTUAL_VALUE = 2;

    /**
     * Command line main application function.
     * hattoemi: C language style,Java's implementation
     */
    public static void main(String[] args) throws IOException {
        // Handle command line arguments
        if (args.length != VIRTUAL_VALUE) {
            System.err.println("Usage: java AdaptiveHuffmanCompress InputFile OutputFile");
            System.exit(1);
            return;
        }
        File inputFile = new File(args[0]);
        File outputFile = new File(args[1]);

        // Perform file compression
        try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile))) {
            try (BitOutputStream out = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)))) {
                compress(in, out);
            }
        }
    }


    /**
     * To allow unit testing, this method is package-private instead of private.
     */
    static void compress(InputStream in, BitOutputStream out) throws IOException {
        int[] initFrequency = new int[257];
        Arrays.fill(initFrequency, 1);

        FrequencyTable frequency = new FrequencyTable(initFrequency);
        HuffmanEncoder enc = new HuffmanEncoder(out);
        // Don't need to make canonical code because we don't transmit the code tree
        enc.codeTree = frequency.buildCodeTree();
        // Number of bytes read from the input file
        int count = 0;
        while (true) {
            // Read and encode one byte
            int symbol = in.read();
            if (symbol == -1) {
                break;
            }
            enc.write(symbol);
            count++;

            // Update the frequency table and possibly the code tree
            frequency.increment(symbol);
            /* Update code tree
             * hattoemi: Don't make complex logical decisions in conditional judgments
             * 262144 = 2^18
             */
            final boolean inLimit = (count < 262144 && isPowerOf2(count) || count % 262144 == 0);
            if (inLimit) {
                enc.codeTree = frequency.buildCodeTree();
            }
            // Reset frequency table
            if (count % 262144 == 0) {
                frequency = new FrequencyTable(initFrequency);
            }
        }
        // EOF
        enc.write(256);
    }


    private static boolean isPowerOf2(int x) {
        return x > 0 && Integer.bitCount(x) == 1;
    }

}
