package org.osjava.multidoc;

import java.io.*;
import java.util.*;

import com.generationjava.io.xml.*;
import com.generationjava.web.*;

import org.apache.commons.lang.StringUtils;

public class Multidoc {

    // multidoc multidoc.conf target/multidoc
    public static void main(String[] args) throws IOException {

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
        while(docEnum.hasMoreElements()) {
            XMLNode subnode = (XMLNode) docEnum.nextElement();
            String type = subnode.getAttr("type");
            String name = subnode.getAttr("name");
            Document document = new Document(type, name);
            site.addDocument(name, document);
            DocumentProjectCreator creator = Multidoc.getCreator(type);
            Enumeration uriEnum = subnode.enumerateNode("uri");
            while(uriEnum.hasMoreElements()) {
                XMLNode urinode = (XMLNode) uriEnum.nextElement();
                String uri = urinode.getValue();
                System.out.println("Loading: "+uri);
                DocumentProject project = creator.create(uri);
                if(project == null) {
                    System.err.println("WARN: null project found: "+uri);
                    continue;
                }
                document.addProject(project);
            }
        }

        // target directory
        File target = new File(args[1]);
        target.mkdirs();

        Collection names = site.getNames();
        Iterator iterator = names.iterator();
        while(iterator.hasNext()) {
            String name = (String) iterator.next();
            Document document = site.getDocument(name);
            MultidocGenerator generator = getGenerator(document.getType());
            File subtarget = new File(target, name);
            subtarget.mkdirs();
            System.out.println("Generating for: "+name);
            generator.generate( subtarget, site, document );
        }

        System.out.println("Generating frontpage");
        generateFrontPage( target, site );
        System.out.println("Generating menu");
        generateMenu( target, site );
        System.out.println("Generating about");
        generateAbout( target, site );
        System.out.println("Generating configuration");
        generateConfiguration( target, site, node );

    }

    public static DocumentProjectCreator getCreator(String type) {
        if("Javadoc".equals(type)) {
            return new org.osjava.multidoc.creators.JavadocCreator();
        }
        if("XRef".equals(type)) {
            return new org.osjava.multidoc.creators.XRefCreator();
        }
        if("JCoverage".equals(type)) {
            return new org.osjava.multidoc.creators.JCoverageCreator();
        }
        return null;
    }

    public static MultidocGenerator getGenerator(String type) {
        if("Javadoc".equals(type)) {
            return new org.osjava.multidoc.generators.JavadocMultidocGenerator();
        }
        if("XRef".equals(type)) {
            return new org.osjava.multidoc.generators.JavadocMultidocGenerator();
        }
        if("JCoverage".equals(type)) {
            return new org.osjava.multidoc.generators.JavadocMultidocGenerator();
        }
        return null;
    }

    public static void generateMenu(File targetDirectory, DocumentSite site) throws IOException {
        FileWriter fw = new FileWriter( new File( targetDirectory, "menu.html") );
        fw.write("<html><head><LINK REL ='stylesheet' TYPE='text/css' HREF='");
        fw.write(site.getStylesheet());
        fw.write("' TITLE='Style'>\n");
        fw.write("<script src='multidoc.js'></script>\n");
        fw.write("</head><body>\n");
        fw.write("<table width='100%'><tr><td class='NavBarCell1'>\n");
//        fw.write("<a href='API/overview-summary.html' target='classFrame'><FONT CLASS='NavBarFont1'><b>Overview</b></FONT></a> - \n"); 

        Collection names = site.getNames();
        Iterator iterator = names.iterator();
        while(iterator.hasNext()) {
            String name = (String) iterator.next();
            fw.write("<a href=\"javascript:load('projectListFrame','");
            fw.write(name);
            fw.write("/project-frame.html','classFrame','");
            fw.write(name);
            fw.write("/overview-summary.html','packageListFrame','");
            fw.write(name);
            fw.write("/overview-frame.html','packageFrame','blank.html')\"><FONT CLASS=\"NavBarFont1\"><b>");
            fw.write(name);
            fw.write("</b></FONT></a> - \n");
        }
        fw.write("<a href='");
        fw.write(site.getUrl());
        fw.write("' target='classFrame'><FONT CLASS='NavBarFont1'><b>");
        fw.write(site.getUrl());
        fw.write("</b></FONT></a>\n");
        fw.write("</td>\n<td align='right' class='NavBarCell1'>");
        fw.write("<a href='about.html' target='classFrame'><FONT CLASS='NavBarFont1'><b>About Multidoc</b></FONT></a>\n");
        fw.write("</td></tr></table>\n");
        fw.write("</body></html>\n");
        fw.close();
    }

