package code316.charlotte;
import java.math.BigInteger;


public class EncodedValue {
    /** width in bits */    
    private int width;
    
    /** starting bit location */
    private int startingBit;

    public BigInteger extract(BigInteger bits) {
        if (bits == null) {
            throw new IllegalArgumentException("invalid value for bits: " + bits);
        }
        
        return null;
    }        
    
    public int getStartingBit() {
        return startingBit;
    }

    public int getWidth() {
        return width;
    }

    public void setStartingBit(int i) {
        startingBit = i;
    }

    public void setWidth(int i) {
        width = i;
    }
}
