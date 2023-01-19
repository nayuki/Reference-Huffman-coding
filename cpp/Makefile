# 
# Reference Huffman coding
# 
# Copyright (c) Project Nayuki
# MIT License. See readme file.
# https://www.nayuki.io/page/reference-huffman-coding
# 


CXXFLAGS += -std=c++11 -O1 -Wall -Wextra -fsanitize=undefined


.SUFFIXES:

.SECONDARY:

.DEFAULT_GOAL = all
.PHONY: all clean


OBJ = BitIoStream.o CanonicalCode.o CodeTree.o FrequencyTable.o HuffmanCoder.o
MAINS = AdaptiveHuffmanCompress AdaptiveHuffmanDecompress HuffmanCompress HuffmanDecompress

all: $(MAINS)

clean:
	rm -f -- $(OBJ) $(MAINS:=.o) $(MAINS)
	rm -rf .deps

%: %.o $(OBJ)
	$(CXX) $(CXXFLAGS) -o $@ $^

%.o: %.cpp .deps/timestamp
	$(CXX) $(CXXFLAGS) -c -o $@ -MMD -MF .deps/$*.d $<

.deps/timestamp:
	mkdir -p .deps
	touch .deps/timestamp

-include .deps/*.d
