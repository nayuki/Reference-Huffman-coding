# 
# Reference Huffman coding
# Copyright (c) Project Nayuki
# 
# https://www.nayuki.io/page/reference-huffman-coding
# https://github.com/nayuki/Reference-Huffman-coding
# 

import heapq, sys
python3 = sys.version_info.major >= 3



# ---- Huffman coding core classes ----

# Encodes symbols and writes to a Huffman-coded bit stream.
class HuffmanEncoder(object):
	
	# Constructs a Huffman encoder based on the given bit output stream.
	def __init__(self, bitout):
		# The underlying bit output stream
		self.output = bitout
		# The code tree to use in the next write() operation. Must be given a suitable value
		# value before calling write(). The tree can be changed after each symbol encoded, as long
		# as the encoder and decoder have the same tree at the same point in the code stream.
		self.codetree = None
	
	
	# Encodes the given symbol and writes to the Huffman-coded output stream.
	def write(self, symbol):
		if not isinstance(self.codetree, CodeTree):
			raise ValueError("Invalid current code tree")
		bits = self.codetree.get_code(symbol)
		for b in bits:
			self.output.write(b)



# Reads from a Huffman-coded bit stream and decodes symbols.
class HuffmanDecoder(object):
	
	# Constructs a Huffman decoder based on the given bit input stream.
	def __init__(self, bitin):
		# The underlying bit input stream
		self.input = bitin
		# The code tree to use in the next read() operation. Must be given a suitable value
		# value before calling read(). The tree can be changed after each symbol decoded, as long
		# as the encoder and decoder have the same tree at the same point in the code stream.
		self.codetree = None
	
	
	# Reads from the input stream to decode the next Huffman-coded symbol.
	def read(self):
		if not isinstance(self.codetree, CodeTree):
			raise ValueError("Invalid current code tree")
		currentnode = self.codetree.root
		while True:
			temp = self.input.read_no_eof()
			if   temp == 0: nextnode = currentnode.leftchild
			elif temp == 1: nextnode = currentnode.rightchild
			else: raise AssertionError("Invalid value from read_no_eof()")
			
			if isinstance(nextnode, Leaf):
				return nextnode.symbol
			elif isinstance(nextnode, InternalNode):
				currentnode = nextnode
			else:
				raise AssertionError("Illegal node type")



# A table of symbol frequencies. Symbols are numbered from 0 to symbolLimit-1.
class FrequencyTable(object):
	
	# Constructs a frequency table from the given sequence of frequencies.
	# The sequence length must be at least 2, and each value must be non-negative.
	def __init__(self, freqs):
		self.frequencies = list(freqs)  # Make a copy
		if len(self.frequencies) < 2:
			raise ValueError("At least 2 symbols needed")
		for x in self.frequencies:
			if x < 0:
				raise ValueError("Negative frequency")
	
	
	# Returns the number of symbols in this frequency table. The result is always at least 2.
	def get_symbol_limit(self):
		return len(self.frequencies)
	
	
	# Returns the frequency of the given symbol in this frequency table. The result is always non-negative.
	def get(self, symbol):
		self._check_symbol(symbol)
		return self.frequencies[symbol]
	
	
	# Sets the frequency of the given symbol in this frequency table to the given value.
	def set(self, symbol, freq):
		self._check_symbol(symbol)
		if freq < 0:
			raise ValueError("Negative frequency")
		self.frequencies[symbol] = freq
	
	
	# Increments the frequency of the given symbol in this frequency table.
	def increment(self, symbol):
		self._check_symbol(symbol)
		self.frequencies[symbol] += 1
	
	
	# Returns silently if 0 <= symbol < len(frequencies), otherwise raises an exception.
	def _check_symbol(self, symbol):
		if 0 <= symbol < len(self.frequencies):
			return
		else:
			raise ValueError("Symbol out of range")
	
	
	# Returns a string representation of this frequency table,
	# useful for debugging only, and the format is subject to change.
	def __str__(self):
		result = ""
		for (i, freq) in enumerate(self.frequencies):
			result += "{}\t{}\n".format(i, freq)
		return result
	
	
	# Returns a code tree that is optimal for the symbol frequencies in this table.
	# The tree always contains at least 2 leaves (even if they come from symbols with
	# 0 frequency), to avoid degenerate trees. Note that optimal trees are not unique.
	def build_code_tree(self):
		# Note that if two nodes have the same frequency, then the tie is broken
		# by which tree contains the lowest symbol. Thus the algorithm has a
		# deterministic output and does not rely on the queue to break ties.
		pqueue = []
		
		# Add leaves for symbols with non-zero frequency
		for (i, freq) in enumerate(self.frequencies):
			if freq > 0:
				heapq.heappush(pqueue, (freq, i, Leaf(i)))
		
		# Pad with zero-frequency symbols until queue has at least 2 items
		for (i, freq) in enumerate(self.frequencies):
			if len(pqueue) >= 2:
				break
			if freq == 0:
				heapq.heappush(pqueue, (freq, i, Leaf(i)))
		assert len(pqueue) >= 2
		
		# Repeatedly tie together two nodes with the lowest frequency
		while len(pqueue) > 1:
			x = heapq.heappop(pqueue)  # Tuple of (frequency, lowest symbol, node object)
			y = heapq.heappop(pqueue)  # Tuple of (frequency, lowest symbol, node object)
			z = (x[0] + y[0], min(x[1], y[1]), InternalNode(x[2], y[2]))  # Construct new tuple
			heapq.heappush(pqueue, z)
		
		# Return the remaining node
		return CodeTree(pqueue[0][2], len(self.frequencies))



