/*
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 *
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */

import java.io.*;


/**
 * Compression application using static Huffman coding.
 * <p>Usage: java HuffmanCompress InputFile OutputFile</p>
 * <p>Then use the corresponding "HuffmanDecompress" application to recreate the original input file.</p>
 * <p>Note that the application uses an alphabet of 257 symbols - 256 symbols for the byte values
 * and 1 symbol for the EOF marker. The compressed file format starts with a list of 257
 * code lengths, treated as a canonical code, and then followed by the Huffman-coded data.</p>
 */
public final class HuffmanCompress {

    // Command line main application function.
    public static void main(String[] args) throws IOException {
        // Handle command line arguments
        if (args.length != 2) {
            System.err.println("Usage: java HuffmanCompress InputFile OutputFile");
            System.exit(1);
            return;
        }
        File inputFile = new File(args[0]);
        File outputFile = new File(args[1]);

        // Read input file once to compute symbol frequencies.
        // The resulting generated code is optimal for static Huffman coding and also canonical.
        FrequencyTable freqs = getFrequencies(inputFile);
        freqs.increment(256);  // EOF symbol gets a frequency of 1
        CodeTree code = freqs.buildCodeTree();
        CanonicalCode canonCode = new CanonicalCode(code, freqs.getSymbolLimit());
        // Replace code tree with canonical one. For each symbol,
        // the code value may change but the code length stays the same.
        code = canonCode.toCodeTree();

        // Read input file again, compress with Huffman coding, and write output file
        try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile))) {
            try (BitOutputStream out = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)))) {
                writeCodeLengthTable(out, canonCode);
                compress(code, in, out);
            }
        }
    }


    // Returns a frequency table based on the bytes in the given file.
    // Also contains an extra entry for symbol 256, whose frequency is set to 0.
    private static FrequencyTable getFrequencies(File file) throws IOException {
        FrequencyTable freqs = new FrequencyTable(new int[257]);
        try (InputStream input = new BufferedInputStream(new FileInputStream(file))) {
            while (true) {
                int b = input.read();
                if (b == -1) {
                    break;
                }
                freqs.increment(b);
            }
        }
        return freqs;
    }


    // To allow unit testing, this method is package-private instead of private.
    static void writeCodeLengthTable(BitOutputStream out, CanonicalCode canonCode) throws IOException {
        for (int i = 0; i < canonCode.getSymbolLimit(); i++) {
            int val = canonCode.getCodeLength(i);
            // For this file format, we only support codes up to 255 bits long
            if (val >= 256) {
                throw new RuntimeException("The code for a symbol is too long");
            }

            // Write value as 8 bits in big endian
            for (int j = 7; j >= 0; j--) {
                out.write((val >>> j) & 1);
            }
        }
    }


    // To allow unit testing, this method is package-private instead of private.
    static void compress(CodeTree code, InputStream in, BitOutputStream out) throws IOException {
        HuffmanEncoder enc = new HuffmanEncoder(out);
        enc.codeTree = code;
        while (true) {
            int b = in.read();
            if (b == -1) {
                break;
            }
            enc.write(b);
        }
        enc.write(256);  // EOF
    }

}
