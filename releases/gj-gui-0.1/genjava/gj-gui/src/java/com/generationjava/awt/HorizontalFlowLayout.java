// HorizontalFlowLayout.java
package com.generationjava.awt;

import java.awt.LayoutManager;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Insets;

/**
 * Like FlowLayout, but works like VerticalFlowLayout.
 * Based on a VerticalFlowLayout in the Java Pitfalls book - 0471361747.
 */
public class HorizontalFlowLayout implements LayoutManager {

    private int horizGap;

    public HorizontalFlowLayout() {
        this(2);
    }

    public HorizontalFlowLayout(int gap) {
        horizGap = gap;
    }

    public int getHorizontalGap() {
        return horizGap;
    }

    // implement LayoutManager
    public void addLayoutComponent(String name, Component comp) {
    }
    public void removeLayoutComponent(Component comp) {
    }
    
    public Dimension preferredLayoutSize(Container parent) {
        return getLayoutSize(parent,false);
    }
    public Dimension minimumLayoutSize(Container parent) {
        return getLayoutSize(parent,true);
    }
    
    private Dimension getLayoutSize(Container parent, boolean min) {
        int count = parent.getComponentCount();
        Dimension size = new Dimension(0,0);
        for(int i=0;i<count;i++) {
            Component c = (Component)parent.getComponent(i);
            Dimension tmp = (min) ? c.getMinimumSize() :
                                    c.getPreferredSize();
            size.height = Math.max(tmp.height, size.height);
            size.width += tmp.width;
            
            // nicer way to do this?
            if(i != 0) {
                size.width += getHorizontalGap();
            }
        }
        Insets border = parent.getInsets();
        size.width += border.left + border.right;
        size.height += border.top + border.bottom;
        return size;
    }
    
    public void layoutContainer(Container parent) {
        Dimension available = parent.getSize();
        Dimension required  = preferredLayoutSize(parent);
        boolean   min       = required.width < available.width;
        Insets    insets    = parent.getInsets();
              int x         = insets.left;
        final int y         = insets.top;
        final int h         = Math.max(available.height,required.height);
        final int xsWidth   = available.width - required.width;
        
        int count = parent.getComponentCount();
        for(int i=0;i<count;i++) {
            Component c = parent.getComponent(i);
            if(c.isVisible()) {
                int w = (min) ? c.getMinimumSize().width :
                                c.getPreferredSize().width;
                if(xsWidth > 0) {
                    w += (w * xsWidth / required.width);
                }
                
                c.setBounds(x,y,w,h);
                x += (w + getHorizontalGap());
            }
        }
    }
}
