package code316.charlotte;
import java.math.BigDecimal;
import java.math.BigInteger;

public class Operator {
    private class Operation {
        char operator;
        String number;
    }
    
    public static BigDecimal resolve(BigInteger val, String operands) {
        BigDecimal result = new BigDecimal(val.toString()).setScale(10);

        if ( operands == null ) {
            return result; 
        }
        
        char buffer[] = operands.toCharArray();
        StringBuffer num = new StringBuffer();
        
        int i = 0;
        while ( i < buffer.length ) {
            char operator = buffer[i];
            
            if ( operator != '+'
                    && operator != '-'
                    && operator != '*'
                    && operator != '/'
                    && operator != '^' ) {
                i++;
                continue;                                
            }             
            
            // extract number
            num.setLength(0);
            i = parseNumber(num, buffer, i + 1);
            
            BigDecimal operand = new BigDecimal(num.toString());
            // perform operation
            switch (operator) {
                case '+': 
                    result = result.add(operand);                   
                    break;

                case '-':
                    result = result.subtract(operand);
                    break;
                    
                case '*':
                    result = result.multiply(operand);
                    break;
                    
                case '/':
                    result = result.divide(operand, BigDecimal.ROUND_HALF_UP);
                    break;
                    
                case '^': {
                    int power = operand.intValue();
                    
                    if ( power == 0 ) {
                        result = new BigDecimal("1");
                    }
                    else {
                        for (int j = 0; j < (power - 1) ; j++) {
                            result = result.multiply(result);        
                        }
                    }
                }
                break;                
                
                default :
                    throw new IllegalArgumentException("unknown operator: " + operator);
            }            
        }
        
        return result;
    }
    
    private static int parseNumber(StringBuffer num, char buffer[], int offset) {
        int i = offset;
        
        while (i < buffer.length) {
            char ch = buffer[i];
            if ( ch == '.' || Character.isDigit(ch) ) {
                num.append(ch);
            }
            else if ( ch == ')' || ch == '(' || Character.isWhitespace(ch) ) {
                // skip white space                
            }
            else {
                return i;
            }            
            i++;
        }
        
        return i;
    }    
}
