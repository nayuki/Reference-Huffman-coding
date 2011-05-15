package nayuki.huffmancoding;

import java.io.IOException;
import java.util.List;


public final class HuffmanEncoder {
	
	private BitOutputStream output;
	
	public CodeTree codeTree;
	
	
	
	public HuffmanEncoder(BitOutputStream out) {
		output = out;
	}
	
	
	
	public void write(int symbol) throws IOException {
		if (codeTree == null)
			throw new NullPointerException("Code tree is null");
		
		List<Integer> bits = codeTree.getCode(symbol);
		for (int b : bits)
			output.write(b);
	}
	
}