# A binary tree that represents a mapping between symbols and binary strings. Each encodable
# symbol is represented as a leaf node, and the path from the root to a leaf represents
# the binary string associated with the symbol.
class CodeTree(object):
	
	# Constructs a code tree from the given tree of nodes and given symbol limit.
	# Each symbol in the tree must have value strictly less than the symbol limit.
	def __init__(self, root, symbollimit):
		# Recursive helper function
		def build_code_list(node, prefix):
			if isinstance(node, InternalNode):
				build_code_list(node.leftchild , prefix + (0,))
				build_code_list(node.rightchild, prefix + (1,))
			elif isinstance(node, Leaf):
				if node.symbol >= symbollimit:
					raise ValueError("Symbol exceeds symbol limit")
				if self.codes[node.symbol] is not None:
					raise ValueError("Symbol has more than one code")
				self.codes[node.symbol] = prefix
			else:
				raise AssertionError("Illegal node type")
		
		# The root node of this code tree
		self.root = root
		# Stores the code for each symbol, or None if the symbol has no code.
		# For example, if symbol 5 has code 10011, then codes[5] is the tuple (1,0,0,1,1).
		self.codes = [None] * symbollimit
		build_code_list(root, ())  # Fill 'codes' with appropriate data
	
	
	# Returns the Huffman code for the given symbol, which is a sequence of 0s and 1s.
	def get_code(self, symbol):
		if symbol < 0:
			raise ValueError("Illegal symbol")
		elif self.codes[symbol] is None:
			raise ValueError("No code for given symbol")
		else:
			return self.codes[symbol]
	
	
	# Returns a string representation of this code tree,
	# useful for debugging only, and the format is subject to change.
	def __str__(self):
		# Recursive helper function
		def to_str(prefix, node):
			if isinstance(node, InternalNode):
				return to_str(prefix + "0", node.leftchild) + to_str(prefix + "0", node.rightchild)
			elif isinstance(node, Leaf):
				return "Code {}: Symbol {}\n".format(prefix, node.symbol)
			else:
				raise AssertionError("Illegal node type")
		
		return to_str("", self.root)



# A node in a code tree. This class has exactly two subclasses: InternalNode, Leaf.
class Node(object):
	pass


# An internal node in a code tree. It has two nodes as children.
class InternalNode(Node):
	def __init__(self, left, right):
		if not isinstance(left, Node) or not isinstance(right, Node):
			raise TypeError()
		self.leftchild = left
		self.rightchild = right


# A leaf node in a code tree. It has a symbol value.
class Leaf(Node):
	def __init__(self, sym):
		if sym < 0:
			raise ValueError("Symbol value must be non-negative")
		self.symbol = sym



