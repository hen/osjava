package com.generationjava.swing;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;
import javax.swing.JPopupMenu;

public class PopupListener implements MouseListener {
    private JPopupMenu popup;

    public PopupListener(JPopupMenu popup) {
        this.popup = popup;
    }

    public void mousePressed(MouseEvent me) {
        if( (me.isPopupTrigger()) ||
            ( (me.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
          )
        {
            popup.show(me.getComponent(), me.getX(), me.getY());
        }
    }

    public void mouseReleased(MouseEvent me) {
    }

    public void mouseEntered(MouseEvent me) {
    }

    public void mouseExited(MouseEvent me) {
    }

    public void mouseClicked(MouseEvent me) {
    }

}
