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

import java.sql.SQLException;
import java.sql.DriverManager;

// gives us pooling
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;

public class PoolSetup {

    public static void setupConnection(String pool, String url, String username, String password) throws SQLException {
        // we have a pool-name to setup using dbcp
        ObjectPool connectionPool = new GenericObjectPool(null);
        ConnectionFactory connectionFactory = null;
        if(username == null || password == null) {
            connectionFactory = new DriverManagerConnectionFactory(url, null);
        } else {
            connectionFactory = new DriverManagerConnectionFactory(url, username, password);
        }
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);
        try {
            Class.forName("org.apache.commons.dbcp.PoolingDriver");
        } catch(ClassNotFoundException cnfe) {
            // not too good
            System.err.println("WARNING: DBCP needed but no in the classpath. ");
        }
        PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
        driver.registerPool(pool, connectionPool);

    //  Runtime.getRuntime().addShutdownHook( new ShutdownDbcpThread(pool) );
    }

    public static String getUrl(String pool) {
        return "jdbc:apache:commons:dbcp:"+pool;
    }

}

/*
class ShutdownDbcpThread extends Thread {
    
    private String pool;

    public ShutdownDbcpThread(String pool) {
        this.pool = pool;
    }

    public void run() {
        try {
            PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
            driver.closePool(this.pool);
        } catch(SQLException sqle) {
            // failed to close
        } catch(ClassNotFoundException cnfe) {
            // oops, unable to close pools, sorry DBA
        }
    }
}
*/

