/*
 * Created on Oct 11, 2005
 */
package org.osjava.dswiz;

/**
 * @author hyandell
 */
public interface DatabaseChosenListener {

    void databaseChosen(String driver, String url);
    
    void databaseChoiceCancelled();
    
}
