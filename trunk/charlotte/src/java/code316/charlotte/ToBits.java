package code316.charlotte;
import java.math.BigInteger;


public class ToBits {
    public static void main(String[] args) {
        BigInteger bi = new BigInteger(args[0]);
        
        
        String bitString = bi.toString(2); 
        //System.out.println();
        int count = bitString.length();
        StringBuffer sb = new StringBuffer();
        
        for (int i = count - 1; i >= 0; i--) {
            sb.append("|");
            if ( i > 99 ) {
                System.out.print(i);    
            }
            else if ( i > 9 ) {
                sb.append(" ");
                sb.append(i);                
            }
            else {
                sb.append(" ");
                sb.append(" ");
                sb.append(i);                
            }
        }
        
        sb.append("|\n");
        
        int count2 = sb.length();
        
        for (int i = 0; i < count2; i++) {
            sb.append("-");
        } 
        sb.append("\n");

        
        for (int i = 0; i < count; i++) {
            sb.append("|");
            sb.append(" ");
            sb.append(" ");
            sb.append(bitString.charAt(i));
        }
        
        sb.append("|\n");
        
        for (int i = 0; i < count2; i++) {
            sb.append("-");
        } 
        sb.append("\n");
        
        
        System.out.println(sb);
        System.out.println();
        System.out.println(bitString);
    }
}