# A canonical Huffman code only describes the code length of each symbol. The codes can
# be reconstructed from this information. In this implementation, symbols with lower code
# lengths, breaking ties by lower symbols, are assigned lexicographically lower codes.
class CanonicalCode(object):
	
	# Constructs a canonical code in one of two ways:
	# - CanonicalCode(codelengths): Directly uses the given sequence of code lengths.
	# - CanonicalCode(tree, symbollimit): Builds a canonical code from the given code tree.
	def __init__(self, codelengths=None, tree=None, symbollimit=None):
		if codelengths is not None and tree is None and symbollimit is None:
			# Check basic validity
			if len(codelengths) < 2:
				raise ValueError("At least 2 symbols needed")
			for cl in codelengths:
				if cl < 0:
					raise ValueError("Illegal code length")
			
			# Copy once and check for tree validity
			codelens = sorted(codelengths, reverse=True)
			currentlevel = codelens[0]
			numnodesatlevel = 0
			for cl in codelens:
				if cl == 0:
					break
				while cl < currentlevel:
					if numnodesatlevel % 2 != 0:
						raise ValueError("Under-full Huffman code tree")
					numnodesatlevel //= 2
					currentlevel -= 1
				numnodesatlevel += 1
			while cl < currentlevel:
				if numnodesatlevel % 2 != 0:
					raise ValueError("Under-full Huffman code tree")
				numnodesatlevel //= 2
				currentlevel -= 1
			if numnodesatlevel < 1:
				raise ValueError("Under-full Huffman code tree")
			if numnodesatlevel > 1:
				raise ValueError("Over-full Huffman code tree")
			
			# Copy again
			self.codelengths = list(codelengths)
		
		elif tree is not None and symbollimit is not None and codelengths is None:
			# Recursive helper method
			def build_code_lengths(node, depth):
				if isinstance(node, InternalNode):
					build_code_lengths(node.leftchild , depth + 1)
					build_code_lengths(node.rightchild, depth + 1)
				elif isinstance(node, Leaf):
					# Note: CodeTree already has a checked constraint that disallows a symbol in multiple leaves
					if self.codelengths[node.symbol] != 0:
						raise AssertionError("Symbol has more than one code")
					if node.symbol >= len(self.codelengths):
						raise ValueError("Symbol exceeds symbol limit")
					self.codelengths[node.symbol] = depth
				else:
					raise AssertionError("Illegal node type")
			
			self.codelengths = [0] * symbollimit
			build_code_lengths(tree.root, 0)
		
		else:
			raise ValueError("Invalid arguments")
	
	
	def get_symbol_limit(self):
		return len(self.codelengths)
	
	
	def get_code_length(self, symbol):
		if 0 <= symbol < len(self.codelengths):
			return self.codelengths[symbol]
		else:
			raise ValueError("Symbol out of range")
	
	
	def to_code_tree(self):
		nodes = []
		for i in range(max(self.codelengths), 0, -1):  # Descend through positive code lengths
			newnodes = []
			
			# Add leaves for symbols with code length i
			for (j, codelen) in enumerate(self.codelengths):
				if codelen == i:
					newnodes.append(Leaf(j))
			
			# Merge pairs of nodes from the previous deeper layer
			for j in range(0, len(nodes), 2):
				newnodes.append(InternalNode(nodes[j], nodes[j + 1]))
			
			nodes = newnodes
			if len(nodes) % 2 != 0:
				raise ValueError("This canonical code does not represent a Huffman code tree")
		
		if len(nodes) != 2:
			raise ValueError("This canonical code does not represent a Huffman code tree")
		return CodeTree(InternalNode(nodes[0], nodes[1]), len(self.codelengths))



# ---- Bit-oriented I/O streams ----

# A stream of bits that can be read. Because they come from an underlying byte stream,
# the total number of bits is always a multiple of 8. The bits are read in big endian.
class BitInputStream(object):
	
	# Constructs a bit input stream based on the given byte input stream.
	def __init__(self, inp):
		# The underlying byte stream to read from
		self.input = inp
		# Either in the range [0x00, 0xFF] if bits are available, or -1 if end of stream is reached
		self.currentbyte = 0
		# Number of remaining bits in the current byte, always between 0 and 7 (inclusive)
		self.numbitsremaining = 0
	
	
	# Reads a bit from this stream. Returns 0 or 1 if a bit is available, or -1 if
	# the end of stream is reached. The end of stream always occurs on a byte boundary.
	def read(self):
		if self.currentbyte == -1:
			return -1
		if self.numbitsremaining == 0:
			temp = self.input.read(1)
			if len(temp) == 0:
				self.currentbyte = -1
				return -1
			self.currentbyte = temp[0] if python3 else ord(temp)
			self.numbitsremaining = 8
		assert self.numbitsremaining > 0
		self.numbitsremaining -= 1
		return (self.currentbyte >> self.numbitsremaining) & 1
	
	
	# Reads a bit from this stream. Returns 0 or 1 if a bit is available, or raises an EOFError
	# if the end of stream is reached. The end of stream always occurs on a byte boundary.
	def read_no_eof(self):
		result = self.read()
		if result != -1:
			return result
		else:
			raise EOFError()
	
	
	# Closes this stream and the underlying input stream.
	def close(self):
		self.input.close()
		self.currentbyte = -1
		self.numbitsremaining = 0



# A stream where bits can be written to. Because they are written to an underlying
# byte stream, the end of the stream is padded with 0's up to a multiple of 8 bits.
# The bits are written in big endian.
class BitOutputStream(object):
	
	# Constructs a bit output stream based on the given byte output stream.
	def __init__(self, out):
		self.output = out  # The underlying byte stream to write to
		self.currentbyte = 0  # The accumulated bits for the current byte, always in the range [0x00, 0xFF]
		self.numbitsfilled = 0  # Number of accumulated bits in the current byte, always between 0 and 7 (inclusive)
	
	
	# Writes a bit to the stream. The given bit must be 0 or 1.
	def write(self, b):
		if b not in (0, 1):
			raise ValueError("Argument must be 0 or 1")
		self.currentbyte = (self.currentbyte << 1) | b
		self.numbitsfilled += 1
		if self.numbitsfilled == 8:
			towrite = bytes((self.currentbyte,)) if python3 else chr(self.currentbyte)
			self.output.write(towrite)
			self.currentbyte = 0
			self.numbitsfilled = 0
	
	
	# Closes this stream and the underlying output stream. If called when this
	# bit stream is not at a byte boundary, then the minimum number of "0" bits
	# (between 0 and 7 of them) are written as padding to reach the next byte boundary.
	def close(self):
		while self.numbitsfilled != 0:
			self.write(0)
		self.output.close()
