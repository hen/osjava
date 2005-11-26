/*
 * org.osjava.jdbc.sqlite.TestPreparedStatement
 * $Id$
 * $Rev$ 
 * $Date$ 
 * $Author$
 * $URL$
 * 
 * Created on Nov 25, 2005
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

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import junit.framework.TestCase;

public class TestPreparedStatement extends TestCase {
    private Connection con = null;

    protected void setUp() throws Exception {
        super.setUp();
        try {
            Class.forName("org.osjava.jdbc.sqlite.Driver");
            con = DriverManager.getConnection("jdbc:sqlite:local.db");
        } catch (ClassNotFoundException e) {
            fail();
        }
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        con.close();
        /* If the local.db file exists, remove it. */
        File db = new File("local.db");
        if(db.exists()) {
            db.delete();
        }
    }

    /** 
     * Construct a simple prepared statement with an empty string as the
     * statement.
     */
    public void testPreparedStatement1() throws Exception {
        con.prepareStatement("");
    }
    
    /** 
     * Construct a slightly more complicated prepared statement.  One that has
     * a couple of tokens.
     */
    public void testPreparedStatement2() throws Exception {
        con.prepareStatement("INSERT INTO foo ('a', 'b') VALUES ('?', '?');");
    }
        
    /**
     * Create a simple prepared statement and try to insert a parameter that
     * is out of range.  If the SQLException for this is caught the test 
     * passes. 
     */ 
    public void testPreparedStatementSetParam1() throws Exception {
        java.sql.PreparedStatement stmt = con.prepareStatement("");
        try {
            stmt.setInt(1, 12345);
        } catch(SQLException e) {
            return;
        }

    }

    /**
     * Construct a statement and populate it's fields
     */
    public void testPreparedStatementSetParam2() throws Exception {
        java.sql.PreparedStatement stmt = con.prepareStatement("INSERT INTO foo ('a', 'b') VALUES ('?', '?');");
        stmt.setInt(1, 12345);
        stmt.setInt(2, 67890);
    }
    
    /**
     * Construct a statement and overpopulate it's fields.  The third set 
     * should throw a SQLException
     */
    public void testPreparedStatementSetParam3() throws Exception {
        java.sql.PreparedStatement stmt = con.prepareStatement("INSERT INTO foo ('a', 'b') VALUES ('?', '?');");
        stmt.setInt(1, 12345);
        stmt.setInt(2, 67890);
        try {
            stmt.setInt(3, 1234567890);
        } catch (SQLException e) {
            return;
        }
    }

    /**
     * Construct a statement and try to populate parameter 0.
     */
    public void testPreparedStatementSetParam4() throws Exception {
        java.sql.PreparedStatement stmt = con.prepareStatement("INSERT INTO foo ('a', 'b') VALUES ('?', '?');");
        try {
            stmt.setInt(0, 1234567890);
        } catch (SQLException e) {
            return;
        }
    }

    /**
     * Construct statements to create a small table and then populate a row 
     * in the table partially.
     */

}
