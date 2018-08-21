/* 
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */

#include <algorithm>
#include <cassert>
#include <stdexcept>
#include <utility>
#include "FrequencyTable.hpp"

using std::uint32_t;
using std::uint64_t;
using std::vector;


FrequencyTable::FrequencyTable(const std::vector<uint32_t> &freqs) :
		frequencies(freqs) {
	if (freqs.size() < 2)
		throw std::invalid_argument("At least 2 symbols needed");
	if (freqs.size() > UINT32_MAX)
		throw std::length_error("Too many symbols");
}


uint32_t FrequencyTable::getSymbolLimit() const {
	return static_cast<uint32_t>(frequencies.size());
}


uint32_t FrequencyTable::get(uint32_t symbol) const {
	return frequencies.at(symbol);
}


void FrequencyTable::set(uint32_t symbol, uint32_t freq) {
	frequencies.at(symbol) = freq;
}


void FrequencyTable::increment(uint32_t symbol) {
	if (frequencies.at(symbol) == UINT32_MAX)
		throw std::overflow_error("Maximum frequency reached");
	frequencies.at(symbol)++;
}


CodeTree FrequencyTable::buildCodeTree() const {
	// Note that if two nodes have the same frequency, then the tie is broken
	// by which tree contains the lowest symbol. Thus the algorithm has a
	// deterministic output and does not rely on the queue to break ties.
	std::priority_queue<NodeWithFrequency> pqueue;
	
	// Add leaves for symbols with non-zero frequency
	{
		uint32_t i = 0;
		for (uint32_t freq : frequencies) {
			if (freq > 0)
				pqueue.push(NodeWithFrequency(new Leaf(i), i, freq));
			i++;
		}
	}
	
	// Pad with zero-frequency symbols until queue has at least 2 items
	{
		uint32_t i = 0;
		for (uint32_t freq : frequencies) {
			if (pqueue.size() >= 2)
				break;
			if (freq == 0)
				pqueue.push(NodeWithFrequency(new Leaf(i), i, freq));
			i++;
		}
	}
	assert(pqueue.size() >= 2);
	
	// Repeatedly tie together two nodes with the lowest frequency
	while (pqueue.size() > 1) {
		NodeWithFrequency x = popQueue(pqueue);
		NodeWithFrequency y = popQueue(pqueue);
		pqueue.push(NodeWithFrequency(
			new InternalNode(std::move(x.node), std::move(y.node)),
			std::min(x.lowestSymbol, y.lowestSymbol),
			x.frequency + y.frequency));
	}
	
	// Return the remaining node
	NodeWithFrequency temp = popQueue(pqueue);
	InternalNode *root = dynamic_cast<InternalNode*>(temp.node.release());
	CodeTree result(std::move(*root), getSymbolLimit());
	delete root;
	return result;
}


FrequencyTable::NodeWithFrequency::NodeWithFrequency(Node *nd, uint32_t lowSym, uint64_t freq) :
	node(std::unique_ptr<Node>(nd)),
	lowestSymbol(lowSym),
	frequency(freq) {}


bool FrequencyTable::NodeWithFrequency::operator<(const NodeWithFrequency &other) const {
	if (frequency > other.frequency)
		return true;
	else if (frequency < other.frequency)
		return false;
	else if (lowestSymbol > other.lowestSymbol)
		return true;
	else if (lowestSymbol < other.lowestSymbol)
		return false;
	else
		return false;
}


FrequencyTable::NodeWithFrequency FrequencyTable::popQueue(std::priority_queue<NodeWithFrequency> &pqueue) {
	FrequencyTable::NodeWithFrequency result = std::move(const_cast<NodeWithFrequency&&>(pqueue.top()));
	pqueue.pop();
	return result;
}
