package com.generationjava.jndi;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.sql.DriverManager;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Properties;

public class PropertiesDataSource implements DataSource {

    private Hashtable env;
    private Properties props;
    private PrintWriter pw;
    private String name;

    public PropertiesDataSource(Properties props, Hashtable env) {
        this.props = props;
        this.env = env;
        this.pw = new PrintWriter(System.err);
    }

    void setName(String name) {
        this.name = name;
//        System.err.println("Loading: "+name+"."+get("driver")+" from "+props);
//        DbUtils.ensureLoaded(get("driver"));
        ensureLoaded(get("driver"));
    }

    // nicked from DbUtils
    private static boolean ensureLoaded(String name) {
        try {
            Class.forName(name).newInstance();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String get(String val) {
        if(name != null && !name.equals("")) {
            val = name + "." + val;
        }
//        System.err.println("Getting: "+val);
        return this.props.getProperty(val);
    }

    public Connection getConnection() throws SQLException {
        String user = get("user");
        String password = get("password");
        return this.getConnection(user, password);
    }

    public Connection getConnection(String username, String password) throws SQLException {
        String url = get("url");
        if(username == null || password == null) {
            return DriverManager.getConnection(url);
        } else {
            return DriverManager.getConnection(url, username, password);
        }
    }

    public PrintWriter getLogWriter() throws SQLException {
        return pw;
    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public void setLogWriter(PrintWriter pw) throws SQLException {
        this.pw = pw;
    }

    public void setLoginTimeout(int timeout) throws SQLException {
        // ignored
    }

}
