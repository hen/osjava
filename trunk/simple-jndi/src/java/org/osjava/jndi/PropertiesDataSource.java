/*
 * Copyright (c) 2003, Henri Yandell
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * 
 * + Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * + Neither the name of Simple-JNDI nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.osjava.jndi;

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
