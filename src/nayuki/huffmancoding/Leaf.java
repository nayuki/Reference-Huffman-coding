package nayuki.huffmancoding;


public final class Leaf extends Node {
	
	public final int symbol;
	
	
	
	public Leaf(int symbol) {
		if (symbol < 0)
			throw new IllegalArgumentException("Negative symbol value");
		this.symbol = symbol;
	}
	
}
