package org.osjava.multidoc;

import java.io.*;
import java.util.*;

import com.generationjava.scrape.HtmlScraper;
import com.generationjava.net.UrlW;
import com.generationjava.web.XmlW;
import com.generationjava.web.HtmlW;

public class Multidoc {

    public static void main(String[] args) throws IOException {
        Multidoc mdoc = new Multidoc(args[2]);
//mdoc.load("http://jakarta.apache.org/commons/collections/apidocs");
//if(true) return;

        FileReader fr = new FileReader(args[0]);
        BufferedReader reader = new BufferedReader(fr);
        String line = "";
        while( (line = reader.readLine()) != null) {
            line = line.trim();
            if("".equals(line) || line.startsWith("#")) {
                continue;
            }
            mdoc.load(line);
        }
        fr.close();

        File target = new File(args[1]);
        target.mkdirs();

        FileWriter fw = new FileWriter( new File( target, "project-frame.html") );
        mdoc.writeProjectFrame(fw);
        fw.close();
        fw = new FileWriter( new File( target, "overview-frame.html") );
        mdoc.writePackagesFrame(fw);
        fw.close();
        fw = new FileWriter( new File( target, "overview-summary.html") );
        mdoc.writeOverviewFrame(fw);
        fw.close();
    }

    private List sites = new LinkedList();
    private String title;

    public Multidoc(String title) {
        this.title = title;
    }

    // storage methods. Could become a MultidocStore class
    public void load(String urlbase) throws IOException {
        HtmlScraper scraper = new HtmlScraper();
        try {
            scraper.scrape( ""+UrlW.getContent(urlbase+"/overview-summary.html") );
            JavadocSite site = scrapeOverview(scraper, urlbase);
            this.sites.add(site);
        } catch(IOException ioe) {
            // assume it means that overview-summary is not available
            String pckge = ""+UrlW.getContent(urlbase+"/package-list");
            String path = pckge.replaceAll("\\.","/");
            JavadocSite site = new JavadocSite( urlbase, pckge );
            site.setSinglePackage(true);
            site.addPackage( new JavadocPackage(pckge, "/"+path+"/package-summary.html", "") );
            this.sites.add(site);
        }
    }

    private JavadocSite scrapeOverview(HtmlScraper scraper, String urlbase) throws IOException {
        boolean xref = false;

        // bit of a hack. two types of possible comment noticed so far
        if(!scraper.moveToComment("=========== END OF NAVBAR ===========")) {
            if(!scraper.moveToComment("========= END OF TOP NAVBAR =========")) {
                // must be an xref page
                xref = true;
                scraper.move("center");
            }
        }

        // also a hack.  TOP variant uses a h1.
        String title = scraper.get("h2").trim();
        if("".equals(title)) {
            title = scraper.get("h1").trim();
        }
        if(xref) {
            // xref has Reference a lot
            title = title.replaceFirst(" Reference$", "");
        } else {
            // title seems to have ' API' on the end
            title = title.replaceFirst(" API$", "");
        }

        JavadocSite site = new JavadocSite(urlbase, title);

        scraper = scraper.scrapeTag("TABLE");
        scraper.move("TD");  // skip the Packages header
        while(scraper.move("TD")) {
            String cellPackage = scraper.get("A");
            String cellAnchor = scraper.get("A[HREF]");
            if(null == cellAnchor || "".equals(cellAnchor)) { continue; }  // HACK: Bug in scraper I think
            if(xref) {
                site.addPackage( new JavadocPackage( cellPackage, cellAnchor, "") );
            } else {
                scraper.move("TD");
                String cellDescription = scraper.get("TD").trim();
                site.addPackage( new JavadocPackage( cellPackage, cellAnchor, cellDescription ) );
            }
        }

        return site;
    }

    public void unload(String urlbase) throws IOException {
        Iterator iterator = this.sites.iterator();
        while(iterator.hasNext()) {
            JavadocSite site = (JavadocSite) iterator.next();
            if(urlbase.equals(site.getUrl())) {
                iterator.remove();
            }
        }
    }

