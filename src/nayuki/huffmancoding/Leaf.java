package nayuki.huffmancoding;


/**
 * A leaf node in a code tree. It has a symbol value. Immutable.
 */
public final class Leaf extends Node {
	
	public final int symbol;
	
	
	
	public Leaf(int symbol) {
		if (symbol < 0)
			throw new IllegalArgumentException("Negative symbol value");
		this.symbol = symbol;
	}
	
}
