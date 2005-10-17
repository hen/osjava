/*
 * Created on Oct 10, 2005
 */
package org.osjava.dswiz;

import javax.sql.DataSource;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import org.osjava.dswiz.oracle.OraclePanel;

/**
 * @author hyandell
 */
public class DataSourceWizard extends JDialog implements DatabaseChosenListener {

    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException("Unable to set system look & feel");
        }
        
        JFrame frame = new JFrame("DataSourceWizard Example");
        DataSourceWizard dsw = new DataSourceWizard(frame);
        dsw.show();
        System.out.println(""+dsw.getDataSource());
        frame.dispose();
    }
    
    private String driver; 
    private String url;
    private String login;
    private String password;
    
    public DataSourceWizard(JFrame frame) {
        super(frame, true);
        
        JTabbedPane jtp = new JTabbedPane();
        getContentPane().add(jtp);
        DatabaseChoicePanel jdbc = new JdbcPanel();
        jdbc.setDatabaseChosenListener(this);
        jtp.add("JDBC", jdbc);

        DatabaseChoicePanel oracle = new OraclePanel();
        oracle.setDatabaseChosenListener(this);
        jtp.add("Oracle", oracle);
        
        pack();
        
        setResizable(false);
    }
  
    public void databaseChosen(String driver, String url) {
        this.driver = driver;
        this.url = url;
        getContentPane().removeAll();
        getContentPane().add(new LoginPanel(this));
        pack();
    }
    
    public void databaseChoiceCancelled() {
        this.dispose();
    }
        
    public void credentialsChosen(String login, String password) {
        this.login = login;
        this.password = password;
        this.dispose();
    }
    
    public void credentialsChoiceCancelled() {
        // TODO: Recreate the previous page?
        // ie) just hide it above and reshow it when Cancel is hit
        this.dispose();
    }
    
    public DataSource getDataSource() {
        if(this.driver == null) {
            return null;
        }
        return new BasicDataSource(this.driver, this.url, this.login, this.password);
    }
}