package com.generationjava.swing;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.net.URL;
import java.net.MalformedURLException;

import com.generationjava.io.FileW;
import org.apache.commons.lang.StringUtils;
import com.generationjava.net.UrlW;

/**
 * An incrementing combobox which asks a url for the 
 * values to show at each point.
 *
 * @author bayard@generationjava.com
 * @date   2001-10-21
 */
public class DelegatingIncrementalComboBox extends IncrementalComboBox implements Cloneable {

    static public void main(String[] args) {
        JFrame frame = new JFrame();
        Object[] common = new Object[] {
            "",
//            "London",
//            "New York",
//            "Paris",
//            "Frankfort",
//            "Hong Kong"
            "LONDON, GB",
            "DUBLIN, IE",
            "CHICAGO, IL"
        };
        String url = "http://www.generationjava.com:8013/inon-demo/getData.jsp";
        JComboBox box = new DelegatingIncrementalComboBox(url, 20, common, "demo");
        frame.getContentPane().add(box);
        frame.pack();
        frame.show();
    }

    private String url;
    private int n;       // the maximum number to have in the list
    private String name;  // name of this component. it sends the name
                          // with each request

    public DelegatingIncrementalComboBox(String url, int n, String name) {
        super();
        initUrl(url, n, name);
        this.n = n;
        this.name = name;
        // immediately call url and ask for n.
        find(this.getModel(), "");
    }

    public DelegatingIncrementalComboBox(String url, int n, Object[] obs, String name) {
        super(obs);
        initUrl(url, n, name);
        this.n = n;
        this.name = name;
    }

    public void initUrl(String url, int n, String name) {
        this.url = url+"?capacity="+n+"&name="+name;
    }

    public int find(ComboBoxModel model, String str) {
        // ask URL for the next N of the data.
        // but only if model.size is equal to n.
//        if(model.getSize() == this.n) {
            String urltmp = this.url + "&chrs="+str;
            try {
                String data = UrlW.getContent(urltmp).toString();
                String[] values = StringUtils.split(data, "|");
                int sz = values.length;
                DefaultComboBoxModel dcbm = (DefaultComboBoxModel)model;
                dcbm.removeAllElements();
                for(int i=0; i<sz; i++) {
                    dcbm.addElement(values[i]);
                }
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
//        } else {
//            System.err.println("Didn't get capacity back from server last time.");
//        }

        return super.find(model, str);
    }

    public DelegatingIncrementalComboBox cloneObject() {
        ListModel model = this.getModel();
        int size = model.getSize();
        Object[] objs = new Object[size];
        for(int i=0; i<size; i++) {
            objs[i] = model.getElementAt(i);
        }

        DelegatingIncrementalComboBox box = new DelegatingIncrementalComboBox(this.url, this.n, objs, this.name);
        /*
        try {
            return (DelegatingIncrementalComboBox)super.clone();
        } catch(CloneNotSupportedException cnse) {
            cnse.printStackTrace();
            return null;
        }
        */
        return box;
    }
}
