/*
 * org.osjava.jdbc.sqlite.Connection
 * $Id$
 * $Rev$ 
 * $Date$ 
 * $Author$
 * $URL$
 * 
 * Created on Jun 26, 2005
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

import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author rzigweid
 *
 */
public class Connection implements java.sql.Connection {
    
    /**
     * A pointer to the database connection object.
     */
    private int dbPointer;
    
    /**
     * A boolean value indicating whether or not the connection has been closed.
     */
    private boolean closed = false;

    /**
     * Statements that have been created.
     */
    Collection statements = new LinkedList();
    
    /**
     * Create a new Connection object.
     * 
     * @param ptr 
     * @see java.sql.Connection
     */
    public Connection(int ptr) {
        super();
        dbPointer = ptr;
    }
    
    /**
     * Create a Statement Object. The Statement will produce ResultSets with a
     * result set type of {@link java.sql.ResultSet#TYPE_FORWARD_ONLY}, a
     * result set concurrency of {@link java.sql.ResultSet#CONCUR_READ_ONLY} and
     * a result set holdability of
     * {@link java.sql.ResultSet#CLOSE_CURSORS_AT_COMMIT}. Note that the latter
     * of these defaults is not determined by the JDBC-3.0 specification (it's
     * oddly missing in the spec).
     * 
     * @see java.sql.Connection#createStatement()
     */
    public java.sql.Statement createStatement() throws SQLException {
        return createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, 
                               java.sql.ResultSet.CONCUR_READ_ONLY,
                               java.sql.ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }
    
    /**
     * Create a Statement Object. The Statement will produce ResultSets with a
     * result set type of <code>resultSetType</code>, a result set
     * concurrency of <code>resultSetConcurrency</code> and a result set
     * holdability of {@link java.sql.ResultSet#CLOSE_CURSORS_AT_COMMIT}. Note
     * that the latter of these defaults is not determined by the JDBC-3.0
     * specification (it's oddly missing in the spec).
     * 
     * @see java.sql.Connection#createStatement(int, int)
     */
    public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return createStatement(resultSetType, resultSetConcurrency, java.sql.ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }

    /**
     * Create a Statement Object. The Statement will produce ResultSets with a
     * result set type of <code>resultSetType</code>, a result set
     * concurrency of <code>resultSetConcurrency</code> and a result set
     * holdability of <code>resultSetHoldability</code>. Note that the latter
     * of these defaults is not determined by the JDBC-3.0 specification (it's
     * oddly missing in the spec).
     * 
     * @see java.sql.Connection#createStatement(int, int, int)
     */
    public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        if(   resultSetType != java.sql.ResultSet.TYPE_FORWARD_ONLY
           && resultSetType != java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE
           && resultSetType != java.sql.ResultSet.TYPE_SCROLL_SENSITIVE) {
            throw new SQLException("Cannot create Statement.  Invalid resultSetType.");
        }
        if(   resultSetConcurrency != java.sql.ResultSet.CONCUR_READ_ONLY
           && resultSetConcurrency != java.sql.ResultSet.CONCUR_UPDATABLE) {
            throw new SQLException("Cannot create Statement.  Invalid resultSetConcurrency.");
        }
        if(   resultSetHoldability != java.sql.ResultSet.CLOSE_CURSORS_AT_COMMIT
           && resultSetHoldability != java.sql.ResultSet.HOLD_CURSORS_OVER_COMMIT) {
            throw new SQLException("Cannot create Statement.  Invalid resultSetHoldability.");
        }
        Statement stmt = new Statement(this, resultSetType, resultSetConcurrency, resultSetHoldability);
        statements.add(stmt);
        return stmt;
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public CallableStatement prepareCall(String sql) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public String nativeSQL(String sql) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public boolean getAutoCommit() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }
    public void commit() throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void rollback() throws SQLException {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * Close the connection.
     *
     * @see java.sql.Connection#close()
     */
    public void close() throws SQLException {
        if(isClosed()) {
            throw new SQLException("Cannot close Connection. Connection is already closed.");
        }
        /* Ensure that all of the Connection's statements are closed. */
        Iterator it = statements.iterator();
        while(it.hasNext()) {
            Statement next = (Statement)it.next();
            next.close();
        }
        /* This can throw an exception based upon whether or not the 
         * connection is busy, or possibly an error if there is another 
         * circumstance. */ 
        proxyCloseConnection();
        closed = true;
    }
    
    /**
     * @see java.sql.Connection#isClosed()
     */
    public boolean isClosed() throws SQLException {
        return closed;
    }
    public DatabaseMetaData getMetaData() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public void setReadOnly(boolean readOnly) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public boolean isReadOnly() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }
    public void setCatalog(String catalog) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public String getCatalog() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public void setTransactionIsolation(int level) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public int getTransactionIsolation() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    public SQLWarning getWarnings() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public void clearWarnings() throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Map getTypeMap() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public void setTypeMap(Map map) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void setHoldability(int holdability) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public int getHoldability() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    public Savepoint setSavepoint() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Savepoint setSavepoint(String name) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public void rollback(Savepoint savepoint) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    /* Extra Methods */
    int getDBPointer() {
        return dbPointer;
    }
    
    /* Native methods */
    private native boolean proxyCloseConnection() throws SQLException;

}