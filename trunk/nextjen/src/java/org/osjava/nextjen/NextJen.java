package org.osjava.nextjen;

import org.apache.commons.cli.*;
import java.io.File;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.xpath.*;
import org.w3c.dom.*;
import java.util.Map;
import java.util.HashMap;

/**
 * NextJenerator main class.
 */
public class NextJen 
{
    public static boolean verbose = true;

    public static void showHelp(Options options, String msg) {
        if(msg != null) {
            System.out.println(msg);
        } 
        HelpFormatter hf = new HelpFormatter();
        hf.printHelp("NextJen [-T <dir>] [-d <dir>] [-i] [-t <xsl>] [-s <xpath>] [-w <xpath>] <xml(s)>",options);
    }

    public static void showHelp(Options options) {
        showHelp(options,null);
    }

    public static void main( String[] args ) throws Exception
    {
        Options options = new Options();
        Option tmp;
        tmp=new Option("T","template-dir",true,"specify the template directory");
        options.addOption(tmp);
        tmp=new Option("d","dest-dir",true,"destination directory for generated files");
        options.addOption(tmp);
        tmp=new Option("i","ignore-pi",false,"ignore processing instructions in the xml source");
        options.addOption(tmp);
        tmp=new Option("t","template",true,"process with specified template");
        options.addOption(tmp);
        tmp=new Option("s","select",true,"select nodes with xpath expression");
        options.addOption(tmp);
        tmp=new Option("w","write-to",true,"write to the result of this xpath expression");
        options.addOption(tmp);
        tmp=new Option("h","help",false,"print help on command line arguments");
        options.addOption(tmp);
        Parser parser = new GnuParser();
        CommandLine cli = null;
        try {
            cli = parser.parse(options,args);
        } catch (ParseException pe) {
            showHelp(options, pe.getMessage());
            return;
        }
        args = cli.getArgs();
        if(cli.hasOption('h') || args.length == 0) {
            showHelp(options);
            return;
        }
        File templateDir = new File(cli.getOptionValue('T',"."));
        File destDir = new File(cli.getOptionValue('d',"."));
        NextJen gen = new NextJen(destDir, templateDir);
        if(!cli.hasOption('i')) {
            for(int i=0;i<args.length;i++) {
                gen.processXML(new File(args[0]));
            }
        }
        if(cli.hasOption('t')) {
            Map props = new HashMap();
            props.put("template",cli.getOptionValue('t'));
            if(cli.hasOption('s')) {
                props.put("select",cli.getOptionValue('s'));
            }
            if(cli.hasOption('w')) {
                props.put("writeTo",cli.getOptionValue('w'));
            }
            for(int i=0;i<args.length;i++) {
                gen.processXML(new File(args[0]));
            }
        }
        gen.processXML(new File(args[0]));
    }

    public File destDir; 
    public File templateDir;
    public XPath myXPath;
    public DocumentBuilder myDocumentBuilder;
    public TransformerFactory tf;
    public Transformer cloneTransform;

    /**
     * Create a new NextJen instance with the specified destination directory
     * and templateDir
     */
    public NextJen(
            File destDir, 
            File templateDir
            ) throws Exception 
    {
        this.destDir = destDir;
        this.templateDir = templateDir;
        XPathFactory xpf = XPathFactory.newInstance();
        myXPath = xpf.newXPath();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        myDocumentBuilder = dbf.newDocumentBuilder();
        tf = TransformerFactory.newInstance();
        cloneTransform = tf.newTransformer();
    }

    public void processXML(File source) throws Exception {
        if(verbose) System.out.println("processXML("+source.toURL()+") called");
        processXML(myDocumentBuilder.parse(source.toURL().toString()));
    }

    public void processXML(Node source) throws Exception {
        if(verbose) System.out.println("processXML() called with Document");
        XPathExpression xpe = myXPath.compile("processing-instruction('nextjen')");
        NodeList list = (NodeList) xpe.evaluate(source,XPathConstants.NODESET);
        if(verbose) System.out.println("Found: "+list.getLength()+" processing instructions");
        for(int i=0;i<list.getLength();i++) {
            Node node = list.item(i);
            Map m = parseProcessingInstruction(node.getNodeValue());
            processXML(source,m);
        }
    }

