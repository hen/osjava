package org.osjava.multidoc.generators;

import org.osjava.multidoc.*;

import java.util.*;
import java.io.*;

public class JavadocMultidocGenerator implements MultidocGenerator {

    public void generate(File targetDirectory, DocumentSite site, Document document) throws IOException {
        // create pages
        FileWriter fw = new FileWriter( new File( targetDirectory, "project-frame.html") );
        writeProjectFrame(fw, site, document);
        fw.close();
        fw = new FileWriter( new File( targetDirectory, "overview-frame.html") );
        writePackagesFrame(fw, site, document);
        fw.close();
        fw = new FileWriter( new File( targetDirectory, "overview-summary.html") );
        writeOverviewFrame(fw, site, document);
        fw.close();
    }


    // move to Velocity or something
    public void writeProjectFrame(Writer writer, DocumentSite site, Document document) throws IOException {
        writer.write("<html><head><LINK REL ='stylesheet' TYPE='text/css' HREF='");
        writer.write(site.getStylesheet());
        writer.write("' TITLE='Style'></head><body>\n");
//        writer.write("<table width='100%'><tr><td class='NavBarCell1'><a href='overview-summary.html' target='classFrame'><FONT CLASS='NavBarFont1'><b>Overview</b></FONT></a>\n");
//        writer.write("<a href=''><FONT CLASS='NavBarFont1'><b>Index</a></b></FONT></a>\n");
//        writer.write("<a href='help-doc.html' target='classFrame'><FONT CLASS='NavBarFont1'><b>Help</b></FONT></a></td></tr></table>\n");
//        writer.write("<br/><br/>\n");
//        writer.write("<form action='' target='packageListFrame'><input type='text' name='query' size='15'/><FONT CLASS='FrameItemFont'><input type='submit' value='Find'></FONT></form>\n");
        writer.write("<h4>");
        writer.write(document.getName());
        writer.write("</h4>");
        writer.write("<p><FONT CLASS='FrameItemFont'><a href='overview-frame.html' target='packageListFrame'>All Packages</a></FONT></p>\n");
        writer.write("<FONT CLASS='FrameHeadingFont'><b>Projects</b></FONT><br/>\n");
        Iterator iterator = document.getProjects().iterator();
        while(iterator.hasNext()) {
            DocumentProject project = (DocumentProject) iterator.next();
            writer.write("<nobr><a name='");
            writer.write(project.getTitle());
            writer.write("'><FONT CLASS='FrameItemFont'><a href='");
            
            // be nice to add some javascript here
            if(project.isSinglePackaged()) {
                writer.write("overview-frame.html#");
                writer.write(project.getTitle());
            } else {
                writer.write(project.getUrl());
                writer.write("/overview-frame.html");
            }
            writer.write("' target='packageListFrame'>");
            writer.write(project.getTitle());
            writer.write("</a></FONT></nobr><br/>\n");
        }
        writer.write("</body></html>\n");
    }

    public void writePackagesFrame(Writer writer, DocumentSite site, Document document) throws IOException {
        writer.write("<HTML><HEAD><TITLE>MetaOverview (Jakarta)</TITLE>\n");
        writer.write("<LINK REL ='stylesheet' TYPE='text/css' HREF='");
        writer.write(site.getStylesheet());
        writer.write("' TITLE='Style'>\n");
        writer.write("</HEAD>\n");
        writer.write("<BODY BGCOLOR='white'>\n");
//        writer.write("<FONT CLASS='FrameItemFont'><A HREF='allclasses-frame.html' target='packageFrame'>All Classes</A></FONT>\n");
        writer.write("<P><FONT size='+1' CLASS='FrameHeadingFont'>Packages</FONT><BR>\n");

        Iterator iterator = document.getProjects().iterator();
        while(iterator.hasNext()) {
            DocumentProject project = (DocumentProject) iterator.next();
            writer.write("<a name='");
            writer.write(project.getTitle());
            writer.write("'>");
            List packages = project.getPackages();
            Iterator pckgIterator = packages.iterator();
            while(pckgIterator.hasNext()) {
                DocumentPackage pckg = (DocumentPackage) pckgIterator.next();
                writer.write("<FONT CLASS='FrameItemFont'><A HREF='");
                writer.write(project.getUrl());
                writer.write("/");
                writer.write(pckg.getUrl().replaceFirst("-summary", "-frame"));
                writer.write("' target='packageFrame'>");
                writer.write(pckg.getName());
                writer.write("</A></FONT><BR>\n");
            }
        }

        writer.write("<P></BODY></HTML>\n");
    }

