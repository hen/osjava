package org.osjava.reportrunner;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

public class ReportFactory {

    public static Report getReport(String groupName, String reportName) {
        Report[] reports = getReports(groupName);
        for(int i=0; i<reports.length; i++) {
            if(reports[i].getName().equals(reportName)) {
                return reports[i];
            }
        }
        return null;
    }
    public static ReportGroup getReportGroup(String groupName) {
        ReportGroup[] groups = getReportGroups();
        for(int i=0; i<groups.length; i++) {
            if(groups[i].getName().equals(groupName)) {
                return groups[i];
            }
        }
        return null;
    }
    public static ReportGroup[] getReportGroups() {
        Map resourcesMap = getResources();
        List groups = new ArrayList();
        XMLNode node = parseXml("reportrunner.xml").getNode("reportrunner");
        Enumeration groupNodes = node.enumerateNode("reports");
        while(groupNodes.hasMoreElements()) {
            XMLNode groupNode = (XMLNode) groupNodes.nextElement();
            ReportGroup group = new ReportGroup();
            applyAttrs(group, groupNode, new String[] { "resources" } );

            Enumeration useNodes = groupNode.enumerateNode("use");
            while(useNodes.hasMoreElements()) {
                XMLNode useNode = (XMLNode) useNodes.nextElement();
                group.putResource( useNode.getAttr("name"), (Resource) resourcesMap.get(useNode.getAttr("resource")) );
            }
            groups.add(group);
        }
        return (ReportGroup[]) groups.toArray( new ReportGroup[0] );
    }
    private static Map getResources() {
        HashMap map = new HashMap();
        XMLNode node = parseXml("resources.xml").getNode("resources");
        Enumeration resourceNodes = node.enumerateNode("resource");
        while(resourceNodes.hasMoreElements()) {
            XMLNode resourceNode = (XMLNode) resourceNodes.nextElement();
            Resource resource = new Resource();
            applyAttrs(resource, resourceNode, new String[0]);
            map.put(resource.getName(), resource);
        }
        return map;
    }
    public static String getReportAsXml(String groupName, String reportName) {
        ReportGroup group = getReportGroup(groupName);
        if(group == null) {
            throw new RuntimeException("Illegal group somehow chosen. ");
        }

        String filename = group.getFilename();
        XMLNode node = parseXml( new File(filename) ).getNode("reports");
        Enumeration reportNodes = node.enumerateNode("report");
        while(reportNodes.hasMoreElements()) {
            XMLNode reportNode = (XMLNode) reportNodes.nextElement();
            if(reportNode.getAttr("name").equals(reportName)) {
                return reportNode.toString();
            }
        }
        throw new RuntimeException("Illegal report somehow chosen. ");
    }
    public static Report[] getReports(String groupName) {
        ReportGroup group = getReportGroup(groupName);
        if(group == null) {
            throw new RuntimeException("Illegal group somehow chosen. ");
        }

        String filename = group.getFilename();
        List reports = new ArrayList();
        XMLNode node = parseXml( new File(filename) ).getNode("reports");
        Enumeration reportNodes = node.enumerateNode("report");
        while(reportNodes.hasMoreElements()) {
            XMLNode reportNode = (XMLNode) reportNodes.nextElement();
            String className = reportNode.getAttr("class");
            Report report = (Report) ClassW.createObject(className);
            report.setName(reportNode.getAttr("name"));
            report.setLabel(reportNode.getAttr("label"));
            applyNodes( report, reportNode.enumerateNode(), new String[] { "param", "column" } );
            applyParamTag( report, reportNode.enumerateNode("param") );
            applyColumnTag( report, reportNode.enumerateNode("column") );
            report.setReportGroup(group);
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
            applyNodes( renderer, rendererNode.enumerateNode(), new String[0] );
            renderers.add(renderer);
        }
        return (Renderer[]) renderers.toArray( new Renderer[0] );
    }

    private static void applyAttrs( Object obj, XMLNode node, String[] ignore ) {
        Enumeration attrs = node.enumerateAttr();
LABEL:  while(attrs.hasMoreElements()) {
            String name = (String) attrs.nextElement();
            for(int i=0; i<ignore.length; i++) {
                if(name.equals(ignore[i])) {
                    continue LABEL;
                }
            }
            String value = node.getAttr(name);
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
    private static void applyNodes( Object obj, Enumeration nodes, String[] ignore ) {
LABEL:  while(nodes.hasMoreElements()) {
            XMLNode node = (XMLNode) nodes.nextElement();
            if(!node.isTag()) {
                continue;
            }
            String name = node.getName();
            for(int i=0; i<ignore.length; i++) {
                if(name.equals(ignore[i])) {
                    continue LABEL;
                }
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
                Param param = new Param();
                param.setName( node.getAttr("name") );
                param.setLabel( node.getAttr("label") );
                param.setType( node.getAttr("type") );
                param.setBinding( node.getAttr("binding") );
                if(node.getAttr("parser") != null) {
                    try {
                        Parser parser = (Parser) Thread.currentThread().getContextClassLoader().loadClass(node.getAttr("parser")).newInstance();
                        parser.setPattern( node.getAttr("pattern") );
                        param.setParser(parser);
                    } catch(ClassNotFoundException cnfe) {
                    } catch(InstantiationException ie) {
                    } catch(IllegalAccessException iae) {
                    }
                }
                report.addParam( param );
            }
        }
    }

    private static void applyColumnTag( Report report, Enumeration nodes ) {
        while(nodes.hasMoreElements()) {
            XMLNode node = (XMLNode) nodes.nextElement();
            String name = node.getName();
            if(name.equals("column")) {
                Column column = new Column();
                column.setName( node.getAttr("name") );
                column.setLabel( node.getAttr("label") );
                if(node.getAttr("formatter") != null) {
                    try {
                        Formatter formatter = (Formatter) Thread.currentThread().getContextClassLoader().loadClass(node.getAttr("formatter")).newInstance();
                        formatter.setPattern( node.getAttr("pattern") );
                        column.setFormatter(formatter);
                    } catch(ClassNotFoundException cnfe) {
                    } catch(InstantiationException ie) {
                    } catch(IllegalAccessException iae) {
                    }
                }
                report.addColumn( column );
            }
        }
    }

    private static XMLNode parseXml(File file) {
        Reader reader = null;
        try {
            XMLParser parser = new XMLParser();
            reader = new FileReader( file );
            return parser.parseXML(reader);
        } catch(IOException ioe) {
            throw new RuntimeException("XML failed to be parsed: "+ioe, ioe);
        } finally {
            try { if(reader != null) { reader.close(); } } catch(IOException ioe) { ; }
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

