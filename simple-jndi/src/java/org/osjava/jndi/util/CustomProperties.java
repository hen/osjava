/// TODO: Refactor this out
package com.generationjava.jndi.util;

import java.io.*;
import java.util.*;
import org.apache.commons.collections.IteratorUtils;

public class CustomProperties extends Properties {

    public synchronized void load(InputStream in) throws IOException {
        try {
            BufferedReader reader = new BufferedReader( new InputStreamReader(in) );
            String line = "";
            String nextLine = null;
            while( (line = reader.readLine()) != null) {

                // we may already be on a multi-line statement.
                if(nextLine != null) {
                    line = nextLine + line;
                    nextLine = null;
                }

                line = line.trim();
                if(line.endsWith("\\")) {
                    nextLine = line;
                    continue;
                }

                int idx = line.indexOf('#');
                // remove comment
                if(idx != -1) {
                    line = line.substring(0,idx);
                }
                // split equals sign
                idx = line.indexOf('=');
                if(idx != -1) {
//                    System.err.println("Setting: "+line.substring(0,idx)+"="+line.substring(idx+1));
                    this.setProperty(line.substring(0,idx), line.substring(idx+1));
                } else {
                    // blank line, or just a bad line
                    // we ignore it
                }
            }
            reader.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public synchronized Object put(Object key, Object value) {
        if(index.contains(key)) {
            Object obj = get(key);
            if( !(obj instanceof List)) {
                List list = new LinkedList();
                list.add(obj);
                obj = list;
            } 
            ((List)obj).add(value);
            value = obj;
        }
        if(!index.contains(key)) {
//            System.err.println("Updating index for: "+key);
            index.add(key);
        }
//        System.err.println("Really setting: "+key+"="+value);
        return super.put(key, value);
    }

    // our index for the ordering
    protected ArrayList index = new ArrayList();

    public CustomProperties() {
        super();
    }

    // the props attribute is for defaults. These will need to be 
    // remembered for the save/store method.
    public CustomProperties(Properties props) {
        super(props);
    }

    public synchronized Object setProperty(String key, String value) {
        return put(key,value);
    }
    
    public synchronized Object remove(Object key) {
        index.remove(key);
        return super.remove(key);
    }
    
    // simple implementation that depends on keySet.
    public synchronized Enumeration propertyNames() {
        return IteratorUtils.asEnumeration(keySet().iterator());
    }
    public synchronized Enumeration keys() {
        return propertyNames();
    }
    
    public synchronized Set keySet() {
        return new OrderedSet(index);
    }
 
    /**
     * Currently will write out defaults as well, which is not 
     * in the specification.
     */
    public void save(OutputStream outstrm, String header) {
        super.save(outstrm,header);
    }
    /**
     * Currently will write out defaults as well, which is not 
     * in the specification.
     */
    public void store(OutputStream outstrm, String header) throws IOException {
        super.store(outstrm,header);
    }

}
