package code316.charlotte;
import java.math.BigInteger;


public class ValueExtractor {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("use: ValueExtractor <bitString> <shift> <maxValue>");
            return;
        }


        int shift = Integer.parseInt(args[1]);

        
        System.out.println(extract(args[0], shift, new BigInteger(args[2])));
    }

    public static BigInteger extract(String valString, int shift, BigInteger maxVal) {
        int radix = 10;
        String val = valString;
        char last = valString.charAt(valString.length() - 1); 
        if (  last == 'h' || last == 'H') {
            radix = 16;
            val = valString.substring(0, valString.length() - 1);
        }
        else if ( last == 'b' || last == 'B') {
            radix = 2;
            val = valString.substring(0, valString.length() - 1);
        }

        return new BigInteger(val, radix).shiftRight(shift).and(maxVal);
    }
}
