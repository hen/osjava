package com.generationjava.apps.jpe;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Config handler reads, keeps and stores all the states within
 * JPE. The current load/storage model is based on a XML file
 * format.
 */
public class ConfigHandler extends AbstractHandler {

        // Current Config file        (config.xml)
        OpenFile file;

        // Hashtable with Vectors of Hashtables per xml type
        Hashtable props=new Hashtable();

        // Construct new Config handler
        public ConfigHandler(JPE jpe) {
                super("Config",jpe);

                // Open the config file within the defined path in getMyPath()
                file=new OpenFile(getJPE().getMyPath()+"\\config.xml");
                if (file!=null) {

                        // read the XML based config file.
                        props=readXMLConfig(file);
                }
        }


        /**
        * get property within named node with given key
        * will only return the first one !!
        */
        public String getProperty(String nodename, String key) {

                // get the correct Vector
                Object obj=props.get(nodename);
                if (obj!=null) {
                        
                        // check if found object is a Vector (should be)
                        if (obj instanceof Vector) {
                
                                // So its a Vector lets cast it one.
                                Vector vec=(Vector)obj;

                                // obtain the _first_ Hashtable with keys/values from Vector
                                Hashtable onenode=(Hashtable)vec.elementAt(0);

                                // return the value defined by the wanted key
                                return((String)onenode.get(key));
                        }
                }
                return(null);        
        }


        /**
        *  store a new value for variable defined by key in the connect
        *  xml node (defined by nodename)
        */
        public void putProperty(String nodename, String key, String value) {
                // did we allready have a node under this type ?
                Object obj=props.get(nodename);
                if (obj!=null) {
                        if (obj instanceof Vector) {
                                // yes, then update this node
                                Vector vec=(Vector)obj;
                                Hashtable onenode=(Hashtable)vec.elementAt(0);
                                onenode.put(key,value);
                        }
                } else {
                        // no, create a Vector and store the key/value
                        Vector vec=new Vector();
                        Hashtable onenode=new Hashtable();
                        onenode.put(key,value);        
                }        
                // sync the changed to disk
                syncConfigFile();
        }

        /**
        * store a new properties vector, watch out replaces all nodes
        * of the given type !
        */
        public void putProperties(String nodename, Vector values) {
                props.put(nodename,values);
                // sync to disk ?
                syncConfigFile();
        }

        /**
        * get the properties vector of the wanted type
        */
        public Vector getProperties(String nodename) {
                // get the object rom our props
                Object obj=props.get(nodename);
                if (obj!=null) {
                        // object found is it a vector (should be)
                        if (obj instanceof Vector) {
                                // cast it to Vector and return it
                                Vector vec=(Vector)obj;
                                return(vec);
                        }
                }
                // not vector found under the wanted name return null
                return(null);        
        }


        /**
        * convert the openfile to props (expects the file to be
        * xml). Very basic xml reader will be replaced in time but
        * works for now)
        */
        public static Hashtable readXMLConfig(OpenFile file) {
                Hashtable results=new Hashtable();
                String body=file.getText();
                int pos=body.indexOf("<?xml");
                int count=0;
                while (pos!=-1) {
                        String xmlpart=body.substring(pos);
                        int nextpos=body.indexOf("<?xml",6);
                        if (nextpos!=-1) {
                                body=xmlpart.substring(nextpos);
                                xmlpart=xmlpart.substring(0,nextpos);
                        } else {
                                body="";
                        }
                        Hashtable onenode=parseOneXML(xmlpart);
                        String nodename=(String)onenode.get("nodename");
                        if (!results.containsKey(nodename)) {
                                Vector vec=new Vector();
                                vec.addElement(onenode); 
                                results.put(nodename,vec);
                        } else {
                                // handle multiple in a vector
                                Vector vec=(Vector)results.get(nodename);
//                                vec.addElement(onenode);
                                vec.insertElementAt(onenode,0);
                        }
                        pos=body.indexOf("<?xml");
                }
                return(results);
        }

        /**
        * parse one xml node to its hashtable
        */
        private static Hashtable parseOneXML(String body) {
                Hashtable results=new Hashtable();

                StringTokenizer tok = new StringTokenizer(body,"\n\r");
                String xmlline=tok.nextToken();
                String docline=tok.nextToken();
                
        
                String builderline=tok.nextToken();
                String endtoken="</"+builderline.substring(1);

                results.put("nodename",builderline.substring(1,builderline.length()-1));                

                String nodedata=body.substring(body.indexOf(builderline)+builderline.length());
                nodedata=nodedata.substring(0,nodedata.indexOf(endtoken));

                int bpos=nodedata.indexOf("<");
                while (bpos!=-1) {
                        String key=nodedata.substring(bpos+1);
                        key=key.substring(0,key.indexOf(">"));
                        String begintoken="<"+key+">";
                        endtoken="</"+key+">";
                        
                        String value=nodedata.substring(nodedata.indexOf(begintoken)+begintoken.length());
                        value=value.substring(0,value.indexOf(endtoken));

                        results.put(key,value);
                
                        nodedata=nodedata.substring(nodedata.indexOf(endtoken)+endtoken.length());
                        bpos=nodedata.indexOf("<");
                }
                return(results);
        }

        /**
        * save currect props file back to disk as xml.
        */
        public void syncConfigFile() {
                String body="";
                Enumeration e=props.keys();
                while (e.hasMoreElements()) {
                        String key=(String)e.nextElement();
                        Vector vec=(Vector)props.get(key);
                        Enumeration f=vec.elements();
                        while (f.hasMoreElements()) {
                                Hashtable onenode=(Hashtable)f.nextElement();
                                String nodename=(String)onenode.get("nodename");
                                body+="<?xml version=\"1.0\"?>\n";
                                body+="<!DOCTYPE jpe."+nodename+" SYSTEM \"http://www.submarine.nl/jpe/"+nodename+".dtd\">\n";
                                body+="<"+nodename+">\n";
                                Enumeration g=onenode.keys();
                                while (g.hasMoreElements()) {
                                        String fkey=(String)g.nextElement();
                                        String fvalue=(String)onenode.get(fkey);
                                        if (!fkey.equals("nodename")) {
                                                body+="<"+fkey+">"+fvalue+"</"+fkey+">\n";
                                        }
                                }                                
                                body+="</"+nodename+">\n\n";
                        }                        
                }
                file.setText(body);
                file.saveFile();
        }

}
