/* 
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */

#pragma once

#include <cstdint>
#include <memory>
#include <queue>
#include <vector>
#include "CodeTree.hpp"


/* 
 * A table of symbol frequencies. Symbols values are numbered from 0 to symbolLimit-1.
 * A frequency table is mainly used like this:
 * 0. Collect the frequencies of symbols in the stream that we want to compress.
 * 1. Build a code tree that is statically optimal for the current frequencies.
 * This implementation is designed to avoid arithmetic overflow - it correctly
 * builds an optimal code tree for any legal number of symbols (2 to UINT32_MAX),
 * with each symbol having a legal frequency (0 to UINT32_MAX).
 */
class FrequencyTable final {
	
	/*---- Fields and constructors ----*/
	
	// Length at least 2, and every element is non-negative
	private: std::vector<std::uint32_t> frequencies;
	
	
	// Constructs a frequency table from the given array of frequencies.
	// The array length must be at least 2, and each value must be non-negative.
	public: FrequencyTable(const std::vector<std::uint32_t> &freqs);
	
	
	
	/*---- Basic methods ----*/
	
	// Returns the number of symbols in this frequency table. The result is always at least 2.
	public: std::uint32_t getSymbolLimit() const;
	
	
	// Returns the frequency of the given symbol in this frequency table.
	public: std::uint32_t get(std::uint32_t symbol) const;
	
	
	// Sets the frequency of the given symbol in this frequency table to the given value.
	public: void set(std::uint32_t symbol, std::uint32_t freq);
	
	
	// Increments the frequency of the given symbol in this frequency table.
	public: void increment(std::uint32_t symbol);
	
	
	
	/*---- Advanced methods ----*/
	
	// Returns a code tree that is optimal for the symbol frequencies in this table.
	// The tree always contains at least 2 leaves (even if they come from symbols with
	// 0 frequency), to avoid degenerate trees. Note that optimal trees are not unique.
	public: CodeTree buildCodeTree() const;
	
	
	// Helper structure for buildCodeTree()
	private: class NodeWithFrequency {
		
		public: std::unique_ptr<Node> node;
		public: std::uint32_t lowestSymbol;
		public: std::uint64_t frequency;  // Using wider type prevents overflow
		
		
		public: NodeWithFrequency(Node *nd, std::uint32_t lowSym, std::uint64_t freq);
		
		
		// Sort by ascending frequency, breaking ties by ascending symbol value.
		public: bool operator<(const NodeWithFrequency &other) const;
		
	};
	
	
	private: static NodeWithFrequency popQueue(std::priority_queue<NodeWithFrequency> &pqueue);
	
};
