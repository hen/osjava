import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.*;

public class Style {

    public static void main(String[] args) {
        try {
            run(args);
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }
    public static void run(String[] args) throws TransformerException, TransformerConfigurationException, FileNotFoundException, IOException, ParserConfigurationException, SAXException {  
        if(args.length != 2) {
            throw new IllegalArgumentException("Must be 2 arguments, book xml and page xsl ");
        }

        // Generate the book pages
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer(new StreamSource(args[1]));
        // Use this in filename
        String name = "docs/"+args[0].substring(0, args[0].length()-4)+".html";

        transformer.transform(new StreamSource(args[0]), new StreamResult(new FileOutputStream(name)));
        System.out.println(". "+name);
    }
}
