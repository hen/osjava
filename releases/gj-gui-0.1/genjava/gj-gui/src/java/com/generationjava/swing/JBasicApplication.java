package com.generationjava.swing;


import com.generationjava.awt.ReportEvent;
import com.generationjava.awt.RequestEvent;
import com.generationjava.awt.MulticastRequestEvent;
import com.generationjava.awt.InformationListener;

import java.awt.Component;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import java.awt.FileDialog;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

import com.generationjava.lang.StringW;
import org.apache.commons.lang.StringUtils;

/**
 * A simple framework which allows files to be opened and 
 * many files be in an open state. Thus files may also be closed.
 */
abstract public class JBasicApplication extends JFrame 
       implements WindowListener, ActionListener 
{

    public final int LOAD = FileDialog.LOAD;
    
    private String currentDir;
    private Object currentObj;
    private Hashtable openList;
    private JMenu openMenu;
    private Hashtable menus;
    
    private Vector listeners;

    // used to signify whether the ctrl-,/ctrl-. is in effect.
    private boolean existsBefore;
    private boolean existsAfter;
    
    public JBasicApplication(String title) {
        super(title);
        openList = new Hashtable();
        menus = new Hashtable();
        JMenuBar bar = new JMenuBar();
        this.setJMenuBar(bar);

        registerMenuItem("File","Open","O");
        registerMenuItem("File","Close","Q");
        createNewBlock("File",0);
        registerMenuItem("File","Exit","E");
        openMenu = (JMenu)registerMenu("Opened");
        registerMenuItem("Help","About","Shift-A");
        registerMenuItem("Help","Licence","Shift-L");
        registerMenuItem("Help","QuickHelp","Shift-H");
    }

    // Menu Handling Code
    private void addTopLevelMenu(JMenu menu) {
        // push after help menu
        getJMenuBar().add(menu);
    }
    // will not return null
    private JMenu getMenu(String fqname) {
        JMenu m = (JMenu)menus.get(fqname);
        if(m == null) {
            m = createMenu(getLabel(getMenuName(fqname)));
            String parentName = StringUtils.substringBefore(fqname,".");
            if("".equals(parentName)) {
                addTopLevelMenu(m);
            } else {
                JMenu parent = getMenu(parentName);
                // parent can't be null
                parent.add(m);
            }
            menus.put(fqname,m);
        }
        return m;
    }
    private String getMenuName(String fqname) {
        return StringUtils.substringAfter(fqname,".");
    }
    // basically inserts a JSeparator
    protected void createNewBlock(String menuName, int block) {
        JMenu menu = getMenu(menuName);
        int idx = getBlockIndex(menu, block);
        menu.insertSeparator(idx);
    }
    // Creates a sub-menu. If menuName is "", then goes in root.
    protected Object registerMenu(String subMenuName, char mnemonic) {
        JMenu menu = getMenu(subMenuName);
        menu.setMnemonic( (int)mnemonic );
        return menu;
    }
    protected Object registerMenu(String subMenuName) {
        return getMenu(subMenuName);
    }
    protected Object registerMenu(String menuName, String subMenuName, String mnemonic) {
        return registerMenu(menuName, subMenuName, -2, null);
    }
    protected Object registerMenu(String menuName, String subMenuName, int block, String mnemonic) {
        JMenu parent = getMenu(menuName);
        int idx = getBlockIndex(parent,block);
        JMenu menu = createMenu( getLabel(subMenuName) );
        if(mnemonic != null) {
            menu.setMnemonic((int)mnemonic.charAt(0));
        }
        parent.insert(menu, idx);
        return menu;
    }
    private JMenu createMenu(String menuName) {
        JMenu menu = new JMenu( menuName);
        menu.setMnemonic( (int)menuName.charAt(0) );
        return menu;
    }
    protected Object registerMenuItem(String menuName, String itemName) {
        return registerMenuItem(menuName, itemName, -2, null);
    }
    protected Object registerMenuItem(String menuName, String itemName, String ch) {
        return registerMenuItem(menuName, itemName, -2, ch);
    }
    protected Object registerMenuItem(String menuName, String itemName, int block) {
        return registerMenuItem(menuName, itemName, -2, null);
    }
    protected Object registerMenuItem(String menuName, String itemName, int block, String ch) {
        String label = getLabel(itemName);
        JMenuItem mi = new JMenuItem( label );
        // set up accelerator?
        if(ch != null) {
            int mask = 0;
            if(ch.indexOf("Shift") != -1) {
                mask |= ActionEvent.SHIFT_MASK;
            }
            if(true) {
                mask |= ActionEvent.CTRL_MASK;
            }
            int key = (int)ch.charAt(ch.length()-1);
            mi.setAccelerator( KeyStroke.getKeyStroke(key, mask) );
        }
        mi.addActionListener(this);
        return registerMenuItem(menuName, mi, block);
    }
    protected Object registerMenuItem(String menuName, JMenuItem item, int block) {
        JMenu m = (JMenu)getMenu(menuName);
        int idx = getBlockIndex(m, block);
        m.insert(item,idx);
        return m;
    }

    // -2 implies the end, -1 means right at the front
    private int getBlockIndex(JMenu menu, int block) {
        if(block == -1) {
            return 0;
        }
        Component[] comps = menu.getMenuComponents();
        if(block == -2) {
            return comps.length;
        }
        int i;
        int sz = comps.length;
        for(i=0; i<sz; i++) {     // i is used outside the for loop
            if(comps[i] instanceof JSeparator) {
                block--;
                if(block == -1) {  
                    // reached end of specified block
                    break;
                }
            }
        }
        return i;
    }
    // End Of Menu Handling Code

    protected String getLabel( String key ) {
        return key;
    }
    
    protected void inform( String inf, String old) {
        this.setTitle(inf);
    }
    
    abstract protected void applicationClosed();

    abstract protected Object fileOpened(String filename);

    abstract protected String getAbout();
    abstract protected String getLicence();
    abstract protected String getQuickHelp();
    
    abstract protected void selection(Object obj);

    abstract protected void fileClosed(Object obj);
    
    abstract protected void menuInvoked(String action);

    protected void closeOpenList() {
        Enumeration enum = openList.keys();
        while(enum.hasMoreElements()) {
            Object obj = openList.get( enum.nextElement() );
            fileClosed(obj);
        }
    }
    
    protected void report(String name) {
        this.report(name, null);
    }
    protected void report(String name, Object value) {
        if(listeners == null) {
            return;
        }
        ReportEvent re = new ReportEvent(name, value);
        Enumeration enum = listeners.elements();
        while(enum.hasMoreElements()) {
            InformationListener il = (InformationListener)enum.nextElement();
            il.report(re);
        }
    }
    
    protected Object request(String name, boolean multi) {    
        return this.request(name, null, multi);
    }
    protected Object request(String name, Object value, boolean multi) {
        if(listeners == null) {
            return null;
        }
        RequestEvent re;
        if(multi) {
            re = new MulticastRequestEvent(name, value);
        } else {
            re = new RequestEvent(name, value);
        }
        Enumeration enum = listeners.elements();
        while(enum.hasMoreElements()) {
            InformationListener il = (InformationListener)enum.nextElement();
            Object obj = il.request(re);
            if(obj != null) {
                if(multi) {
                    re.setValue(obj);
                } else {
                    return obj;
                }
            }
        }
        return null;
    }
    
    protected void addInformationListener(InformationListener il) {
        if(listeners == null) {
            listeners = new Vector();
        }
        listeners.addElement(il);
    }
    protected void removeInformationListener(InformationListener il) {
        if(listeners != null) {
            listeners.removeElement(il);
        }
    }
    
    public boolean open(String filename) {
        Object obj = fileOpened(filename);
        if(obj != null) {
            setCurrent(obj);
            selection();
            addToOpenList(obj);
        }
        return (obj != null);
    }
    // ActionListener interface
    public void actionPerformed(ActionEvent ae) {
        String cmd = ae.getActionCommand();
        if("Exit".equals(cmd)) {
            applicationClosed();
            this.dispose();
        } else
        if("Open".equals(cmd)) {
            String filename = getFileName(LOAD);
            if(filename != null) {
                open(filename);
            }
        } else
        if("Licence".equals(cmd)) {
            String licence = getLicence();
            licence = StringW.wordWrap(licence, 40);
            JOptionPane.showMessageDialog(this,licence,"GenJava JBasicApplication",JOptionPane.INFORMATION_MESSAGE);
        } else
        if("QuickHelp".equals(cmd)) {
            String quickhelp = getQuickHelp();
            quickhelp = StringW.wordWrap(quickhelp, 40);
            JOptionPane.showMessageDialog(this,quickhelp,"GenJava JBasicApplication",JOptionPane.INFORMATION_MESSAGE);
        } else
        if("About".equals(cmd)) {
            String about = getAbout();
            about = StringW.wordWrap(about, 40);
            String old = this.getTitle();
            inform(about,old);
            JOptionPane.showMessageDialog(this,about,"GenJava JBasicApplication",JOptionPane.INFORMATION_MESSAGE);
        } else
        if("Close".equals(cmd)) {
            removeFromOpenList(currentObj);
            fileClosed(currentObj);
            setCurrent( popFromOpenList() );
            selection();
        } else
        if( openList.get(cmd) != null) {
            setCurrent( openList.get(cmd) );
            selection();
        } else {
            menuInvoked(cmd);
        }
    }
    
    public Object setCurrent(Object obj) {
        Object tmp = currentObj;
        currentObj = obj;
        return tmp;
    }
    
    public Object getCurrent() {
        return currentObj;
    }
    
    protected String getTitle(Object obj) {
        return obj.toString();
    }
    
    // Dynamic Open Menu
    /// TODO: Allow many dynamic menu's to be done?
    public void addToOpenList(Object obj) {
        openList.put( getTitle(obj), obj);
        registerMenuItem( "Opened", getTitle(obj) );
//                reset( obj );
    }
    
    public void removeFromOpenList(Object obj) {
        openList.remove( getTitle(obj) );

        openMenu.removeAll();

        Enumeration enum = openList.keys();
        while (enum.hasMoreElements()) {
                Object tmp = enum.nextElement();
                openMenu.add( tmp.toString() );
        }
    }
    
    private Object getFromOpenList(String name) {
        return openList.get(name);
    }
    
    protected void selection() {
        selection( getCurrent() );
    }
    
    public Object popFromOpenList() {
        Enumeration enum = openList.keys();
        if(enum.hasMoreElements()) {
            return openList.get( enum.nextElement() );
        } else {
            return null;
        }
    }
    
/* 
    private void reset(String filename) {
                int menusz = openMenu.getItemCount();
                existsBefore = false;
                existsAfter = false;
                for(int i=0;i<menusz;i++) {
                        openMenu.getItem(i).deleteShortcut();
                }
                for(int i=0;i<menusz;i++) {
                        if(filename.equals(openMenu.getItem(i).getLabel())) {
                                if(i != 0) {
                                        existsBefore = true;
                                        openMenu.getItem(i-1).setShortcut(new MenuShortcut(','));
                                }
                                if(i != menusz - 1) {
                                        existsAfter = true;
                                        openMenu.getItem(i+1).setShortcut(new MenuShortcut('.'));
                                }
                                break;
                        }
                }
        }*/
    
    protected String getFileName(int mode) {
        String prompt;

        // depending on load or save set the prompt                
        if (mode == LOAD) {
            prompt = "Open File ";
        } else {
            prompt = "Save File As ";
        }

        // create a file requester
        FileDialog d = new FileDialog(this, prompt, mode);

        if(currentDir != null) {
            d.setDirectory(currentDir);
        }
        d.show();
                
        currentDir = d.getDirectory();
                                
        return d.getDirectory() + d.getFile();
    }        

    // WindowListener interface
    public void windowDeiconified(WindowEvent event) {
    }

    public void windowIconified(WindowEvent event) {
    }

    public void windowActivated(WindowEvent event) {
    }

    public void windowDeactivated(WindowEvent event) {
    }

    public void windowOpened(WindowEvent event) {
    }

    public void windowClosed(WindowEvent event) {
    }

    public void windowClosing(WindowEvent event) {
                this.dispose();
    }

}
