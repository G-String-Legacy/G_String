package utilities;

import java.util.BitSet;

public class Bits {

	  public static BitSet convert(long value) {
	    BitSet bits = new BitSet();
	    int index = 0;
	    while (value != 0L) {
	      if (value % 2L != 0) {
	        bits.set(index);
	      }
	      ++index;
	      value = value >>> 1;
	    }
	    return bits;
	  }
}