package com.generationjava.swing;

import javax.swing.JDialog;
import javax.swing.JFrame;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;
import java.util.Collection;
import java.awt.LayoutManager;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.Icon;

import javax.swing.JPanel;
import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// DialogEntry
import java.awt.Component;
// StringDialogEntry
import javax.swing.JTextField;
// BooleanDialogEntry
import javax.swing.JCheckBox;
// Object[]DialogEntry
import javax.swing.JComboBox;
// CollectionDialogEntry
//import java.lang.reflect.Constructor;
import java.util.Collection;
import javax.swing.JList;

/**
 * A class for easily creating configuration/option dialogs.
 * 
 * @version 0.9 A finished alpha version. The plus points are that data is
 *              supplied as normal data types and returned more or less the same.
 *              The Event/Listener model used seems fine. The negative side is that 
 *              it doesn't feel to be the right separation. It also feels clunky.
 *
 * TODO: Properly Javadoc,
 *       Split into class files.
 *       Work out how to get data out again.
 *       Make it serializable. Ensure JavaBeanable.
 */

public class AutoOptionDialog extends JDialog {

    static public void main(String[] args) {
        Map map = new HashMap();
        map.put("Test","Whee");
        map.put("And",new Boolean(true));
        Object[] bob = new Object[3];
        bob[0] = "Hey";
        bob[1] = "You";
        bob[2] = "Fred!";
        map.put("Message",bob);
        java.util.ArrayList li = new java.util.ArrayList();
        for(int i=0;i<10;i++)
            li.add(""+i);
        map.put("Maybe",li);
        JFrame jf = new JFrame();
        AutoDialogListener adl = new AutoDialogListener() {
            public void dialogApplied(AutoDialogEvent ade) {
                System.out.println(""+ade.getDataMap());
            }
        };
        //JDialog jd = new AutoOptionDialog(jf,"TEST",true,map,new java.awt.FlowLayout());
        JDialog jd = new AutoOptionDialog(jf,adl,"TEST",true,map);
        jd.pack();
        jd.show();
    }

    private Map                myData     = null;
    private AutoDialogListener myListener = null;

    public AutoOptionDialog(JFrame owner, AutoDialogListener adl, String title, boolean modal, Map data, LayoutManager layout) {
        super(owner,title,modal);
        myData   = preParse(data);   // bother storing this?
        myListener = adl;
        initDialog(myData, layout);
    }

    public AutoOptionDialog(JFrame owner, AutoDialogListener adl, String title, boolean modal, Map data) {
        this(owner,adl,title,modal,data,new java.awt.GridLayout(data.keySet().size(),1));
    }
    public AutoOptionDialog(JFrame owner, String title, boolean modal, Map data) {
        this(owner,null,title,modal,data,new java.awt.GridLayout(data.keySet().size(),1));

        // can`t have this before the this()
        AutoDialogListener adl = null;
        if(owner instanceof AutoDialogListener) {
            myListener = (AutoDialogListener)owner;
        }
    }

    public Map preParse(Map data) {
        Map returnData = new HashMap();
        Iterator dataKeysI = data.keySet().iterator();
        while(dataKeysI.hasNext()) {
            Object key = dataKeysI.next();
            Object returnKey = null;
            Object value = data.get(key);
            Object returnValue = null;
            if(key instanceof String) {
                returnKey = new JLabel((String)key);
            } else
            if(key instanceof Icon) {
                returnKey = new JLabel((Icon)key);
            } else 
            if(key instanceof JLabel) {
                returnKey = key;
                // desired state
            } else {
                // COMPLAIN!!
            }
            
            if(value instanceof String) {
                returnData.put(returnKey,new StringDialogEntry( value));
            } else
            if(value instanceof Boolean) {
                returnData.put(returnKey,new BooleanDialogEntry( value));
            } else
            if(value instanceof Collection) {
                returnData.put(returnKey,new CollectionDialogEntry( value));
            } else
            if(value instanceof Object[]) {
                returnData.put(returnKey,new ObjectArrayDialogEntry( value));
            } else
            if(value instanceof DialogEntry) {
                returnData.put(returnKey,value);
               //leave alone
            } else {
               // COMPLAIN!
            }
        }
        return returnData;
    }

