package code316.charlotte;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import code316.core.Args;


public class EncodingDumper {
    private static int PLAIN = 0;
    private static int ALPHABET_SOUP = 2;
    
    

    public static void main(String[] args) throws FileNotFoundException, IOException {
        String encodingFile = args[0];
        int format = PLAIN;


        if ( args.length > 1 ) {            
            Properties props = Args.parse(args, 0, 1);
            String formatString = props.getProperty("format", "plain");
            
            if ( formatString.equals("alpha") ) {
                format = ALPHABET_SOUP;
            }
        }

        Encoding e = new Encoding(new FileInputStream(encodingFile));
        
        System.out.println(encodingFile);
        
        List fields = e.getFieldDefinitions();
        Iterator tor = fields.iterator();
        while (tor.hasNext()) {
            System.out.println("###############################");
            FieldDefinition fd = (FieldDefinition) tor.next();
            System.out.println("field name: "+ fd.getName());
            System.out.println(" start bit: "+ fd.getOffset());
            System.out.println("     width: "+ fd.getLength());
            if ( fd.getOperands() == null ) {
                System.out.println("  mutators: none");            
            }
            else {
                System.out.println("  mutators: " + fd.getOperands());
            }
            
            
        }
            System.out.println("-------------------------------");
        System.out.println("encoding length: " + e.getLength() + " bit(s)");
    }
}
