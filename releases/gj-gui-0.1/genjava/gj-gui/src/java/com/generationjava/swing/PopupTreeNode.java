package com.generationjava.swing;

import java.awt.event.ActionEvent;

public interface PopupTreeNode {

    public PopupTreeNode getPopupParent();

    public void setPopupParent(PopupTreeNode parent);

    public boolean isPopupRoot();

    public String getName();

    public void notifyAction(ActionEvent event);

}