    public void writeOverviewFrame(Writer writer, DocumentSite site, Document document) throws IOException {
        writer.write("<HTML><HEAD><TITLE>Overview (Multidoc)</TITLE>\n");
        writer.write("<LINK REL ='stylesheet' TYPE='text/css' HREF='");
        writer.write(site.getStylesheet());
        writer.write("' TITLE='Style'>\n");
        writer.write("<SCRIPT type='text/javascript'>\n");
        writer.write("function windowTitle() {\n");
        writer.write("    parent.document.title='Overview (Multidoc)';\n");
        writer.write("}\n");
        writer.write("</SCRIPT></HEAD>\n");
        writer.write("<BODY BGCOLOR='white' onload='windowTitle();'>\n");
//        writer.write("<table width='100%'><tr><td class='NavBarCell1'><span class='NavBarCell1Rev'>\n");
//        writer.write("<FONT CLASS='NavBarFont1Rev'><b>Overview</b></FONT></span>\n");
//        writer.write("<a href=''><FONT CLASS='NavBarFont1'><b>Index</a></b></FONT></a>\n");
//        writer.write("<a href='help-doc.html' target='classFrame'><FONT CLASS='NavBarFont1'><b>Help</b></FONT></a></td></tr></table>\n");
        writer.write("<br/><br/>\n");
        writer.write("<HR><CENTER><H1>");
        writer.write(site.getTitle());
        writer.write(" packages</H1></CENTER>\n");

        writer.write("<TABLE BORDER='1' WIDTH='100%' CELLPADDING='3' CELLSPACING='0' SUMMARY=''>\n");
        writer.write("<TR BGCOLOR='#CCCCFF' CLASS='TableHeadingColor'>\n");
        writer.write("<TD COLSPAN=2><FONT SIZE='+2'>\n");
        writer.write("<B>Packages</B></FONT></TD>\n");
        writer.write("</TR>\n");
        Iterator iterator = document.getProjects().iterator();
        while(iterator.hasNext()) {
            DocumentProject project = (DocumentProject) iterator.next();
            writer.write("<TR BGCOLOR='#DDDDFF'><TD COLSPAN='2'><B><FONT SIZE='+1'><A HREF='");
            writer.write(project.getUrl());
            if(project.isSinglePackaged()) {
                writer.write("/");
                writer.write(((DocumentPackage)project.getPackages().get(0)).getUrl());
                writer.write("'>");
            } else {
                writer.write("/overview-summary.html'>");
            }
            writer.write(project.getTitle());
            writer.write("</A></FONT></B></TD></TR>\n");
            List packages = project.getPackages();
            Iterator pckgIterator = packages.iterator();
            while(pckgIterator.hasNext()) {
                DocumentPackage pckg = (DocumentPackage) pckgIterator.next();
                writer.write("<TR BGCOLOR='white' CLASS='TableRowColor'>\n");
                writer.write("<TD WIDTH='20%'><B><A HREF='");
                writer.write(project.getUrl());
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

//        writer.write("<P><HR>Copyright &copy; 2000-2004 The Apache Software Foundation. All Rights Reserved. </P>");
        writer.write("</BODY></HTML>\n");
    }

}

