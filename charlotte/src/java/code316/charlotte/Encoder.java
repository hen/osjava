package code316.charlotte;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Properties;

public class Encoder {

    public static void main(String[] args) throws IOException {
        String encodingFileName = args[0];
        String valueFile = args[1];

        Encoding e = new Encoding();
        e.parse(new FileInputStream(encodingFileName));
        
        Properties props = new Properties();
        props.load(new FileInputStream(valueFile));
        
        System.out.println(e.dumpFields());
        
        System.out.println(props);
        
        BigInteger val = e.encode(props);

        System.out.println(val);
    }
}