    public static void generateAbout(File targetDirectory, DocumentSite site) throws IOException {
        FileWriter fw = new FileWriter( new File( targetDirectory, "about.html") );
        fw.write("<html><HEAD>\n");
        fw.write("<TITLE>About Multidoc</TITLE>\n");
        fw.write("<LINK REL ='stylesheet' TYPE='text/css' HREF='stylesheet.css' TITLE='Style'>\n");
        fw.write("<SCRIPT type='text/javascript'>\n");
        fw.write("function windowTitle()\n");
        fw.write("{\n");
        fw.write("    parent.document.title='About Multidoc';\n");
        fw.write("}\n");
        fw.write("</SCRIPT>\n");
        fw.write("</HEAD>\n");
        fw.write("<BODY BGCOLOR='white' onload='windowTitle();'>\n");
        fw.write("<HR>\n");
        fw.write("<CENTER> <div class='NavBarCell1'>What is this all about?</div> </CENTER>\n");
        fw.write("<p>This is a Multidoc system. It consists of a wrapper around a set of javadocs.</p>\n");
        fw.write("<CENTER> <div class='NavBarCell1'>Why does it exist?</div> </CENTER>\n");
        fw.write("<p>First and foremost, to put all of Jakarta Commons into a single javadoc, possibly all of Apache/Jakarta if desired. Also should be useful for other locations; osjava, codehaus etc. </p>\n");
        fw.write("<CENTER> <div class='NavBarCell1'>How does it work?</div> </CENTER>\n");
        fw.write("<p>The current version scrapes from a list of documentation urls and collates a small set of pages around those pages. The exact urls used are available on the <a href='configuration.html'>configuration</a> page. </p>\n");
        fw.write("<CENTER> <div class='NavBarCell1'>Future?</div> </CENTER>\n");
        fw.write("<ul>\n");
        fw.write("<li>Add a categorization scheme so all of Apache fits. Projects should be able to be in multiple categories. </li>\n");
        fw.write("<li>Needs to maintain a state so it doesn't have to keep downloading all pages just because a new one is added.</li>\n");
        fw.write("<li>It would be nice if it would also pull down and collate an index.</li>\n");
        fw.write("<li>It would be very nice to integrate a search into the project-frame.</li>\n");
        fw.write("<li>Improve the HTML as the current one is quite hacked from the javadoc style. </li>\n");
        fw.write("<li>Integrate Clover</li>\n");
        fw.write("<li>Mavenise and produce an executable jar</li>\n");
        fw.write("<li>Release on osjava.org</li>\n");
        fw.write("</ul>\n");
        fw.write("</BODY>\n");
        fw.write("</HTML>\n");
        fw.close();
    }

    public static void generateConfiguration(File targetDirectory, DocumentSite site, XMLNode root) throws IOException {
        FileWriter fw = new FileWriter( new File( targetDirectory, "configuration.html") );
        String xml = root.toString();
        String font = "<font color='#CC6600'>";
        xml = StringUtils.replace(xml, "<", font+"&lt;");
        xml = StringUtils.replace(xml, ">", "&gt;</font>");
        xml = StringUtils.replace(xml, "  ", "&nbsp;&nbsp;");
        xml = StringUtils.replace(xml, "=\"", "=\"</font>");
        xml = StringUtils.replace(xml, "\" ", font+"\" ");
        xml = StringUtils.replace(xml, "\"&gt;", font+"\"&gt;");
        xml = HtmlW.nl2br(xml);
        fw.write(xml);
        fw.close();
    }

    public static void generateFrontPage(File targetDirectory, DocumentSite site) throws IOException {
        FileWriter fw = new FileWriter( new File( targetDirectory, "frontpage.html") );
        fw.write("<html><head>");
        fw.write("</head><body>");
        fw.write("<h3>Multidoc</h3>\n");
        fw.write("<p>This is a multidoc system for <a href='");
        fw.write(site.getUrl());
        fw.write("'>");
        fw.write(site.getTitle());
        fw.write("</a>. To use it, use the document-types listed in the menu-frame above. </p>");
        fw.write("</body></html>");
        fw.close();
    }
}
