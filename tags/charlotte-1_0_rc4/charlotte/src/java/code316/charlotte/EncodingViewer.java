package code316.charlotte;

import java.io.FileInputStream;
import java.io.IOException;


public class EncodingViewer {
    public static void main(String[] args) throws IOException {
        FileInputStream in = new FileInputStream(args[0]);
        Parser p = new Parser();
        Encoding e = p.parse(in);
        
        System.out.println(EncodingUtil.encodingToString(e));        
    }
}
