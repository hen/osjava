// VerticalFlowLayout.java
package com.generationjava.awt;

import java.awt.LayoutManager;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Insets;

/**
 * A vertical flow layout. Useful if Box isn't available.
 * Based on an original in the Java Pitfalls book - 0471361747.
 */
public class VerticalFlowLayout implements LayoutManager {

    private int vertGap;

    public VerticalFlowLayout() {
        this(2);
    }

    public VerticalFlowLayout(int gap) {
        vertGap = gap;
    }

    public int getVerticalGap() {
        return vertGap;
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
            size.width = Math.max(tmp.width, size.width);
            size.height += tmp.height;
            
            // nicer way to do this?
            if(i != 0) {
                size.height += getVerticalGap();
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
        boolean   min       = required.height < available.height;
        Insets    insets    = parent.getInsets();
        final int x         = insets.left;
        int       y         = insets.top;
        final int w         = Math.max(available.width,required.width);
        final int xsHeight  = available.height - required.height;
        
        int count = parent.getComponentCount();
        for(int i=0;i<count;i++) {
            Component c = parent.getComponent(i);
            if(c.isVisible()) {
                int h = (min) ? c.getMinimumSize().height :
                                c.getPreferredSize().height;
                if(xsHeight > 0) {
                    h += (h * xsHeight / required.height);
                }
                
                c.setBounds(x,y,w,h);
                y += (h + getVerticalGap());
            }
        }
    }
}
