package org.osjava.reportrunner;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;
import org.apache.commons.lang.StringUtils;

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
            applyResourceParamTag( group, groupNode.enumerateNode("resource-param"), resourcesMap );
            groups.add(group);
        }
        return (ReportGroup[]) groups.toArray( new ReportGroup[0] );
    }
    public static Map getResources() {
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
        XMLNode node = parseXml( filename ).getNode("reports");
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
        XMLNode node = parseXml( filename ).getNode("reports");
        Enumeration reportNodes = node.enumerateNode("report");
        while(reportNodes.hasMoreElements()) {
            XMLNode reportNode = (XMLNode) reportNodes.nextElement();
            String className = reportNode.getAttr("class");
            Report report = (Report) ClassW.createObject(className);
            applyAttrs( report, reportNode, new String[] { "class" } );
            applyNodes( report, reportNode.enumerateNode(), new String[] { "param", "column", "renderer", "columns", "renderers", "variant" } );
            applyParamTag( report, reportNode.enumerateNode("param") );
            applyVariantTag( report, reportNode.enumerateNode("variant") );
            applyColumnTags( report, reportNode );    // "column" and "columns"
            applyRendererTags( report, reportNode );  // "renderer" and "renderers"
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
                report.addParam( createParam(node) );
            }
        }
    }

    private static void applyVariantTag( Report report, Enumeration nodes ) {
        while(nodes.hasMoreElements()) {
            XMLNode node = (XMLNode) nodes.nextElement();
            String name = node.getName();
            if(name.equals("variant")) {
                Variant variant = new Variant();
                variant.setName( node.getAttr("name") );
                variant.setLabel( node.getAttr("label") );
                Enumeration options = node.enumerateNode("option");
                while(options.hasMoreElements()) {
                    XMLNode optionNode = (XMLNode) options.nextElement();
                    VariantOption option = new VariantOption();
                    option.setName( optionNode.getAttr("name") );
                    option.setLabel( optionNode.getAttr("label") );
                    option.setValue( optionNode.getAttr("value") );
                    Enumeration params = optionNode.enumerateNode("param");
                    while(params.hasMoreElements()) {
                        option.addParam( createParam( (XMLNode) params.nextElement() ) );
                    }
                    variant.addOption(option);
                }
                report.addVariant(variant);
            }
        }
    }

    private static void applyResourceParamTag( ReportGroup group, Enumeration nodes, Map resourcesMap ) {
        while(nodes.hasMoreElements()) {
            XMLNode node = (XMLNode) nodes.nextElement();
            String name = node.getName();
            if(name.equals("resource-param")) {
                // HACK: Same xml is modeled differently currently
                if(node.getAttr("value") != null) {
                    group.putResource( node.getAttr("name"), (Resource) resourcesMap.get(node.getAttr("value")) );
                } else {
                    group.addResourceParam( createParam(node) );
                }
            }
        }
    }

    private static Param createParam( XMLNode node ) {
        Param param = new Param();
        applyAttrs( param, node, new String[] { "parser", "pattern" } );
        if(node.getAttr("parser") != null) {
            try {
                Parser parser = (Parser) Thread.currentThread().getContextClassLoader().loadClass(node.getAttr("parser")).newInstance();
                parser.setPattern( node.getAttr("pattern") );
                param.setParser(parser);
            } catch(ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            } catch(InstantiationException ie) {
                ie.printStackTrace();
            } catch(IllegalAccessException iae) {
                iae.printStackTrace();
            }
        }
        return param;
    }

    // move <renderers> into here too
    private static void applyRendererTags( Report report, XMLNode reportNode ) {
        Renderer[] renderers = ReportFactory.getRenderers();
        Enumeration nodes = reportNode.enumerateNode();
        while(nodes.hasMoreElements()) {
            XMLNode node = (XMLNode) nodes.nextElement();
            String name = node.getName();
            if(name.equals("renderers")) {
                String[] rendererNames = StringUtils.split(node.getValue(), ",");
                for(int i=0; i<rendererNames.length; i++) {
                    for(int j=0; j<renderers.length; j++) {
                        if(rendererNames[i].equals(renderers[j].getName())) {
                            // need to clone renderer
                            report.addRenderer(renderers[j]);
                            break;
                        }
                    }
                }
            } else
            if(name.equals("renderer")) {
                String rendererName = node.getAttr("name");
                for(int i=0; i<renderers.length; i++) {
                    Renderer renderer = renderers[i];
                    if(rendererName.equals(renderer.getName())) {
                        // need to clone renderer
                        applyAttrs(renderer, node, new String[0]);
                        Enumeration vars = node.enumerateNode("variable");
                        while(vars.hasMoreElements()) {
                            XMLNode var = (XMLNode) vars.nextElement();
                            renderer.setVariable( var.getAttr("name"), var.getAttr("value") );
                        }
                        report.addRenderer(renderer);
                        break;
                    }
                }

            }
        }
    }

    // Can either be <column> or <columns>
    private static void applyColumnTags( Report report, XMLNode reportNode ) {
        Enumeration nodes = reportNode.enumerateNode();
        while(nodes.hasMoreElements()) {
            XMLNode node = (XMLNode) nodes.nextElement();
            String name = node.getName();
            if(name.equals("columns")) {
                String[] columns = StringUtils.split(node.getValue(), ",");
                for(int i=0; i<columns.length; i++) {
                    Column column = new Column();
                    column.setName( columns[i] );
                    column.setLabel( columns[i] );
                    report.addColumn(column);
                }
            } else
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
                        cnfe.printStackTrace();
                    } catch(InstantiationException ie) {
                        ie.printStackTrace();
                    } catch(IllegalAccessException iae) {
                        iae.printStackTrace();
                    }
                }
                report.addColumn( column );
            }
        }
    }

    private static XMLNode parseXml(String file) {
        if(file.startsWith("classpath:")) {
            file = file.substring("classpath:".length());
            return parseXmlFromClasspath( file );
        }

        if(file.startsWith("file:")) {
            file = file.substring("file:".length());
        }

        File f = new File(file);
        if(f.exists()) {
            return parseXmlFromFile( f );
        } else {
            return parseXmlFromClasspath( file );
        }
    }
    private static XMLNode parseXmlFromFile(File file) {
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
    private static XMLNode parseXmlFromClasspath(String file) {
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

