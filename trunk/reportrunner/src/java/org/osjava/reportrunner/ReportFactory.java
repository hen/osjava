package org.osjava.reportrunner;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

public class ReportFactory {

    public static Report getReport(String reportName) {
        XMLNode node = parseXml("reports.xml").getNode("reports");
        Enumeration reports = node.enumerateNode("report");
        while(reports.hasMoreElements()) {
            XMLNode reportNode = (XMLNode) reports.nextElement();
            if(reportNode.getAttr("name").equals(reportName)) {
                String className = reportNode.getAttr("class");
                Report report = (Report) ClassW.createObject(className);
                report.setName(reportName);
                report.setLabel(reportNode.getAttr("label"));
                applyNodes( report, reportNode.enumerateNode() );
                applyParamTag( report, reportNode.enumerateNode("param") );
                return report;
            }
        }
        return null;
    }

    public static Renderer getRenderer(String rendererName) {
        XMLNode node = parseXml("renderers.xml").getNode("renderers");
        Enumeration renderers = node.enumerateNode("renderer");
        while(renderers.hasMoreElements()) {
            XMLNode rendererNode = (XMLNode) renderers.nextElement();
            if(rendererNode.getAttr("name").equals(rendererName)) {
                String className = rendererNode.getAttr("class");
                Renderer renderer = (Renderer) ClassW.createObject(className);
                renderer.setName(rendererName);
                renderer.setLabel(rendererNode.getAttr("label"));
                applyNodes( renderer, rendererNode.enumerateNode() );
                return renderer;
            }
        }
        return null;
    }

    public static String[] getNames() {
        ArrayList list = new ArrayList();
        XMLNode node = parseXml("reports.xml").getNode("reports");
        Enumeration reports = node.enumerateNode("report");
        while(reports.hasMoreElements()) {
            XMLNode reportNode = (XMLNode) reports.nextElement();
            list.add(reportNode.getAttr("name"));
        }
        return (String[]) list.toArray( new String[0] );
    }

    private static void applyNodes( Object obj, Enumeration nodes ) {
        while(nodes.hasMoreElements()) {
            XMLNode node = (XMLNode) nodes.nextElement();
            if(!node.isTag()) {
                continue;
            }
            String name = node.getName();
            if(name.equals("param")) {
                // special subtag for report
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