    protected void initDialog(Map data, LayoutManager layout) {
        this.getContentPane().setLayout( new BorderLayout());
        JPanel panel = new JPanel();
        JPanel buttonPanel = new JPanel();
        panel.setLayout(layout);
        Set keys = data.keySet();

        Iterator keysI = keys.iterator();
        while(keysI.hasNext()) {
            JLabel key = (JLabel)keysI.next();
            DialogEntry de = (DialogEntry)data.get(key);
            panel.add(key);
            Component com = de.createComponent();
            panel.add(com);
        }
        final AutoOptionDialog self = this; // FIX
        JButton okB = new JButton("OK");
        okB.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                self.hide();   // FIX
            }
        });
        buttonPanel.add( okB );
        JButton cancelB = new JButton("Cancel");
        buttonPanel.add( cancelB );
        if(myListener != null) {
            JButton applyB = new JButton("Apply");
            applyB.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    myListener.dialogApplied(new AutoDialogEvent(getDataMap()));
                }
            });
            buttonPanel.add( applyB );
        }
        this.getContentPane().add( panel, BorderLayout.CENTER );
        this.getContentPane().add( buttonPanel, BorderLayout.SOUTH );
    }

    public Object getData(Object key) {
        return myData.get(key);
    }

    public Map getDataMap() {
        // can i translate these???
        HashMap map = new HashMap();
        Set keys = myData.keySet();

        Iterator keysI = keys.iterator();
        while(keysI.hasNext()) {
            JLabel key = (JLabel)keysI.next();
            DialogEntry de = (DialogEntry)myData.get(key);
            map.put(key.getText(), de.getValue());
        }
        
        return map;
    }
}

// question as whether to have data in these or not.....
interface DialogEntry {
    public Component createComponent();
    public Component getComponent();
    public Object getValue();
}

class StringDialogEntry implements DialogEntry {

    private Object obj;
    private Component c;
    
    public StringDialogEntry(Object obj) {
        this.obj = obj;
    }

    public Component createComponent() {
        if(!(obj instanceof String)) {
            return null;  // throw wobbly?
        } else {
            c = new JTextField( (String)obj );
            return c;
        }
    }
    public Component getComponent() {
        return c;
    }
    public Object getValue() {
        if(!(c instanceof JTextField)) {
            return null;  // throw wobbly?
        } else {
            return ((JTextField)c).getText();
        }
    }
}

class BooleanDialogEntry implements DialogEntry {

    private Object obj;
    private Component c;
    
    public BooleanDialogEntry(Object obj) {
        this.obj = obj;
    }

    public Component createComponent() {
        if(!(obj instanceof Boolean)) {
            return null;  // throw wobbly?
        } else {
            c = new JCheckBox( "",((Boolean)obj).booleanValue() );
            return c;
        }
    }
    public Component getComponent() {
        return c;
    }
    public Object getValue() {
        if(!(c instanceof JCheckBox)) {
            return null;  // throw wobbly?
        } else {
            return new Boolean(((JCheckBox)c).isSelected());
        }
    }
}

class CollectionDialogEntry implements DialogEntry {
    private Collection col;
    private Component c;
    
    public CollectionDialogEntry(Object col) {
        this.col = (Collection)col;
    }


    public Component createComponent() {
        if(col == null) {
            return null;
        }

        c = new JList( col.toArray() );
        return c;
    }
    public Component getComponent() {
        return c;
    }
    public Object getValue() {
        if(!(c instanceof JList)) {
            return null;  // throw wobbly?
        } else {
          /*
          // far too far too ambitious
          try {
            Class[] params = new Class[1];
            params[0] = (new Object[0]).getClass();
            Constructor con = specificClass.getConstructor( params );
            Object[] args = new Object[1];
            args[0] = ((JComboBox)c).getSelectedObjects();
            return con.newInstance(args);
          } catch(Exception e) {
              System.out.println("Exception: "+e.getMessage());
              //e.printStackTrace();  
              return null;
          }
          */
          ArrayList list = new ArrayList();
          Object[] objarray = ((JList)c).getSelectedValues();
          for(int i=0;i<objarray.length;i++) {
              list.add(objarray[i]);
          }
          return list;
        }
    }
}

class ObjectArrayDialogEntry implements DialogEntry {

    private Object obj;
    private Component c;
    
    public ObjectArrayDialogEntry(Object obj) {
        this.obj = obj;
    }

    public Component createComponent() {
        if(!(obj instanceof Object[])) {
            return null;  // throw wobbly?
        } else {
            c = new JComboBox( (Object[])obj );
            return c;
        }
    }
    public Component getComponent() {
        return c;
    }
    public Object getValue() {
        if(!(c instanceof JComboBox)) {
            return null;  // throw wobbly?
        } else {
            return ((JComboBox)c).getSelectedObjects();
        }
    }
}

interface AutoDialogListener {

    public void dialogApplied(AutoDialogEvent ade);

}

class AutoDialogEvent {

    private Map data = null;

    public AutoDialogEvent(Map data) {
        this.data = data;
    }

    public Object getData(Object key) {
        return data.get(key);
    }

    public Map getDataMap() {
        return data;
    }
}

