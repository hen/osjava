import java.io.*;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import javax.xml.transform.dom.DOMSource;

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
        if(args.length < 2) {
            throw new IllegalArgumentException("Must be at least 2 arguments, xml and xsl. A third optional argument is an XPath to a point in the xml. ");
        }
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer(new StreamSource(args[1]));

        int extIdx = args[1].lastIndexOf("-");
        String ext = ".html";
        if(extIdx != -1) { 
            ext = "."+args[1].substring(extIdx+1, args[1].length()-4);
        }

        if(args.length > 2) {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(args[2]);
            // God, how I hate W3C DOM:
            NodeList target = doc.getDocumentElement().getElementsByTagName(args[3]);
            System.out.print(args[0]+" as "+ext+" ");
            for(int i=0; i<target.getLength(); i++) {
                Node node = target.item(i);
                String id = node.getAttributes().getNamedItem(args[4]).getNodeValue();
                // Need to do search and replace on args[0] for '$id'
                String txt = loadFileAndReplace(args[0], args[4], id);
                StringReader rdr = new StringReader(txt);

                // Use this in filename. Changing xml to html and content/ to docs/
                String name = "docs/"+args[0].substring(8, args[0].length()-4)+"-"+id+ext;
                transformer.transform(new StreamSource(rdr), new StreamResult(new FileOutputStream(name)));
                System.out.print(".");
            }
            System.out.println();
        } else {
            // Use this in filename. Changing xml to html and content/ to docs/
            String name = "docs/"+args[0].substring(8, args[0].length()-4)+ext;
            transformer.transform(new StreamSource(args[0]), new StreamResult(new FileOutputStream(name)));
            System.out.println(args[0]+" as "+ext+" .");
        }
    }

    static private String loadFileAndReplace(String filename, String tag, String value) throws IOException {
        StringBuffer txt = new StringBuffer();
        BufferedReader rdr = new BufferedReader( new FileReader(filename) );
        String line = "";
        while( (line = rdr.readLine()) != null) {
            line = line.replaceAll("\\$"+tag, value);
            txt.append(line);
            txt.append("\n");
        }
        rdr.close();
        return txt.toString();
    }

}