    public void processXML(File source, Map properties) throws Exception
    {
        if(verbose) System.out.println("processXML() called with File: "+source.toURL()+" and "+properties);
        processXML(
                myDocumentBuilder.parse(source.toURL().toString()),
                properties);
    }

    public void processXML(Node source, Map properties) throws Exception
    {
        if(verbose) System.out.println("processXML() called with Document and "+properties);
        XPathExpression writeTo = null;
        XPathExpression select = null;
        Transformer trans = null;
        String format = null;
        if(properties.containsKey("writeTo")) {
            writeTo = myXPath.compile((String) properties.get("writeTo"));
        }
        if(properties.containsKey("select")) {
            select = myXPath.compile((String) properties.get("select"));
        }
        if(properties.containsKey("template")) {
            File file = new File(templateDir,(String) properties.get("template"));
            trans = tf.newTransformer(new StreamSource(file));
        } else {
            trans = cloneTransform;
        }
        if(select == null) {
            if(writeTo == null) {
                DOMResult result = new DOMResult();
                trans.transform(new DOMSource(source), result);
                Node resultNode = result.getNode();
                processXML(resultNode);
            } else {
                String out = (String)
                    writeTo.evaluate(source,XPathConstants.STRING);
                File outFile = new File(destDir,out);
                File parentFile = outFile.getParentFile();
                if(!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                StreamResult sr = new StreamResult(outFile);
                trans.transform(new DOMSource(source), sr);
            }
        } else {
            NodeList list = (NodeList) 
                select.evaluate(source,XPathConstants.NODESET);
            for(int i=0;i<list.getLength();i++) {
                Node node = list.item(i);
                if(writeTo == null) {
                    DOMResult result = new DOMResult();
                    trans.transform(new DOMSource(node), result);
                    Node resultNode = result.getNode();
                    processXML(resultNode);
                } else {
                    String out = (String)
                        writeTo.evaluate(node,XPathConstants.STRING);
                    File outFile = new File(destDir,out);
                    File parentFile = outFile.getParentFile();
                    if(!parentFile.exists()) {
                        parentFile.mkdirs();
                    }
                    StreamResult sr = new StreamResult(outFile);
                    trans.transform(new DOMSource(node), sr);
                }
            }
        }
    }


    private static final int PI_PARSE_NONE = 0;
    private static final int PI_PARSE_IDENT = 1;
    private static final int PI_PARSE_EQUALS = 2;
    private static final int PI_PARSE_VALUE = 3;

    private Map parseProcessingInstruction(String val) {
        Map ret = new HashMap();
        StringBuffer tmp = new StringBuffer();
        String name = null;
        int state = PI_PARSE_NONE;
        for(int i=0;i<val.length();i++) {
            char ch = val.charAt(i);
            switch(state) {
                case PI_PARSE_NONE:
                    if(!Character.isWhitespace(ch)) {
                        if(Character.isLetter(ch)) {
                            tmp.append(ch);
                            state = PI_PARSE_IDENT;
                        } else {
                            throw new RuntimeException("Broken processing instruction");
                        }
                    }
                    break;
                case PI_PARSE_IDENT:
                    if('=' == ch) {
                        name = tmp.toString();
                        tmp.setLength(0);
                        state = PI_PARSE_EQUALS;
                    } else if(Character.isLetterOrDigit(ch)) {
                        tmp.append(ch);
                    } else {
                        throw new RuntimeException("Broken processing instruction");
                    }
                    break;
                case PI_PARSE_EQUALS:
                    if('"' == ch) {
                        state = PI_PARSE_VALUE;
                    } else {
                        throw new RuntimeException("Broken processing instruction");
                    }
                    break;
                case PI_PARSE_VALUE:
                    if('"' == ch) {
                        ret.put(name, tmp.toString());
                        tmp.setLength(0);
                        state = PI_PARSE_NONE;
                    } else {
                        tmp.append(ch);
                    }
                    break;
            }
        }
        return ret;
    }
}
