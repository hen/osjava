package code316.charlotte;

import java.math.BigDecimal;
import java.math.BigInteger;

import junit.framework.TestCase;


public class OperatorTest extends TestCase {
    public void testResolve() {        
        BigInteger num = new BigInteger("1");

        assertEquals(new BigDecimal("25.00").floatValue(), Operator.resolve(num, "+1 * 2.5 ^ 2").floatValue(), 0.00F);
        assertEquals(new BigDecimal("22.00").floatValue(), Operator.resolve(num, "+1 * 2.5 ^ 2 - 3").floatValue(), 0.00F);
    }
}
