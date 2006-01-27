/*
 * org.osjava.jdbc.sqlite.PreparedStatement
 * $Id$
 * $Rev$ 
 * $Date$ 
 * $Author$
 * $URL$
 * 
 * Created on Nov 23, 2005
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

import java.io.InputStream;
import java.io.Reader;

import java.math.BigDecimal;

import java.net.URL;

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * @author rzigweid
 *
 */
public class PreparedStatement extends Statement implements java.sql.PreparedStatement {

    /** 
     * The original sql string that was used to create the PreparedStatement
     */
    private String sql = null;
    
    /**
     * The parameters of the current incarnation of the statement.
     */
    private Object[] parameters = null;
    
    /**
     * Array of parameter arrays that have been batched;
     */
    private Object[] batched;
    
/* **************
 * CONSTRUCTORS *
 * **************/
    public PreparedStatement(Connection con,
                             String sql,
                             int resultSetType,
                             int resultSetConcurrency,
                             int resultSetHoldability)
            throws SQLException {
        super(con, resultSetType, resultSetConcurrency, resultSetHoldability);
        this.sql = sql;
        /* Determine the parameters of the statement.  */
        setupParameters();
    }

/* *********************
 * STATEMENT EXECUTION *
 * *********************/
    /**
     * Determine whether or not all of the parameters that are expected
     * in a statement have been filled.  This is done by checking all of the
     * elements of the array for null.  
     */
    protected boolean checkParametersFilled() {
        for(int i = 0; i < parameters.length; i++) {
            if(parameters[i] == null) {
                return false;
            }
        }
        return true;
    }
    
    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#addBatch()
     */
    public void addBatch() throws SQLException {
        if(!checkParametersFilled()) {
            throw new SQLException("Not all statement parameters have been filled.");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#execute()
     */
    public boolean execute() throws SQLException {
        if(!checkParametersFilled()) {
            throw new SQLException("Not all statement parameters have been filled.");
        }
        if(con.getAutoCommit()) {
            /* If the Connection is in autocommit mode, always commit before
             * a new statement is executed. */
            forceCommit();
        }
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#executeQuery()
     */
    public ResultSet executeQuery() throws SQLException {
        if(con.getAutoCommit()) {
            /* If the Connection is in autocommit mode, always commit before
             * a new statement is executed. */
            forceCommit();
        }
        if(!checkParametersFilled()) {
            throw new SQLException("Not all statement parameters have been filled.");
        }
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#executeUpdate()
     */
    public int executeUpdate() throws SQLException {
        if(!checkParametersFilled()) {
            throw new SQLException("Not all statement parameters have been filled.");
        }
        if(con.getAutoCommit()) {
            /* If the Connection is in autocommit mode, always commit before
             * a new statement is executed. */
            forceCommit();
        }
        // TODO Auto-generated method stub
        return 0;
    }


    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#getMetaData()
     */
    public ResultSetMetaData getMetaData() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#getParameterMetaData()
     */
    public ParameterMetaData getParameterMetaData() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    
/* *******************
 * PARAMETER SETTING *
 * *******************/
    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#clearParameters()
     */
    public void clearParameters() throws SQLException {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setArray(int, java.sql.Array)
     */
    public void setArray(int parameterIndex, Array x) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, int)
     */
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setBigDecimal(int, java.math.BigDecimal)
     */
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, int)
     */
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setBlob(int, java.sql.Blob)
     */
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setBoolean(int, boolean)
     */
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setByte(int, byte)
     */
    public void setByte(int parameterIndex, byte x) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setBytes(int, byte[])
     */
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, int)
     */
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setClob(int, java.sql.Clob)
     */
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setDate(int, java.sql.Date)
     */
    public void setDate(int parameterIndex, Date x) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setDate(int, java.sql.Date, java.util.Calendar)
     */
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setDouble(int, double)
     */
    public void setDouble(int parameterIndex, double x) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setFloat(int, float)
     */
    public void setFloat(int parameterIndex, float x) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setInt(int, int)
     */
    public void setInt(int parameterIndex, int x) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        parameters[parameterIndex - 1] = new Integer(x);
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setLong(int, long)
     */
    public void setLong(int parameterIndex, long x) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub        
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setNull(int, int)
     */
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setNull(int, int, java.lang.String)
     */
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setObject(int, java.lang.Object)
     */
    public void setObject(int parameterIndex, Object x) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int)
     */
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setRef(int, java.sql.Ref)
     */
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int, int)
     */
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setShort(int, short)
     */
    public void setShort(int parameterIndex, short x) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setString(int, java.lang.String)
     */
    public void setString(int parameterIndex, String x) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setTime(int, java.sql.Time)
     */
    public void setTime(int parameterIndex, Time x) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setTime(int, java.sql.Time, java.util.Calendar)
     */
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp)
     */
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp, java.util.Calendar)
     */
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setUnicodeStream(int, java.io.InputStream, int)
     */
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.PreparedStatement#setURL(int, java.net.URL)
     */
    public void setURL(int parameterIndex, URL x) throws SQLException {
        /* Ensure that the index is not out of bounds */
        if(parameterIndex > parameters.length || parameterIndex < 1) {
            throw new SQLException("Parameter out of bounds");
        }
        // TODO Auto-generated method stub
    }

/* ****************
 * HELPER METHODS *
 * ****************/
    protected void setupParameters() {
        /* Use a string tokenizer to find out how many elements parameterized.
         * This is only getting the count of columns to construct the array
         * that will contain the parameters. */
        StringTokenizer tokens = new StringTokenizer(sql, "?");
        int count = tokens.countTokens();
        if(count < 1) {
            parameters = new Object[0];
        } else {
            parameters = new Object[count - 1];
        }
    }

}
