package org.osjava.scraping.util;

import com.generationjava.lang.ClassW;

public class FactoryUtils {

    static private String NULL_NAME = "Null";

    static public Object getObject(String name, String type) {
        if(name != null) {
            Object obj = ClassW.createObject("org.osjava.scraping."+name+type);
            // should it check the type? it assumes we have a 
            // clue what the interface is...
            if(obj != null) {
                return obj;
            }
            obj = ClassW.createObject(name+type);
            if(obj != null) {
                return obj;
            }
            obj = ClassW.createObject(name);
            if(obj != null) {
                return obj;
            }
        }
        if(name == NULL_NAME) {
            // if it couldn't find the Null pattern for the type
            return null;
        } else {
            return getObject(NULL_NAME, type);
        }
    }

}
