package com.generationjava.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

public class PopupTreeMenuItem extends JMenuItem implements PopupTreeNode, ActionListener {

    private String name;
    private Object value;
    private PopupTreeNode parent;

    public PopupTreeMenuItem() {
        super();
        addActionListener(this);
    }

    public PopupTreeMenuItem(Action action) {
        super(action);
        addActionListener(this);
    }

    public PopupTreeMenuItem(Icon icon) {
        super(icon);
        addActionListener(this);
    }

    public PopupTreeMenuItem(String str) {
        super(str);
        addActionListener(this);
    }

    public PopupTreeMenuItem(String str,Icon icon) {
        super(str, icon);
        addActionListener(this);
    }

    public PopupTreeMenuItem(String str,int i) {
        super(str, i);
        addActionListener(this);
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public void actionPerformed(ActionEvent event) {
        notifyAction(event);
    }

    public void notifyAction(ActionEvent event) {
        this.parent.notifyAction(event);
    }

    public void setPopupParent(PopupTreeNode parent) {
        this.parent = parent;
    }

    public PopupTreeNode getPopupParent() {
        return this.parent;
    }

    public boolean isPopupRoot() {
        return false;
    }

}
