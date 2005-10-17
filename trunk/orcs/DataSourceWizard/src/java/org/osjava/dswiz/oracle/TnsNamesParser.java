/*
 * Created on Oct 10, 2005
 */
package org.osjava.dswiz.oracle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

/**
 * @author hyandell
 */
public class TnsNamesParser {
    
    public static void main(String[] args) throws Exception {
        File f = getDefaultFile();
        TnsName[] tnsNames = parseTnsNames(f);
        System.out.println(""+tnsNames);
    }
    
    public static File getDefaultFile() {
        return new File("C:/oracle/ora90/network/admin/tnsnames.ora");
    }
    
    public static TnsName[] parseTnsNames(File f) throws IOException {

        // Begin by treating the file as separate lines to throw out the comments
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String line;
        StringBuffer tnsText = new StringBuffer();
        while( (line = br.readLine()) != null ) {
            line = line.trim();
            if(line.startsWith("#") || line.equals("")) {
                continue;
            } else {
                tnsText.append(line);
            }
        }
        
        // Now switch to a streaming parser to get the actual data
        Map tnsNamesMap = new HashMap();

        // used to ascertain whether we are awaiting the RHS of an =
        boolean parsingValue=false;
        // used to indicate that we have finished a block and should either start
        // a new sibling block, or start a new tns block
        boolean endBlock=false;
        StringBuffer currentTnsKey = new StringBuffer();
        StringBuffer currentTnsValue = new StringBuffer();
        Map currentMap = tnsNamesMap;
        char[] tnsChars = tnsText.toString().toCharArray();
        int size = tnsChars.length;
        Stack mapStack = new Stack();
        for(int i=0; i<size; i++) {
            char ch = tnsChars[i];
            switch(ch) {
                case ' ' : {
                    break;
                }
                case '=' : {
                    parsingValue = true;
                    break;
                }
                case '(' : {
                    if(endBlock) {
                        endBlock = false;
                    }
                    if(parsingValue) {
                        Map newMap = new HashMap();
                        currentMap.put(currentTnsKey.toString(), newMap);
                        currentTnsKey.setLength(0);
                        mapStack.push(currentMap);
                        currentMap = newMap;
                        parsingValue = false;
                    }
                    break;
                }
                case ')' : {
                    if(parsingValue) {
                        currentMap.put(currentTnsKey.toString(), currentTnsValue.toString());
                        currentTnsKey.setLength(0);
                        currentTnsValue.setLength(0);
                        parsingValue = false;
                        endBlock = true;
                    } else {
                        currentMap = (Map) mapStack.pop();
                    }
                    break;
                }
                default  : {
                    if(parsingValue) {
                        currentTnsValue.append(ch);
                    } else {
                        if(endBlock) {
                            currentMap = (Map) mapStack.pop();
                            endBlock = false;
                        }
                        currentTnsKey.append(ch);
                    }
                    break;
                }
            }
        }
        
        TnsName[] tnsNames = new TnsName[tnsNamesMap.size()];
        
        Iterator iterator = tnsNamesMap.keySet().iterator();
        int i=0;
        while(iterator.hasNext()) {
            String name = (String) iterator.next();
            Map details = (Map) tnsNamesMap.get(name);
            tnsNames[i] = TnsName.createTnsName(name, details);
            i++;
        }
        
        return tnsNames;
    }
}
