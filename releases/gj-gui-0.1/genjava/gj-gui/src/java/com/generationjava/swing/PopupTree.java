package com.generationjava.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PopupTree extends JPopupMenu implements PopupTreeNode {

    private String url;
    private String name;
    private ActionListener listener;
    private Object target;

    public PopupTree() {
        super();
    }

    public PopupTree(String label) {
        super(label);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getTarget() {
        return this.target;
    }

    public void setTarget(Object obj) {
        this.target = obj;
    }

    public void addMenu(String label) {
        PopupTreeMenu popupMenu = new PopupTreeMenu(label, this.url, name);
        popupMenu.setPopupParent(this);
        this.add(popupMenu);
    }

    public void addActionListener(ActionListener listener) {
        this.listener = listener;
    }

    public void notifyAction(ActionEvent event) {
        System.out.println("NOTIFY");
        this.listener.actionPerformed(event);
    }

    public PopupTreeNode getPopupParent() {
        return null;
    }

    public void setPopupParent(PopupTreeNode parent) {
    }

    public boolean isPopupRoot() {
        return true;
    }

    public String getName() {
        return this.url;
    }

}
