package com.generationjava.swing;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JMenu;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import com.generationjava.net.UrlW;
import com.generationjava.swing.PopupTreeMenuItem;

public class PopupTreeMenu extends JMenu implements PopupTreeNode {

    private boolean done = false;
    private PopupTreeNode parent;
    private String label;
    private String name;
    
    public PopupTreeMenu(final String label, final String url, final String name) {
        super(label);
        this.label = label;
        PopupTreeMenuItem item = new PopupTreeMenuItem(">>>");
        item.addMouseListener( new MouseAdapter() {
            public void mouseEntered(MouseEvent ae) {
                if(PopupTreeMenu.this.done) {
                    return;
                }
                PopupTreeMenu.this.done = true;
                PopupTreeMenu.this.removeAll();
                PopupTreeMenuItem loading = new PopupTreeMenuItem("Loading...");
                PopupTreeMenu.this.add(loading);
                PopupTreeMenu.this.getPopupMenu().pack();
                PopupTreeMenu.this.getPopupMenu().invalidate();
                PopupTreeMenu.this.getPopupMenu().repaint();

                // get values

                // create components
                PopupTreeMenu.this.removeAll();
                String urltmp = url + "?option="+label+"&name="+name;
                try {
                    String data = UrlW.getContent(urltmp).toString();
                    String[] values = StringUtils.split(data, "|");
                    int sz = values.length;
                    for(int i=0; i<sz; i++) {
                        PopupTreeMenuItem itemtmp = new PopupTreeMenuItem(values[i]);
                        itemtmp.setPopupParent(PopupTreeMenu.this);
                        itemtmp.setName(values[i]);
                        PopupTreeMenu.this.add(itemtmp);
                    }

                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }

                PopupTreeMenu.this.getPopupMenu().pack();
                PopupTreeMenu.this.getPopupMenu().invalidate();
                PopupTreeMenu.this.getPopupMenu().repaint();
            }
        });
        this.add(item);
    }

    public PopupTreeNode getPopupParent() {
        return this.parent;
    }

    public void notifyAction(ActionEvent event) {
        System.out.println("HING?");
        this.parent.notifyAction(event);
    }

    public void setPopupParent(PopupTreeNode parent) {
        this.parent = parent;
    }

    public boolean isPopupRoot() {
        return false;
    }

    public String getName() {
        return this.label;
    }

}
