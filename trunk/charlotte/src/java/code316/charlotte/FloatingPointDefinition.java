package code316.charlotte;

import java.math.BigDecimal;
import java.math.BigInteger;


public class FloatingPointDefinition extends SimpleDefinition {
    public FloatingPointDefinition() {
        setType(FieldType.FLOAT);
    }
    
    public FloatingPointDefinition(String name, String value) {
        setName(name);
    }
    
    public BigInteger floatToBits(BigDecimal value) {
        return null;        
    }
    
    public BigDecimal bitsToFloat(BigInteger bits) {
        return null;
    }
}
