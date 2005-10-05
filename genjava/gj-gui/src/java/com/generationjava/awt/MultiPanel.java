package com.generationjava.awt;

import java.awt.Panel;
import java.util.Hashtable;

public class MultiPanel extends Panel implements PanelFactory {

    private PanelFactory factory = null;
    private Hashtable panelMap = new Hashtable();

    public MultiPanel() {
        super();
    }
        
    public void setPanelFactory(PanelFactory factory) {
        this.factory = factory;
    }
    
    public void addPanel(String name) {
        addPanel(name, this);
    }
    
    public void addPanel(String name, Panel panel) {
        panelMap.put(name, panel);
    }

    public Panel getPanel(String name) {
        return (Panel)panelMap.get(name);
    }

    
    // IMPLEMENTING PanelFactory
    public Panel createPanel(String name) {
        if(factory != null) {
            return factory.createPanel(name);
        } else {
            return null;
        }
    }
    // End of PanelFactory
    
    public void switchPanel(String name) {
        Panel panel = getPanel(name);
        if(panel == null) {
            return;  // not an existing panel
        }
        if(panel == this) {
            panel = createPanel(name);
            addPanel(name, panel);
        }
        switchPanel(panel);
    }
    
    public void switchPanel(Panel panel) {
        this.removeAll();
        this.add(panel);
        this.invalidate();
        this.repaint();
    }

}