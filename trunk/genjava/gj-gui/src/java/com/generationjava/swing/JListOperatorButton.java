package com.generationjava.swing;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.*;

/**
 * A button which cuts an item from one JList and transfers it 
 * to another JList.
 *
 * @author bayard@generationjava.com
 * @date   2000-09-05
 */
public class JListOperatorButton extends JButton {

    // no idea of a clipboard.
    // copy and cut require two JList's, delete just the one.
    public static Object COPY    = new Object();
    public static Object CUT     = new Object();
    public static Object DELETE  = new Object();

    private JList  sourceList  = null;
    private JList  targetList    = null;
    private Object opType     = null;

    static public void main(String[] strs) {
        JFrame frame = new JFrame("Test JListOperatorCut");
        Object[] objs = new Object[] { "Apple", "Banana", "Zebra" };
        DefaultListModel model = new DefaultListModel();
        for(int i=0;i<objs.length;i++) {
            model.addElement(objs[i]);
        }
        JList list1 = new JList( model );
        JList list2 = new JList( new DefaultListModel() );
        frame.getContentPane().setLayout( new FlowLayout() );
        frame.getContentPane().add( list1 );
        frame.getContentPane().add( new JListOperatorButton("Add",list1,list2,CUT) );
        frame.getContentPane().add( new JListOperatorButton("Remove",list2,list1,CUT) );
        frame.getContentPane().add( list2 );
        frame.pack();
        frame.show();
    }

    public JListOperatorButton(JList sourcelist) {
        this(sourcelist,null);
    }
    public JListOperatorButton(JList sourcelist, JList targetlist) {
        this("Transfer",sourcelist,targetlist, CUT);
    }
    public JListOperatorButton(String name, JList sourcelist) {
        this(name,sourcelist,null);
    }
    public JListOperatorButton(String name, JList sourcelist, JList targetlist) {
        this(name,sourcelist,targetlist, CUT);
    }
    public JListOperatorButton(String name, JList sourcelist, Object type) {
        this(name,sourcelist,null,type);
    }
    public JListOperatorButton(String name, JList sourcelist, JList targetlist, Object type) {
        super(name);
        opType      = type;
        sourceList = sourcelist;
        targetList   = targetlist;
        this.addActionListener( new ActionListener() {
            
            public void actionPerformed(ActionEvent ae) {
                JListOperatorButton.this.move();
            }

        } );
    }

    public void move() {
        int[] selec = sourceList.getSelectedIndices();
        DefaultListModel sourceListModel = (DefaultListModel)sourceList.getModel();
        DefaultListModel targetListModel = (DefaultListModel)targetList.getModel();

        // by going from the top we don't affect the index values when 
        // we remove an element
        for(int i=selec.length;i>0;i--) {
            Object element = sourceListModel.getElementAt(selec[i-1]);
            if(!opType.equals(COPY)) {
                sourceListModel.removeElementAt(selec[i-1]);
            }
            if(!opType.equals(DELETE)) {
                targetListModel.addElement(element);
            }
        }
        sourceList.revalidate();
        targetList.revalidate();
    }

    public JList getSourceList() {
        return sourceList;
    }

    public void setSourceList(JList list) {
        sourceList = list;
    }

    public JList getTargetList() {
        return targetList;
    }

    public void setTargetList(JList list) {
        targetList = list;
    }
}
