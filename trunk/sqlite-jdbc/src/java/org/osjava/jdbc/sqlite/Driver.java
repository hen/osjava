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
        try {
            DriverManager.registerDriver(new Driver());
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
     * Useful properties to pass.
     * <ul>
     * <li><b>create</b>: Whether or not to create a new database.  Valid
     *     values are "Always", "Never", or "Open".  These values are case 
     *     incensitive.  "Always" will always create a new database, removing
     *     the old one if it existed.  "Never" will never create a new, only 
     *     using an already existing one.  If there is not a file there, an 
     *     {@link SQLException} will be thrown. If "Open" is passed, a new 
     *     file will be created if it does not exist, otherwise the existing
     *     file will be used.  Any other value will result in a SQLException
     *     being thrown with a message indicating an invalid Property value.
     *     The default behavior is "Open".</li>
     * </ul>
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

    /**
     * The major version of the driver.
     * @see java.sql.Driver#getMajorVersion()
     */
    public int getMajorVersion() {
        return 0;
    }

    /**
     * The minor version of the driver.
     * @see java.sql.Driver#getMinorVersion()
     */
    public int getMinorVersion() {
        return 1;
    }

    /* Looking forward to turning this into a true ;) */
    public boolean jdbcCompliant() {
        // TODO Auto-generated method stub
        return false;
    }

    /* JNI goodies.*/
    /**
     * Call to make the actual connection. If the connection is successful, the
     * newly created Connection object is returned.  If an error occurs, a 
     * SQLException is thrown.
     * 
     * @param url String containing the filename that is the location of the
     *        database file.
     * @return true on success and false if the connection failed.
     */
    private native Connection proxyConnect(String url) throws SQLException;
}
