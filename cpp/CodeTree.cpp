/* 
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */

#include <utility>
#include "CodeTree.hpp"

using std::uint32_t;
using std::vector;


Node::~Node() {}


Leaf::Leaf(uint32_t sym) :
	symbol(sym) {}


InternalNode::InternalNode(std::unique_ptr<Node> &&left, std::unique_ptr<Node> &&right) :
	leftChild(std::move(left)),
	rightChild(std::move(right)) {}


CodeTree::CodeTree(InternalNode &&rt, uint32_t symbolLimit) :
		root(std::move(rt)) {
	if (symbolLimit < 2)
		throw "At least 2 symbols needed";
	if (symbolLimit > SIZE_MAX)
		throw "Too many symbols";
	codes = vector<vector<char> >(symbolLimit, vector<char>());  // Initially all empty
	vector<char> prefix;
	buildCodeList(&root, prefix);  // Fill 'codes' with appropriate data
}


void CodeTree::buildCodeList(const Node *node, vector<char> &prefix) {
	if (dynamic_cast<const InternalNode*>(node) != nullptr) {
		const InternalNode *internalNode = dynamic_cast<const InternalNode*>(node);
		
		prefix.push_back(0);
		buildCodeList(internalNode->leftChild .get(), prefix);
		prefix.pop_back();
		
		prefix.push_back(1);
		buildCodeList(internalNode->rightChild.get(), prefix);
		prefix.pop_back();
		
	} else if (dynamic_cast<const Leaf*>(node) != nullptr) {
		const Leaf *leaf = dynamic_cast<const Leaf*>(node);
		if (leaf->symbol >= codes.size())
			throw "Symbol exceeds symbol limit";
		if (!codes.at(leaf->symbol).empty())
			throw "Symbol has more than one code";
		codes.at(leaf->symbol) = prefix;
		
	} else {
		throw "Assertion error: Illegal node type";
	}
}


const vector<char> &CodeTree::getCode(uint32_t symbol) const {
	if (codes.at(symbol).empty())
		throw "No code for given symbol";
	else
		return codes.at(symbol);
}
