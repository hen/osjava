package com.generationjava.swing;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.*;

// David Geary. Graphic Java Mastering the JFC Volume II: Swing
// Exmaple 18.4
public class IncrementalKeySelectionManager implements JComboBox.KeySelectionManager {

    static public void main(String[] args) {
        JFrame frame = new JFrame();
        Object[] opbs = new Object[] { "", "Apple", "Banana", "Zebra" };
        JComboBox box = new JComboBox(opbs);
        IncrementalKeySelectionManager mgr = new IncrementalKeySelectionManager(); 
        box.setKeySelectionManager(mgr);
        frame.getContentPane().add(box);
        frame.pack();
        frame.show();
    }

    private String searchString = new String();
    private long lastTime;
    private boolean lowerCase = false;

    public IncrementalKeySelectionManager() {
        this(true);
    }
    
    public IncrementalKeySelectionManager(boolean caseinsensitive) {
        lowerCase = caseinsensitive;
    }

    public int selectionForKey(char key,ComboBoxModel model) {
            updateSearchString(model, key);

            int start = getIndexAfter(model,getSelectedString(model));
            int selection = search(model, start);

            if(selection == -1 && start != 0)
                    selection = search(model, 0);

            return selection;
    }
    public String getSearchString() {
            return searchString;
    }
    private int search(ComboBoxModel model, int start) {
            for(int i=start; i < model.getSize(); ++i) {
                    String s = getStringAt(model, i);
                    if(lowerCase) {
                        s = s.toLowerCase();
                    }
                    int searchLength = searchString.length();

                    if(s.regionMatches(0,searchString,0,searchLength))
                            return i;
            }
            return -1;
    }
    private int getIndexAfter(ComboBoxModel model, String find) {
            int size = model.getSize();

            if(find != null) {
                    if(lowerCase) {
                        find = find.toLowerCase();
                    }
                    for(int i=0; i < size; ++i) {
                            String s = getStringAt(model, i);
                            if(lowerCase) {
                                s = s.toLowerCase();
                            }
                            if(s.compareTo(find) == 0) {
                                    return (i == size-1) ? 0 : i + 1;
                            }
                    }
            }
            return 0;
    }
    private String getStringAt(ComboBoxModel model, int index) {
            return (String)model.getElementAt(index);
    }
    private String getSelectedString(ComboBoxModel model) {
            return (String)model.getSelectedItem();
    }
    private void updateSearchString(ComboBoxModel model, char key) {
            long time = System.currentTimeMillis();

            if(time - lastTime < 500) searchString += key;
            else searchString = "" + key;

            lastTime = time;

            if(lowerCase) {
                searchString = searchString.toLowerCase();
            }
    }
}
