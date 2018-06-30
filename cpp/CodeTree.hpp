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
#include <vector>


/* 
 * A node in a code tree. This class has exactly two subclasses: InternalNode, Leaf.
 */
class Node {
	
	public: virtual ~Node() = 0;
		
};



/* 
 * A leaf node in a code tree. It has a symbol value.
 */
class Leaf final : public Node {
	
	public: std::uint32_t symbol;
	
	
	public: Leaf(std::uint32_t sym);
	
};



/* 
 * An internal node in a code tree. It has two nodes as children.
 */
class InternalNode final : public Node {
	
	public: std::unique_ptr<Node> leftChild;  // Not null
	
	public: std::unique_ptr<Node> rightChild;  // Not null
	
	
	public: InternalNode(std::unique_ptr<Node> &&left, std::unique_ptr<Node> &&right);
	
};



/* 
 * A binary tree that represents a mapping between symbols and binary strings.
 * The data structure is immutable. There are two main uses of a code tree:
 * - Read the root field and walk through the tree to extract the desired information.
 * - Call getCode() to get the binary code for a particular encodable symbol.
 * The path to a leaf node determines the leaf's symbol's code. Starting from the root, going
 * to the left child represents a 0, and going to the right child represents a 1. Constraints:
 * - The root must be an internal node, and the tree is finite.
 * - No symbol value is found in more than one leaf.
 * - Not every possible symbol value needs to be in the tree.
 * Illustrated example:
 *   Huffman codes:
 *     0: Symbol A
 *     10: Symbol B
 *     110: Symbol C
 *     111: Symbol D
 *   
 *   Code tree:
 *       .
 *      / \
 *     A   .
 *        / \
 *       B   .
 *          / \
 *         C   D
 */
class CodeTree final {
	
	/*---- Fields ----*/
	
	public: std::unique_ptr<InternalNode> root;
	
	
	// Stores the code for each symbol, or null if the symbol has no code.
	// For example, if symbol 5 has code 10011, then codes.get(5) is the list [1,0,0,1,1].
	private: std::vector<std::vector<char> > codes;
	
	
	/*---- Constructor ----*/
	
	// Constructs a code tree from the given tree of nodes and given symbol limit.
	// Each symbol in the tree must have value strictly less than the symbol limit.
	public: CodeTree(std::unique_ptr<InternalNode> &&rt, std::uint32_t symbolLimit);
	
	
	/*---- Methods ----*/
	
	// Recursive helper function for the constructor
	private: void buildCodeList(const Node *node, std::vector<char> &prefix);
	
	
	// Returns the Huffman code for the given symbol, which is a list of 0s and 1s.
	public: const std::vector<char> &getCode(std::uint32_t symbol) const;
	
};
