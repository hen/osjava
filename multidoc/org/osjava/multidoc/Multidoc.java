package org.osjava.multidoc;

import java.io.*;
import java.util.*;

import com.generationjava.io.xml.*;

public class Multidoc {

    // to be:   multidoc multidoc.conf targt/multidoc
    public static void main(String[] args) throws IOException {

        // conf parser, many sites of the following structure:
        //  site.name=Jakarta Commons
        //  site.stylesheet=http://jakarta.apache.org/commons/style.css
        //  site.type=Javadoc
        //  site.uri=whitespace delimited urls

        // NEEDS REWRITING IN GJ-XML
        FileReader reader = new FileReader(args[0]);
        XMLParser parser = new XMLParser();
        XMLNode node = parser.parseXML(new BufferedReader(reader));
        // node is <site>
        String title = node.getAttr("name");
        String style = node.getAttr("style");
        String url = node.getAttr("url");
        System.out.println("Multidocing for "+title);
        DocumentSite site = new DocumentSite(url, title, style);
        Enumeration docEnum = node.enumerateNode("document");
        // hacked to do just the first one
        if(docEnum.hasMoreElements()) {
            XMLNode subnode = (XMLNode) docEnum.nextElement();
            String type = subnode.getAttr("type");
            Enumeration uriEnum = subnode.enumerateNode("uri");
            while(uriEnum.hasMoreElements()) {
                XMLNode urinode = (XMLNode) uriEnum.nextElement();
                String uri = urinode.getValue();
                System.out.println("Considering "+uri);
                // use type with a factory
                DocumentCreator creator = new org.osjava.multidoc.creators.JavadocCreator();
                Document doc = creator.create(uri);
                site.addDocument(doc);
            }
        }

        // target directory
        File target = new File(args[1]);
        target.mkdirs();

        MultidocGenerator generator = new org.osjava.multidoc.generators.JavadocMultidocGenerator();

        // create pages
        FileWriter fw = new FileWriter( new File( target, "project-frame.html") );
        generator.writeProjectFrame(fw, site);
        fw.close();
        fw = new FileWriter( new File( target, "overview-frame.html") );
        generator.writePackagesFrame(fw, site);
        fw.close();
        fw = new FileWriter( new File( target, "overview-summary.html") );
        generator.writeOverviewFrame(fw, site);
        fw.close();
    }

}

