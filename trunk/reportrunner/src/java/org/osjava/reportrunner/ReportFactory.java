package org.osjava.reportrunner;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

public class ReportFactory {

    public static Report getReport(String reportName) {
        Report[] reports = getReports();
        for(int i=0; i<reports.length; i++) {
            if(reports[i].getName().equals(reportName)) {
                return reports[i];
            }
        }
        return null;
    }
    public static Report[] getReports() {
        List reports = new ArrayList();
        XMLNode node = parseXml("reports.xml").getNode("reports");
        Enumeration reportNodes = node.enumerateNode("report");
        while(reportNodes.hasMoreElements()) {
            XMLNode reportNode = (XMLNode) reportNodes.nextElement();
            String className = reportNode.getAttr("class");
            Report report = (Report) ClassW.createObject(className);
            report.setName(reportNode.getAttr("name"));
            report.setLabel(reportNode.getAttr("label"));
            applyNodes( report, reportNode.enumerateNode() );
            applyParamTag( report, reportNode.enumerateNode("param") );
            applyColumnTag( report, reportNode.enumerateNode("column") );
            reports.add(report);
        }
        return (Report[]) reports.toArray( new Report[0] );
    }

    public static Renderer getRenderer(String rendererName) {
        Renderer[] renderers = getRenderers();
        for(int i=0; i<renderers.length; i++) {
            if(renderers[i].getName().equals(rendererName)) {
                return renderers[i];
            }
        }
        return null;
    }
    public static Renderer[] getRenderers() {
        List renderers = new ArrayList();
        XMLNode node = parseXml("renderers.xml").getNode("renderers");
        Enumeration renderNodes = node.enumerateNode("renderer");
        while(renderNodes.hasMoreElements()) {
            XMLNode rendererNode = (XMLNode) renderNodes.nextElement();
            String className = rendererNode.getAttr("class");
            Renderer renderer = (Renderer) ClassW.createObject(className);
            renderer.setName(rendererNode.getAttr("name"));
            renderer.setLabel(rendererNode.getAttr("label"));
            applyNodes( renderer, rendererNode.enumerateNode() );
            renderers.add(renderer);
        }
        return (Renderer[]) renderers.toArray( new Renderer[0] );
    }

    private static void applyNodes( Object obj, Enumeration nodes ) {
        while(nodes.hasMoreElements()) {
            XMLNode node = (XMLNode) nodes.nextElement();
            if(!node.isTag()) {
                continue;
            }
            String name = node.getName();
            if(name.equals("param") || name.equals("column")) {
                // special subtag for reports
                continue;
            }
            // needs code to ignore comments
            String value = node.getValue();
            Class objClass = obj.getClass();
            try {
                Method method = objClass.getMethod("set"+Character.toTitleCase(name.charAt(0))+name.substring(1), new Class[] { String.class } );
                method.invoke( obj, new String[] { value } );
            } catch(NoSuchMethodException nsme) {
                nsme.printStackTrace();
            } catch(IllegalAccessException iae) {
                iae.printStackTrace();
            } catch(IllegalArgumentException iae) {
                iae.printStackTrace();
            } catch(InvocationTargetException ite) {
                ite.printStackTrace();
            }
        }
    }

    private static void applyParamTag( Report report, Enumeration nodes ) {
        while(nodes.hasMoreElements()) {
            XMLNode node = (XMLNode) nodes.nextElement();
            String name = node.getName();
            if(name.equals("param")) {
                String type = node.getAttr("type");
                String binding = node.getAttr("binding");
                Param param = new Param();
                param.setName( node.getAttr("name") );
                param.setTypeAsString( type );
                param.setBinding( binding );
                report.addParam( param );
            }
        }
    }

    private static void applyColumnTag( Report report, Enumeration nodes ) {
        while(nodes.hasMoreElements()) {
            XMLNode node = (XMLNode) nodes.nextElement();
            String name = node.getName();
            if(name.equals("column")) {
                String format = node.getAttr("format");
                String pattern = node.getAttr("pattern");
                Column column = new Column();
                column.setName( node.getAttr("name") );
                column.setLabel( node.getAttr("label") );
                column.setFormatAsString( format );
                column.setPattern( pattern );
                report.addColumn( column );
            }
        }
    }

    private static XMLNode parseXml(String file) {
        Reader reader = null;
        try {
            XMLParser parser = new XMLParser();
            file = "/"+file;
            reader = new InputStreamReader( parser.getClass().getResourceAsStream(file) );
            return parser.parseXML(reader);
        } catch(IOException ioe) {
            throw new RuntimeException("XML failed to be parsed: "+ioe, ioe);
        } finally {
            try { if(reader != null) { reader.close(); } } catch(IOException ioe) { ; }
        }
    }

}

