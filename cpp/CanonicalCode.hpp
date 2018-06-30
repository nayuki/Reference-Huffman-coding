/* 
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */

#pragma once

#include <cstdint>
#include <vector>
#include "CodeTree.hpp"


/* 
 * A canonical Huffman code, which only describes the code length of
 * each symbol. Immutable. Code length 0 means no code for the symbol.
 * The binary codes for each symbol can be reconstructed from the length information.
 * In this implementation, lexicographically lower binary codes are assigned to symbols
 * with lower code lengths, breaking ties by lower symbol values. For example:
 *   Code lengths (canonical code):
 *     Symbol A: 1
 *     Symbol B: 3
 *     Symbol C: 0 (no code)
 *     Symbol D: 2
 *     Symbol E: 3
 *   
 *   Sorted lengths and symbols:
 *     Symbol A: 1
 *     Symbol D: 2
 *     Symbol B: 3
 *     Symbol E: 3
 *     Symbol C: 0 (no code)
 *   
 *   Generated Huffman codes:
 *     Symbol A: 0
 *     Symbol D: 10
 *     Symbol B: 110
 *     Symbol E: 111
 *     Symbol C: None
 *   
 *   Huffman codes sorted by symbol:
 *     Symbol A: 0
 *     Symbol B: 110
 *     Symbol C: None
 *     Symbol D: 10
 *     Symbol E: 111</pre>
 */
class CanonicalCode final {
	
	/*---- Field ----*/
	
	private: std::vector<std::uint32_t> codeLengths;
	
	
	
	/*---- Constructors ----*/
	
	// Constructs a canonical Huffman code from the given array of symbol code lengths.
	// Each code length must be non-negative. Code length 0 means no code for the symbol.
	// The collection of code lengths must represent a proper full Huffman code tree.
	// Examples of code lengths that result in under-full Huffman code trees:
	// - [1]
	// - [3, 0, 3]
	// - [1, 2, 3]
	// Examples of code lengths that result in correct full Huffman code trees:
	// - [1, 1]
	// - [2, 2, 1, 0, 0, 0]
	// - [3, 3, 3, 3, 3, 3, 3, 3]
	// Examples of code lengths that result in over-full Huffman code trees:
	// - [1, 1, 1]
	// - [1, 1, 2, 2, 3, 3, 3, 3]
	public: CanonicalCode(const std::vector<std::uint32_t> &codeLens);
	
	
	// Builds a canonical Huffman code from the given code tree.
	public: CanonicalCode(const CodeTree &tree, std::uint32_t symbolLimit);
	
	
	// Recursive helper method for the above constructor.
	private: void buildCodeLengths(const Node *node, std::uint32_t depth);
	
	
	
	/*---- Various methods ----*/
	
	// Returns the symbol limit for this canonical Huffman code.
	// Thus this code covers symbol values from 0 to symbolLimit&minus;1.
	public: std::uint32_t getSymbolLimit() const;
	
	
	// Returns the code length of the given symbol value. The result is 0
	// if the symbol has node code; otherwise the result is a positive number.
	public: std::uint32_t getCodeLength(std::uint32_t symbol) const;
	
	
	// Returns the canonical code tree for this canonical Huffman code.
	public: CodeTree toCodeTree() const;
	
};
