package com.generationjava.swing;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.*;

/**
 * A object to live in a combo-box.
 *
 * @author bayard@generationjava.com
 * @date   2000-09-06
 */
public class JComboBoxItem {

    private String str = null;
    private Object obj = null;

    public JComboBoxItem(String str, Object obj) {
        setString(str);
        setObject(obj);
    }

    public String toString() {
        return getString();
    }

    public String getString() {
        return str;
    }

    public void setString(String str) {
        str = str;
    }

    public Object getObject() {
        return obj;
    }

    public void setObject(Object obj) {
        obj = obj;
    }

    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }

        if(o instanceof JComboBoxItem) {
            JComboBoxItem other = (JComboBoxItem)o;
            Object otherObj = other.getObject();
            if(otherObj == null) {
                return false;  // what if our obj is null?
            } else
            if(getObject() == null) {
                return false;  // what if our obj is null?
            } else {
                return (getObject().equals( other.getObject() ) ); 
            }
        }

        return false;
    }
}
