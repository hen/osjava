package com.generationjava.swing;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.*;

/**
 * A combobox that changes the selected item as you type.
 *
 * @author bayard@generationjava.com
 * @date   2000-09-04
 */
public class IncrementalComboBox extends JComboBox {

    static public void main(String[] args) {
        JFrame frame = new JFrame();
        Object[] opbs = new Object[] { "", 
                                       "Aaron",
                                       "Apathy", 
                                       "Apple", 
                                       "Atelier",
                                       "Ban" ,
                                       "Banana", 
                                       "Barings", 
                                       "Barracuda",
                                       "Fred",
                                       "Zebra",
                                       "Zulu"
                                       };
        JComboBox box = new IncrementalComboBox(opbs);
        frame.getContentPane().add(box);
        frame.pack();
        frame.show();
    }

    public IncrementalComboBox() {
        super();
        initIncrementalComboBox();
    }

    public IncrementalComboBox(Object[] obs) {
        super(obs);
        initIncrementalComboBox();
    }

    public void initIncrementalComboBox() {
        setEditable(true);

        this.getEditor().getEditorComponent().addKeyListener( new KeyAdapter() {

            // released means it happens after the change is made 
            // to the TextField
            public void keyReleased(KeyEvent ke) {
//                if(ke.isControlDown() ||
//                   ke.isMetaDown() ||
//                   ke.isAltDown()
//                  ) 
//                {
//                    // don't interfere with special stuff
//                    return;
//                }
                Object obj = ke.getSource();
                if(obj instanceof JTextField) {
                    char chr = ke.getKeyChar();
                    if(chr == (char)KeyEvent.VK_DELETE ) {
                        // allow delete full reign
                        return;
                    }
                    JTextField field = (JTextField)obj;
                    String text = field.getText();
                    int car = field.getCaretPosition();
                    text = text.substring(0,car);
                    if(chr == (char)KeyEvent.VK_BACK_SPACE ) {
                        // auto-magically remove all after cursor
                        IncrementalComboBox.this.setSelectedItem(text);
                        return;
                    }
                    int index = IncrementalComboBox.this.find(IncrementalComboBox.this.getModel(),text);
                    if(index != -1) {
                        field.setText( getStringAt(IncrementalComboBox.this.getModel(),index) );
                        IncrementalComboBox.this.setSelectedIndex(index);
                    } else {
                        IncrementalComboBox.this.setSelectedItem(text);
                    }
                    int pos = text.length();
                    field.setCaretPosition(pos);
                }
            }
        } );

    }

    public int find(ComboBoxModel model, String str) {
        str = str.toLowerCase();
        for(int i=0; i < model.getSize(); i++) {
            String s = getStringAt(model, i).toLowerCase();
            int searchLength = str.length();
            if(searchLength == 0) {
                continue;
            }

            if(s.regionMatches(0,str,0,searchLength)) {
                    return i;
            }
        }
        return -1;
    }

    public String getStringAt(ComboBoxModel model, int idx) {
        return (String)model.getElementAt(idx);
    }
}
