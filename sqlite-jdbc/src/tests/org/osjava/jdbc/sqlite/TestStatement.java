/*
 * org.osjava.jdbc.sqlite.TestStatement
 * $Id$
 * $Rev$ 
 * $Date$ 
 * $Author$
 * $URL$
 * 
 * Created on Jul 3, 2005
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
import java.sql.SQLException;

import junit.framework.TestCase;

public class TestStatement extends TestCase {
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
     * Simple Statement.executeUpdate() test.  This particular test creates
     * a new database if it doesn't exist, and then creates a table 'foo' in
     * it.  The returned value should be 0;
     */
    public void testExecuteUpdate1() throws Exception {
        java.sql.Statement stmt = con.createStatement();
        int result = stmt.executeUpdate("CREATE TABLE foo (TestCol VARCHAR(10));");
        /* The ResultSet cannot be null, but it can be empty. */
        assertTrue(result == 0);
    }
    
    /**
     * A series of SQL statements, to create a table in a database, and
     * populate one row of the table.
     */
    public void testExecuteUpdate2() throws Exception {
        java.sql.Statement stmt = con.createStatement();
        stmt.executeUpdate("CREATE TABLE foo (TestCol VARCHAR(10));");
        int count = stmt.executeUpdate("INSERT INTO foo (TestCol) VALUES (\"WHEE\");");
        System.err.println("JAVA: Count returned -- " + count);
        assertEquals(count, 1);
    }

    public void testExecuteUpdate3() throws Exception {
        java.sql.Statement stmt = con.createStatement();
        /* Try to create the table.  This statement has a syntax error */
        try {
            stmt.executeUpdate(
                               "CREATE TABLE foo (" +
                               "    TinyInt1 TINYINT,  " +
                               ");"
            );
        } catch(SQLException e) {
            System.out.println("PING!");
            /* Only pass if this is a syntax error message.  This is subject 
             * to change if the underlying error is changed. */
            if(e.getMessage().endsWith(": syntax error")) {
                return;
            }
        }
        fail("Expected SQL syntax error and didn't get one.");
    }
        
    /**
     * Simple Statement.executeQuery() test.  This particular test creates
     * a new database if it doesn't exist, and then uses 
     * Statement.executeQuery() to create the first table.  The returned 
     * ResultSet is examined for basic properties. 
     * It should not be null.
     * It should not have any rows.  This will be determined by examining 
     * the last() method to ensure that it returns False.
     */
    public void testExecuteQuery1() throws Exception {
        java.sql.Statement stmt = con.createStatement();
        java.sql.ResultSet result = stmt.executeQuery("CREATE TABLE foo (TestCol VARCHAR(10));");
        /* The ResultSet cannot be null, but it can be empty. */
        assertNotNull(result);
        /* Move to the next row.  This should generate a false response 
         * because there are no rows returned. */
        assertFalse(result.next());
    }
    
    
    /**
     * Make a statement that executes a query and examine its resultSet.
     * 
     */
    public void testExecuteQuery2() throws Exception {
        java.sql.Statement stmt = con.createStatement();
        stmt.executeUpdate("CREATE TABLE foo (TestCol VARCHAR(10));");
        int count = stmt.executeUpdate("INSERT INTO foo (TestCol) VALUES (\"Test\");");
        /* Make sure that the row was actually added */
        assertEquals(count, 1);
        /* Commit before query */
        java.sql.ResultSet result = stmt.executeQuery("SELECT * FROM foo;");
        while(result.next()) {
            String foo = result.getString("TestCol");
            assertEquals("Test", foo);
        }
    }
}
