/*
 * Created on Oct 11, 2005
 */
package org.osjava.dswiz;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * @author hyandell
 */
public class BasicDataSource implements DataSource {
    
    private String driver;
    private String url;
    private String defaultLogin;
    private String defaultPassword;
    
    public BasicDataSource(String driver, String url, String defaultLogin, String defaultPassword) {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException cnfe) {
            throw new RuntimeException("JDBC Driver not found: " + driver);
        }
        this.driver = driver;
        this.url = url;
        this.defaultLogin = defaultLogin;
        this.defaultPassword = defaultPassword;
    }

    public Connection getConnection() throws SQLException {
        return getConnection(this.defaultLogin, this.defaultPassword);
    }
    public Connection getConnection(String login, String password) throws SQLException {
        return DriverManager.getConnection(this.url, login, password);
    }
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }
    public void setLoginTimeout(int timeout) throws SQLException {
        DriverManager.setLoginTimeout(timeout);
    }
    public void setLogWriter(PrintWriter writer) throws SQLException {
        DriverManager.setLogWriter(writer);
    }
    
    public String toString() {
        return this.getClass() + "::::" + this.driver+ "::::" + this.url + "::::" + this.defaultLogin;
    }
}
