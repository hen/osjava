 /*
 * org.osjava.jdbc.sqlite.TestResultSetMetaData
 * $Id$
 * $Rev$ 
 * $Date$ 
 * $Author$
 * $URL$
 * 
 * Created on Sep 25, 2005
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

public class TestResultSetMetaData extends TestCase {
    private Connection con = null;

    /**
     * Setup the Connection and table
     */
    protected void setUp() throws Exception {
        super.setUp();
        try {
            Class.forName("org.osjava.jdbc.sqlite.Driver");
            con = DriverManager.getConnection("jdbc:sqlite:local.db");
        } catch (ClassNotFoundException e) {
            fail();
        }
        java.sql.Statement stmt = con.createStatement();
        /* Create the table */
        try {
            stmt.executeUpdate(
                               "CREATE TABLE foo (" +
                               "    TinyInt1 TINYINT, " +
                               "    SmallInt1 SMALLINT, " +
                               "    Integer1 INTEGER, " +
                               "    BigInt1 BIGINT, " +
                               "    Real1 REAL, " + 
                               "    Float1 FLOAT, " +
                               "    Double1 DOUBLE, " +
                               "    Decimal1 DECIMAL, " +
                               "    Numeric1 NUMERIC, " +
                               "    Bit1 BIT, " +
                               "    Boolean1 BOOLEAN, " +
                               "    Char1 CHAR, " +
                               "    VarChar1 VARCHAR(12), VarChar2 VARCHAR(100), " +
                               "    LongVarChar1 LONGVARCHAR(1024), " +
                               "    Binary1 BINARY, " +
                               "    VarBinary1 VARBINARY, " +
                               "    LongVarBinary1 LONGVARBINARY, " +
                               "    Date1 DATE, " +
                               "    Time1 TIME, " +
                               "    Timestamp1 TIMESTAMP, " +
                               "    Clob1 CLOB, " +
                               "    Blob1 BLOB, " +
                               "    Array1 INTEGER[], " +
//                             "    Ref1, REF, " +
                               "    DataLink1 DATALINK " +
//                             "    Struct1 STRUCT, " +
//                             "    Object1 JAVA_OBJECT, " +
//                             "    Nchar1 NCHAR, " +
//                             "    NVarChar1 NVARCHAR, " +
//                             "    LongNChar1 LONGNCHAR," +
//                             "    NClob1 NCLOB, " +
//                             "    SQLXML1 SQLXML, " +
                               ");"
            );
        } catch(SQLException e) {
            fail("Failed to create table : " + e.getMessage());
        }
        try {
            stmt.executeUpdate(
                               "INSERT INTO foo (" + 
                               "    TinyInt1, " +
                               "    SmallInt1, " +
                               "    Integer1, " +
                               "    BigInt1, " +
                               "    Real1, " +
                               "    Float1, " +
                               "    Double1, " +
                               "    Decimal1, " +
                               "    Numeric1, " +
                               "    Bit1, " +
                               "    Boolean1, " +
                               "    Char1, " +
                               "    VarChar1, " +
                               "    VarChar2, " +
                               "    LongVarChar1, " +
                               "    Binary1, " +
                               "    VarBinary1, " +
                               "    LongVarBinary1, " +
                               "    Date1, " +
                               "    Time1, " +
                               "    Timestamp1, " +
                               "    Clob1, " +
                               "    Blob1, " +
                               "    Array1, " +
//                             Reference types don't seem to be supported in SQLite            
//                             "    Ref1, " +
                               "    DataLink1 " +
//                             "    Struct1, " +
//                             "    Object1, " +
//                             "    Nchar1, " +
//                             "    NVarChar1, " +
//                             "    LongNChar1, " +
//                             "    NClob1, " +
//                             "    SQLXML1, " +
                               "    ) " +
                               "VALUES (" +
                               "    1, " +
                               "    2, " +
                               "    3, " +
                               "    4, " +
                               "    5.6, " +
                               "    7.8, " +
                               "    9.10, " +
                               "    11.12, " +
                               "    13, " +
                               "    'TRUE', " +
                               "    'FALSE', " +
                               "    'ABCD', " +
                               "    'EFGH', " +
                               "    'IJKL', " +
                               "    'MNOP', " +
                               "    'aaaa', " +
                               "    'bbbb', " +
                               "    'cccc', " +
                               "    '01-01-2001', " +
                               "    '12:00', " +
                               "    '01-01-2001 12:00:00', " +
//                             Clob here isn't done right.
                               "    'dddd', " +
//                             Blob here isn't done right.
                               "    'eeee', " +
                               "    '{10000, 10001, 10002, 10003}', " +
//                             Skipping Ref            
                               "    'http://www.osjava.org' " +
//                             Skipping Struct            
//                             Skipping JavaObject
//                             Skipping Nchar
//                             Skipping NvarChar
//                             Skipping LongNChar
//                             Skipping NClob
//                             Skipping SQLXML
                               ");"
            );
        } catch(SQLException e) {
            fail("Failed to populate table : " + e.getMessage());
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
    
    public void testGetColumnCount1() throws Exception {
        java.sql.Statement stmt = con.createStatement();
        java.sql.ResultSet result = null;
        java.sql.ResultSetMetaData meta = null;
        try {
            result = stmt.executeQuery("SELECT * FROM foo;");
            meta = result.getMetaData();
         } catch(SQLException e) {
             fail(e.getMessage());
         }
         stmt.close();
        assertEquals(25, meta.getColumnCount());
    }

    public void testGetColumnCount2() throws Exception {
        java.sql.Statement stmt = con.createStatement();
        java.sql.ResultSetMetaData meta = null;
        java.sql.ResultSet res = stmt.executeQuery("SELECT Real1, VarChar2, Integer1, Bit1, Char1 FROM foo;");
        meta = res.getMetaData();
        assertEquals(5, meta.getColumnCount());
    }

    public void testGetColumnName1() throws Exception {
        java.sql.Statement stmt = con.createStatement();
        java.sql.ResultSetMetaData meta = null;
        java.sql.ResultSet res = stmt.executeQuery("SELECT Real1, VarChar2, Integer1, Bit1, Char1 FROM foo;");
        meta = res.getMetaData();
        assertEquals("Real1", meta.getColumnName(1));
    }

    public void testGetColumnName2() throws Exception {
        java.sql.Statement stmt = con.createStatement();
        java.sql.ResultSetMetaData meta = null;
        java.sql.ResultSet res = stmt.executeQuery("SELECT Real1 as A, VarChar2 as B, Integer1 AS C, Bit1, Char1 FROM foo;");
        meta = res.getMetaData();
        assertEquals("A", meta.getColumnName(1));
    }
    
    public void testGetColumnType1() throws Exception {
        java.sql.Statement stmt = con.createStatement();
        java.sql.ResultSetMetaData meta = null;
        java.sql.ResultSet res = stmt.executeQuery("SELECT * FROM foo;");
        meta = res.getMetaData();
        assertEquals(java.sql.Types.TINYINT, meta.getColumnType(1));
    }

    public void testGetColumnTypeName1() throws Exception {
        java.sql.Statement stmt = con.createStatement();
        java.sql.ResultSetMetaData meta = null;
        java.sql.ResultSet res = stmt.executeQuery("SELECT * FROM foo;");
        meta = res.getMetaData();
        assertEquals("TINYINT", meta.getColumnTypeName(1));
    }
}
