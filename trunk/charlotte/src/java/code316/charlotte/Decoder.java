package code316.charlotte;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;


public class Decoder {
    public static void main(String[] args) throws IOException {
        String encodingName = args[0];
        String value = args[1];

        Encoding e = new Encoding();
        e.parse(new FileInputStream(encodingName));
        
        System.out.println(e.dumpFields());
        System.out.println(e.getLength());
        System.out.println(e.dumpValues(new BigInteger(value)));
    }
}