package com.generationjava.apps.jpe;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class TextController {

    static private TextController self = new TextController(true); 
    
    // gets latest instance 
    static public TextController getInstance() {
        return self;
    }
    
    private ResourceBundle bundle;
    
    public TextController(boolean b) {
           try {
                this.bundle = ResourceBundle.getBundle("com.generationjava.apps.jpe.text");
            } catch(MissingResourceException mre) {
                mre.printStackTrace();
            }
    }

    public TextController() {
    }
    
    public String getText(String key) {
        if(bundle == null) {
            return "mre:"+key;
        }
        try {
            return (String)this.bundle.getString(key);
        } catch(MissingResourceException mre) {
            return "mre-x:"+key;
        }
    }

    public String getText(String key, Object value) {
        Object[] objs = new Object[1];
        objs[0] = value;
        return getText(key, objs);
    }

    public String getText(String key, Object value1, Object value2) {
        Object[] objs = new Object[2];
        objs[0] = value1;
        objs[1] = value2;
        return getText(key, objs);
    }

    public String getText(String key, Object[] vals) {
        String tmp = getText(key);
        return MessageFormat.format(tmp,vals);
    }

}
