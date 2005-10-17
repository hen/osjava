/*
 * Created on Oct 10, 2005
 */
package org.osjava.dswiz;

import javax.swing.JPanel;

/**
 * @author hyandell
 */
public abstract class DatabaseChoicePanel extends JPanel {

    protected DatabaseChosenListener listener;
    
    public void setDatabaseChosenListener(DatabaseChosenListener listener) {
        this.listener = listener;
    }
    
    public void notifyDatabaseChosen(String driver, String url) {
        this.listener.databaseChosen(driver, url);
    }
    
    public void notifyDatabaseChoiceCancelled() {
        this.listener.databaseChoiceCancelled();
    }    
    
}
