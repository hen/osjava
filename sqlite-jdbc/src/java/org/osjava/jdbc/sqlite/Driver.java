/*
 * org.osjava.jdbc.sqlite.Driver
 * $Id$
 * $Rev$ 
 * $Date$ 
 * $Author$
 * $URL$
 * 
 * Created on Jun 25, 2005
 *
 * Copyright (c) 2004, Robert M. Zigweid All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer. 
 *
 * + Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution. 
 *
 * + Neither the name of the SQLite-JDBC nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without 
 *   specific prior written permission.
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


package org.osjava.jdbc.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Driver implementation for the SQLite-JNDI package.
 *
 * @author Robert M. Zigweid.
 * @version $Rev$ $Date$
 */
public class Driver implements java.sql.Driver {
    
    /**
     * The last error message that the driver threw
     */
    private String lastErrorMessage;
    
    /* Load the proxy driver */
    static {
        System.loadLibrary("sqlite-jdbc");
    }
    
    /**
     * Creates a new SQLite-JDBC driver object.
     */
    public Driver() {
        try {
            DriverManager.registerDriver(this);
        } catch (SQLException e) {
            /* 
             * I'm not really sure why this is throwing an SQLException.  
             * Nothing should be happening at this point.
             */
            /* FIXME: Improve error handling.  This is not the place to 
             *        just report an error. 
             */
            e.printStackTrace();
        }
    }

    /** 
     * Connect to a database and return the resultant Connection object.
     * The <code>url</code> must contain a target that the underlying 
     * SQLite library can understand.  This is usually a filename.</br>
     * 
     * No properties are used to create the connection at this time.
     * 
     * @param url the url to connect to.
     * @param info additional property information used when connecting.  
     *        Currently, there is no information that cannot be gotten from
     *        the <code>url</code> that will be used, so any properties are
     *        ignored.
     * @return the new Connection object or null if the connection failed.
     * @throws SQLException if a connection error occurs.
     * @see java.sql.Driver#connect(java.lang.String, java.util.Properties)
     */
    public java.sql.Connection connect(String url, Properties info) 
            throws SQLException {
        /* Make sure that we can read the URL first. */
        if(!acceptsURL(url)) {
            return null;
        }
        
        /* Properties are ignored here, they really aren't relavent */
        String[] parts = url.split(":");
        StringBuffer fileName = new StringBuffer();
        for(int i = 2; i < parts.length; i++) {
            fileName.append(parts[i]);
        }
        Connection con = proxyConnect(fileName.toString());
        if(con == null) {
            throw new SQLException(lastErrorMessage);
        }
        return con;
    }

    /**
     * @see java.sql.Driver#acceptsURL(java.lang.String)
     */
    public boolean acceptsURL(String url) throws SQLException {
        if(   url.startsWith("jdbc:sqlite:")
           || url.startsWith("jdbc:sqlite3:")) {
            return true;
        }
        return false;
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) 
            throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public int getMajorVersion() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMinorVersion() {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean jdbcCompliant() {
        // TODO Auto-generated method stub
        return false;
    }

    /* JNI goodies.*/
    /**
     * Call to make the actual connection. If the connection is successful, the
     * newly created Connection object is returned. If an error occurs,
     * <code>null</code> is returned and the callback {@link #setErrorMessage}
     * will have been called with error message that the driver returned.
     * 
     * @param url String containing the filename that is the location of the
     *        database file.
     * @return true on success and false if the connection failed.
     */
    private native Connection proxyConnect(String url);

    
    private void setErrorMessage(String str) {
        lastErrorMessage = str;
    }
}