    public void reload(String urlbase) throws IOException {
        load(urlbase);
    }
    // end of storage methods.

    // move to Velocity or something
    public void writeProjectFrame(Writer writer) throws IOException {
        writer.write("<html><head><LINK REL ='stylesheet' TYPE='text/css' HREF='stylesheet.css' TITLE='Style'></head><body>\n");
        writer.write("<table width='100%'><tr><td class='NavBarCell1'><a href='overview-summary.html' target='classFrame'><FONT CLASS='NavBarFont1'><b>Overview</b></FONT></a>\n");
//        writer.write("<a href=''><FONT CLASS='NavBarFont1'><b>Index</a></b></FONT></a>\n");
        writer.write("<a href='help-doc.html' target='classFrame'><FONT CLASS='NavBarFont1'><b>Help</b></FONT></a></td></tr></table>\n");
//        writer.write("<br/><br/>\n");
//        writer.write("<form action='' target='packageListFrame'><input type='text' name='query' size='15'/><FONT CLASS='FrameItemFont'><input type='submit' value='Find'></FONT></form>\n");
        writer.write("<p><FONT CLASS='FrameItemFont'><a href='overview-frame.html' target='packageListFrame'>All Packages</a></FONT></p>\n");
        writer.write("<FONT CLASS='FrameHeadingFont'><b>Projects</b></FONT><br/>\n");
        Iterator iterator = this.sites.iterator();
        while(iterator.hasNext()) {
            JavadocSite site = (JavadocSite) iterator.next();
            writer.write("<nobr><a name='");
            writer.write(site.getTitle());
            writer.write("'><FONT CLASS='FrameItemFont'><a href='");
            
            // be nice to add some javascript here
            if(site.isSinglePackage()) {
                writer.write("overview-frame.html#");
                writer.write(site.getTitle());
            } else {
                writer.write(site.getUrl());
                writer.write("/overview-frame.html");
            }
            writer.write("' target='packageListFrame'>");
            writer.write(site.getTitle());
            writer.write("</a></FONT></nobr><br/>\n");
        }
        writer.write("</body></html>\n");
    }

    public void writePackagesFrame(Writer writer) throws IOException {
        writer.write("<HTML><HEAD><TITLE>MetaOverview (Jakarta)</TITLE>\n");
        writer.write("<LINK REL ='stylesheet' TYPE='text/css' HREF='stylesheet.css' TITLE='Style'>\n");
        writer.write("</HEAD>\n");
        writer.write("<BODY BGCOLOR='white'>\n");
//        writer.write("<FONT CLASS='FrameItemFont'><A HREF='allclasses-frame.html' target='packageFrame'>All Classes</A></FONT>\n");
        writer.write("<P><FONT size='+1' CLASS='FrameHeadingFont'>Packages</FONT><BR>\n");

        Iterator iterator = this.sites.iterator();
        while(iterator.hasNext()) {
            JavadocSite site = (JavadocSite) iterator.next();
            writer.write("<a name='");
            writer.write(site.getTitle());
            writer.write("'>");
            List packages = site.getPackages();
            Iterator pckgIterator = packages.iterator();
            while(pckgIterator.hasNext()) {
                JavadocPackage pckg = (JavadocPackage) pckgIterator.next();
                writer.write("<FONT CLASS='FrameItemFont'><A HREF='");
                writer.write(site.getUrl());
                writer.write("/");
                writer.write(pckg.getUrl().replaceFirst("-summary", "-frame"));
                writer.write("' target='packageFrame'>");
                writer.write(pckg.getName());
                writer.write("</A></FONT><BR>\n");
            }
        }

        writer.write("<P></BODY></HTML>\n");
    }

