package code316.charlotte;

import java.math.BigInteger;

import junit.framework.TestCase;


public class ValueExtractorTest extends TestCase {
    public void testExtraction() {
        int shift = 2;

        BigInteger maxVal = new BigInteger("2047");
        
        // decimal test
        BigInteger result = ValueExtractor.extract("2748", shift, maxVal);
        assertEquals(new BigInteger("687"), result);
        
        
        // hex test
        result = ValueExtractor.extract("abch", shift, maxVal);
        assertEquals(new BigInteger("687"), result);
        
        result = ValueExtractor.extract("abcH", shift, maxVal);
        assertEquals(new BigInteger("687"), result);


        // binary test        
        result = ValueExtractor.extract("101010111100b", shift, maxVal);
        assertEquals(new BigInteger("687"), result);

        result = ValueExtractor.extract("101010111100B", shift, maxVal);
        assertEquals(new BigInteger("687"), result);
    }

    
    public void testMainMethod() {        
        ValueExtractor.main(new String[] {"2748", "2", "2047"});        
    }
}
