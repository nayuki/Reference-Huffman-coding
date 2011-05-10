package nayuki.huffmancoding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CanonicalCode {
	
	private int[] codeLengths;
	
	
	
	public CanonicalCode(int[] codeLengths) {
		this.codeLengths = codeLengths.clone();
	}
	
	
	public CanonicalCode(CodeTree tree, int symbolLimit) {
		codeLengths = new int[symbolLimit];
		Arrays.fill(codeLengths, -1);
		build(tree.root, 0);
	}
	
	
	private void build(Node node, int depth) {
		if (node instanceof InternalNode) {
			InternalNode internalNode = (InternalNode)node;
			build(internalNode.leftChild , depth + 1);
			build(internalNode.rightChild, depth + 1);
		} else if (node instanceof Leaf) {
			codeLengths[((Leaf)node).symbol] = depth;
		} else {
			throw new AssertionError("Illegal node type");
		}
	}
	
	
	
	public int getSymbolLimit() {
		return codeLengths.length;
	}
	
	
	public int getCodeLength(int symbol) {
		if (symbol < 0 || symbol >= codeLengths.length)
			throw new IllegalArgumentException("Symbol out of range");
		return codeLengths[symbol];
	}
	
	
	public CodeTree toCodeTree() {
		List<Node> nodes = new ArrayList<Node>();
		for (int i = max(codeLengths); i >= 1; i--) {  // Descend through code lengths
			List<Node> newNodes = new ArrayList<Node>();
			
			// Add leaves for symbols with length i
			for (int j = 0; j < codeLengths.length; j++) {
				if (codeLengths[j] == i)
					newNodes.add(new Leaf(j));
			}
			
			// Merge nodes from the previous deeper layer
			for (int j = 0; j < nodes.size(); j += 2)
				newNodes.add(new InternalNode(nodes.get(j), nodes.get(j + 1)));
			
			nodes = newNodes;
			assert nodes.size() % 2 == 0;
		}
		
		assert nodes.size() == 2;
		return new CodeTree(new InternalNode(nodes.get(0), nodes.get(1)), codeLengths.length);
	}
	
	
	private static int max(int[] array) {
		int result = array[0];
		for (int x : array)
			result = Math.max(x, result);
		return result;
	}
	
}
