# 
# Decompression application using adaptive Huffman coding
# 
# Usage: python adaptive-huffman-decompress.py InputFile OutputFile
# This decompresses files generated by the adaptive-huffman-compress.py application.
# 
# Copyright (c) Project Nayuki
# MIT License. See readme file.
# https://www.nayuki.io/page/reference-huffman-coding
# 

import sys
import huffmancoding


# Command line main application function.
def main(args):
	# Handle command line arguments
	if len(args) != 2:
		sys.exit("Usage: python adaptive-huffman-decompress.py InputFile OutputFile")
	inputfile, outputfile = args
	
	# Perform file decompression
	with open(inputfile, "rb") as inp, open(outputfile, "wb") as out:
		decompress(huffmancoding.BitInputStream(inp), out)


def decompress(bitin, out):
	initfreqs = [1] * 257
	freqs = huffmancoding.FrequencyTable(initfreqs)
	dec = huffmancoding.HuffmanDecoder(bitin)
	dec.codetree = freqs.build_code_tree()  # Use same algorithm as the compressor
	count = 0  # Number of bytes written to the output file
	while True:
		# Decode and write one byte
		symbol = dec.read()
		if symbol == 256:  # EOF symbol
			break
		out.write(bytes((symbol,)))
		count += 1
		
		# Update the frequency table and possibly the code tree
		freqs.increment(symbol)
		if (count < 262144 and is_power_of_2(count)) or count % 262144 == 0:  # Update code tree
			dec.codetree = freqs.build_code_tree()
		if count % 262144 == 0:  # Reset frequency table
			freqs = huffmancoding.FrequencyTable(initfreqs)


def is_power_of_2(x):
	return x > 0 and x & (x - 1) == 0


# Main launcher
if __name__ == "__main__":
	main(sys.argv[1 : ])