    public void writeOverviewFrame(Writer writer) throws IOException {
        writer.write("<HTML><HEAD><TITLE>Overview (MultiJavadoc)</TITLE>\n");
        writer.write("<LINK REL ='stylesheet' TYPE='text/css' HREF='stylesheet.css' TITLE='Style'>\n");
        writer.write("<SCRIPT type='text/javascript'>\n");
        writer.write("function windowTitle() {\n");
        writer.write("    parent.document.title='Overview (MultiJavadoc)';\n");
        writer.write("}\n");
        writer.write("</SCRIPT></HEAD>\n");
        writer.write("<BODY BGCOLOR='white' onload='windowTitle();'>\n");
        writer.write("<table width='100%'><tr><td class='NavBarCell1'><span class='NavBarCell1Rev'>\n");
        writer.write("<FONT CLASS='NavBarFont1Rev'><b>Overview</b></FONT></span>\n");
//        writer.write("<a href=''><FONT CLASS='NavBarFont1'><b>Index</a></b></FONT></a>\n");
        writer.write("<a href='help-doc.html' target='classFrame'><FONT CLASS='NavBarFont1'><b>Help</b></FONT></a></td></tr></table>\n");
        writer.write("<br/><br/>\n");
        writer.write("<HR><CENTER><H1>");
        writer.write(this.title);
        writer.write(" packages</H1></CENTER>\n");

        writer.write("<TABLE BORDER='1' WIDTH='100%' CELLPADDING='3' CELLSPACING='0' SUMMARY=''>\n");
        writer.write("<TR BGCOLOR='#CCCCFF' CLASS='TableHeadingColor'>\n");
        writer.write("<TD COLSPAN=2><FONT SIZE='+2'>\n");
        writer.write("<B>Packages</B></FONT></TD>\n");
        writer.write("</TR>\n");
        Iterator iterator = this.sites.iterator();
        while(iterator.hasNext()) {
            JavadocSite site = (JavadocSite) iterator.next();
            writer.write("<TR BGCOLOR='#DDDDFF'><TD COLSPAN='2'><B><FONT SIZE='+1'><A HREF='");
            writer.write(site.getUrl());
            if(site.isSinglePackage()) {
                writer.write("/");
                writer.write(((JavadocPackage)site.getPackages().get(0)).getUrl());
                writer.write("'>");
            } else {
                writer.write("/overview-summary.html'>");
            }
            writer.write(site.getTitle());
            writer.write("</A></FONT></B></TD></TR>\n");
            List packages = site.getPackages();
            Iterator pckgIterator = packages.iterator();
            while(pckgIterator.hasNext()) {
                JavadocPackage pckg = (JavadocPackage) pckgIterator.next();
                writer.write("<TR BGCOLOR='white' CLASS='TableRowColor'>\n");
                writer.write("<TD WIDTH='20%'><B><A HREF='");
                writer.write(site.getUrl());
                writer.write("/");
                writer.write(pckg.getUrl());
                writer.write("'>");
                writer.write(pckg.getName());
                writer.write("</A></B></TD>\n<TD>");
                writer.write(pckg.getDescription());
                writer.write("</TD></TR>\n");
            }
        }
        writer.write("</TABLE>\n");

        writer.write("<P><HR>Copyright &copy; 2000-2004 The Apache Software Foundation. All Rights Reserved. </BODY></HTML>\n");
    }

}

class JavadocSite {

    private String url;
    private String title;
    private List packages = new ArrayList();
    private boolean singlePackage;

    public JavadocSite(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public String getUrl() {
        return this.url;
    }

    public String getTitle() {
        return this.title;
    }

    public List getPackages() {
        return this.packages;
    }

    public void addPackage(JavadocPackage pckge) {
        this.packages.add(pckge);
    }

    public boolean isSinglePackage() {
        return this.singlePackage;
    }

    public void setSinglePackage(boolean b) {
        this.singlePackage = b;
    }

    public String toString() {
        return "["+title+" "+url+(singlePackage?"/single":"")+"];"+packages;
    }

}

class JavadocPackage {

    private String name;
    private String url;
    private String description;

    public JavadocPackage(String name, String url, String description) {
        this.name = name;
        this.url = url;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }

    public String getDescription() {
        return this.description;
    }

    public String toString() {
        return this.name + " - '" + this.description + "'";
    }

    public boolean equals(Object obj) {
        if(obj instanceof JavadocPackage) {
            JavadocPackage jp = (JavadocPackage)obj;
            return (this.name == jp.name);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.name.hashCode();
    }

}
