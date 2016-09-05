# 
# Compression application using static Huffman coding
# 
# Usage: python huffman-compress.py InputFile OutputFile
# Then use the corresponding huffman-decompress.py application to recreate the original input file.
# Note that the application uses an alphabet of 257 symbols - 256 symbols for the byte values
# and 1 symbol for the EOF marker. The compressed file format starts with a list of 257
# code lengths, treated as a canonical code, and then followed by the Huffman-coded data.
# 
# Copyright (c) Project Nayuki
# 
# https://www.nayuki.io/page/reference-huffman-coding
# https://github.com/nayuki/Reference-Huffman-coding
# 

import sys
import huffmancoding
python3 = sys.version_info.major >= 3


# Command line main application function.
def main(args):
	# Handle command line arguments
	if len(args) != 2:
		sys.exit("Usage: python huffman-compress.py InputFile OutputFile")
	inputfile  = args[0]
	outputfile = args[1]
	
	# Read input file once to compute symbol frequencies.
	# The resulting generated code is optimal for static Huffman coding and also canonical.
	freqs = get_frequencies(inputfile)
	freqs.increment(256)  # EOF symbol gets a frequency of 1
	code = freqs.build_code_tree()
	canoncode = huffmancoding.CanonicalCode(tree=code, symbollimit=257)
	# Replace code tree with canonical one. For each symbol,
	# the code value may change but the code length stays the same.
	code = canoncode.to_code_tree()
	
	# Read input file again, compress with Huffman coding, and write output file
	inp = open(inputfile, "rb")
	bitout = huffmancoding.BitOutputStream(open(outputfile, "wb"))
	try:
		write_code_len_table(bitout, canoncode)
		compress(code, inp, bitout)
	finally:
		bitout.close()
		inp.close()


# Returns a frequency table based on the bytes in the given file.
# Also contains an extra entry for symbol 256, whose frequency is set to 0.
def get_frequencies(filepath):
	freqs = huffmancoding.FrequencyTable([0] * 257)
	with open(filepath, "rb") as input:
		while True:
			b = input.read(1)
			if len(b) == 0:
				break
			b = b[0] if python3 else ord(b)
			freqs.increment(b)
	return freqs


def write_code_len_table(bitout, canoncode):
	for i in range(canoncode.get_symbol_limit()):
		val = canoncode.get_code_length(i)
		# For this file format, we only support codes up to 255 bits long
		if val >= 256:
			raise ValueError("The code for a symbol is too long")
		
		# Write value as 8 bits in big endian
		for j in reversed(range(8)):
			bitout.write((val >> j) & 1)


def compress(code, inp, bitout):
	enc = huffmancoding.HuffmanEncoder(bitout)
	enc.codetree = code
	while True:
		b = inp.read(1)
		if len(b) == 0:
			break
		enc.write(b[0] if python3 else ord(b))
	enc.write(256)  # EOF


# Main launcher
if __name__ == "__main__":
	main(sys.argv[1 : ])
