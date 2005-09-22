/*
 * org.osjava.jdbc.sqlite.ResultSet
 * $Id$
 * $Rev$
 * $Date$
 * $Author$
 * $URL$
 *
 * Created on Jul 2, 2005
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
import java.sql.Date;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Time;
import java.sql.Timestamp;

import java.util.Calendar;
import java.util.Map;

/**
 * @author rzigweid
 *
 */
public class ResultSet implements java.sql.ResultSet {
    /**
     * The statement used to create this ResultSet
     */
    Statement stmt;

    /**
     * The type of ResultSet.  Valid values are:
     *      java.sql.ResultSet.TYPE_FORWARD_ONLY
     *      java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE
     *      java.sql.ResultSet.TYPE_SCROLL_SENSITIVE
     */
    private int resultSetType;

    /**
     * The concurrency of the ResultSet.  Valid values are:
     *      java.sql.ResultSet.CONCUR_READ_ONLY
     *      java.sql.ResultSet.CONCUR_UPDATABLE
     */
    private int resultSetConcurrency;

    /**
     * Value indicating whether or not to close the ResultSet when a commit
     * is made.  Valid values are:
     *      java.sql.ResultSet.CLOSE_CURSORS_AT_COMMIT
     *      java.sql.ResultSet.HOLD_CURSORS_OVER_COMMIT
     */
    private int resultSetHoldability;

    /**
     * The number of results that will be fetched by the driver at a time.
     */
    private int fetchSize = 0;

    /**
     * The number of the current row.
     * Special values are: </br>
     * <ul><li>-1: Position 'beforeFirst()'</li>
     *     <li>-2: Position 'afterLast()'</li>
     */
    private int currentRow = -1;

    /**
     * Maximum row known
     */
    private int maxRow = -1;

    /**
     * Array of rows.
     */
    private Object rows[] = null;

    /**
     * The minimum and maximum rows of the current page.
     */
    private int pageMin;
    private int pageMax;

    /**
     * MetaData for this ResultSet.
     */
    private ResultSetMetaData metaData;

    private int statementPointer;

    private boolean closed;

/****************
 * Constructors *
 ****************/
    /**
     * Create a new ResultSet with from the Statement <code>st</code>.
     *
     * @param st the Statement that produced this object.
     * @param resultSetType the result set holdability of the ResultSet.
     * @param resultSetConcurrency the result set concurrency of the ResultSet.
     * @param resultSetHoldability the result set type of the ResultSet.
     * @throws SQLException if any of the parameter values are out of range.
     */
    ResultSet(Statement st, int resultSetType, int resultSetConcurrency, int resultSetHoldability, Statement parent)
            throws SQLException {
        super();
        if (   resultSetType != java.sql.ResultSet.TYPE_FORWARD_ONLY
            && resultSetType != java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE
            && resultSetType != java.sql.ResultSet.TYPE_SCROLL_SENSITIVE) {
            throw new SQLException("Cannot create Statement.  Invalid resultSetType.");
        }
        if (   resultSetConcurrency != java.sql.ResultSet.CONCUR_READ_ONLY
            && resultSetConcurrency != java.sql.ResultSet.CONCUR_UPDATABLE) {
            throw new SQLException("Cannot create Statement.  Invalid resultSetConcurrency.");
        }
        if (   resultSetHoldability != java.sql.ResultSet.CLOSE_CURSORS_AT_COMMIT
            && resultSetHoldability != java.sql.ResultSet.HOLD_CURSORS_OVER_COMMIT) {
            throw new SQLException("Cannot create Statement.  Invalid resultSetHoldability.");
        }
        stmt = st;
        this.resultSetType = resultSetType;
        this.resultSetConcurrency = resultSetConcurrency;
        this.resultSetHoldability = resultSetHoldability;

        /* Set the fetch size based upon the Statement's fetchSize */
        fetchSize = parent.getFetchSize();

        /* Create the rows ArrayList, based upon the fetch size */
        rows = new Object[fetchSize];
        pageMin = 0;
        pageMax = fetchSize;

        /*
         * Create the ResultSetMetadata object for this ResultSet.  It doesn't
         * get populated right away, but it needs to exist.
         */
        metaData = new ResultSetMetaData(this);
    }

/************************
 * ResultSet navigation *
 ************************/
    /* (non-Javadoc)
     * @see java.sql.ResultSet#next()
     */
    public boolean next() throws SQLException {
        if(currentRow == pageMax) {
            scrollResultSet(pageMax + 1);
        }
        currentRow++;
        /* If the next row is null, that means that the end of the ResultSet
         * is reached.  Set things properly */
        if(rows[currentRow - pageMin] == null) {
            currentRow = -2;
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#afterLast()
     */
    public void afterLast() throws SQLException {
        currentRow = -2;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#beforeFirst()
     */
    public void beforeFirst() throws SQLException {
        currentRow = -1;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#first()
     */
    public boolean first() throws SQLException {
        int originalRow = currentRow;
        if(resultSetType == ResultSet.TYPE_FORWARD_ONLY) {
            throw new SQLException("Type is java.sql.ResultSet.TYPE_FORWARD_ONLY.  Invalid operation");
        }
        /* Make sure tha tthe row 0 is valid */
        currentRow = 0;

        return false;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#last()
     */
    public boolean last() throws SQLException {
        if(resultSetType == ResultSet.TYPE_FORWARD_ONLY) {
            throw new SQLException("Type is java.sql.ResultSet.TYPE_FORWARD_ONLY.  Invalid operation");
        }
        /* Scroll from current all the way to the end.  */
        int pageStart = pageMin;
        if(currentRow < 0) {
            scrollResultSet(0);
        }
        /* Look at the last element of the page. If it's filled, try to get
         * the next page.  Do this until we're on the last valid page. */
        while(rows[rows.length] != null) {
            scrollResultSet(pageMax + 1);
        }

        /* Scroll backwards until we determine the first actual filled row.
         * This is where we will set up
         */
        for(int i = fetchSize; i >= 0; i--) {
            if(rows[i] != null) {
                currentRow = pageMin + i;
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#absolute(int)
     */
    public boolean absolute(int row) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#relative(int)
     */
    public boolean relative(int rows) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#previous()
     */
    public boolean previous() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#findColumn(java.lang.String)
     */
    public int findColumn(String columnName) throws SQLException {
        return metaData.getColumnForName(columnName);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#isAfterLast()
     */
    public boolean isAfterLast() throws SQLException {
        return currentRow == -2;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#isBeforeFirst()
     */
    public boolean isBeforeFirst() throws SQLException {
        return currentRow == -1;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#isFirst()
     */
    public boolean isFirst() throws SQLException {
        return currentRow == 0;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#isLast()
     */
    public boolean isLast() throws SQLException {
        if(maxRow != -1 && resultSetType != java.sql.ResultSet.TYPE_SCROLL_SENSITIVE) {
            return currentRow == maxRow;
        }
        /* If the next element in the page is not null, return false.  There
         * is a potential issue here if the current row is the last row
         * possible on the page.   I'm not sure if I want to make it pull
         * the next page down. */
        if(currentRow != pageMax) {
            return rows[(currentRow - pageMin) + 1] == null;
        }
        return false;
    }

    /**
     * @see java.sql.ResultSet#close()
     */
    public void close() throws SQLException {
        System.err.println("Trying to close ResultSet --" + this);
        if(closed) {
            return;
        }
        proxyCloseStatement();
        stmt.removeResult(this);
        closed = true;
    }

/*********************************
 * General ResultSet information *
 *********************************/
    /**
     * @see java.sql.ResultSet#getConcurrency()
     */
    public int getConcurrency() throws SQLException {
        return resultSetConcurrency;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getCursorName()
     */
    public String getCursorName() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#setFetchDirection(int)
     */
    public void setFetchDirection(int direction) throws SQLException {
    // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getFetchDirection()
     */
    public int getFetchDirection() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * Sets the ResultSet's fetch size.
     *
     * @see java.sql.ResultSet#setFetchSize(int)
     */
    public void setFetchSize(int rows) throws SQLException {
        if(rows > stmt.getMaxRows()) {
            throw new SQLException("Cannot set fetch size. Statement's maximum is " + stmt.getMaxRows() + ".");
        }
        if(rows < 0) {
            throw new SQLException("Cannot set fetch size.  Minimum value is 0.");
        }
        fetchSize = rows;
    }

    /**
     * Return the number of rows that will be fetched by the JDBC driver at a
     * time.  If the value has not been set,
     * {@link java.sql.Statement#getFetchSize()} will be used.
     * @see java.sql.ResultSet#getFetchSize()
     */
    public int getFetchSize() throws SQLException {
        if(fetchSize > 0) {
            return fetchSize;
        }
        return stmt.getFetchSize();
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getMetaData()
     */
    public java.sql.ResultSetMetaData getMetaData() throws SQLException {
        return metaData;
    }

    /**
     * @see java.sql.ResultSet#getType()
     */
    public int getType() throws SQLException {
        return resultSetType;
    }


    /* (non-Javadoc)
     * @see java.sql.ResultSet#wasNull()
     */
    public boolean wasNull() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

/**********************************
 * ResultSet Row Querying Methods *
 **********************************/
    /* (non-Javadoc)
     * @see java.sql.ResultSet#getArray(int)
     */
    public Array getArray(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getArray(java.lang.String)
     */
    public Array getArray(String columnName) throws SQLException {
        return getArray(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getAsciiStream(int)
     */
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getAsciiStream(java.lang.String)
     */
    public InputStream getAsciiStream(String columnName) throws SQLException {
        return getAsciiStream(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getBigDecimal(int)
     */
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getBigDecimal(java.lang.String)
     */
    public BigDecimal getBigDecimal(String columnName) throws SQLException {
        return getBigDecimal(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getBigDecimal(int, int)
     */
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getBigDecimal(java.lang.String, int)
     */
    public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
        return getBigDecimal(findColumn(columnName), scale);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getBinaryStream(int)
     */
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getBinaryStream(java.lang.String)
     */
    public InputStream getBinaryStream(String columnName) throws SQLException {
        return getBinaryStream(findColumn(columnName));
    }


    /* (non-Javadoc)
     * @see java.sql.ResultSet#getBlob(int)
     */
    public Blob getBlob(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see java.sql.ResultSet#getBlob(java.lang.String)
     */

    public Blob getBlob(String columnName) throws SQLException {
        return getBlob(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getBoolean(int)
     */
    public boolean getBoolean(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getBoolean(java.lang.String)
     */
    public boolean getBoolean(String columnName) throws SQLException {
        return getBoolean(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getByte(int)
     */
    public byte getByte(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getByte(java.lang.String)
     */
    public byte getByte(String columnName) throws SQLException {
        return getByte(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getBytes(int)
     */
    public byte[] getBytes(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getBytes(java.lang.String)
     */
    public byte[] getBytes(String columnName) throws SQLException {
        return getBytes(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getCharacterStream(int)
     */
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getCharacterStream(java.lang.String)
     */
    public Reader getCharacterStream(String columnName) throws SQLException {
        return getCharacterStream(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getClob(int)
     */
    public Clob getClob(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getClob(java.lang.String)
     */
    public Clob getClob(String columnName) throws SQLException {
        return getClob(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getDate(int)
     */
    public Date getDate(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getDate(java.lang.String)
     */
    public Date getDate(String columnName) throws SQLException {
        return getDate(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getDate(int, java.util.Calendar)
     */
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getDate(java.lang.String, java.util.Calendar)
     */
    public Date getDate(String columnName, Calendar cal) throws SQLException {
        return getDate(findColumn(columnName), cal);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getDouble(int)
     */
    public double getDouble(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getDouble(java.lang.String)
     */
    public double getDouble(String columnName) throws SQLException {
        return getDouble(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getFloat(int)
     */
    public float getFloat(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getFloat(java.lang.String)
     */
    public float getFloat(String columnName) throws SQLException {
        return getFloat(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getInt(int)
     */
    public int getInt(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        Object raw = ((Object[])rows[currentRow])[columnIndex - 1];
        if(raw == null) {
            return 0;
        }
        if(raw instanceof Number) {
            return ((Number)raw).intValue();
        }
        if(raw instanceof String) {
            try {
                return Integer.parseInt((String)raw);
            } catch (NumberFormatException e) {
                /* FIXME: I don't like this.  SQLException doesn't have a
                 *        constructor that takes a Throwable argument like
                 *        most exceptions
                 */
                throw new SQLiteException(e);
            }
        }
        throw new SQLException("Unable to determine source data type.");
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getInt(java.lang.String)
     */
    public int getInt(String columnName) throws SQLException {
        return getInt(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getLong(int)
     */
    public long getLong(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getLong(java.lang.String)
     */
    public long getLong(String columnName) throws SQLException {
        return getLong(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getObject(int)
     */
    public Object getObject(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getObject(java.lang.String)
     */
    public Object getObject(String columnName) throws SQLException {
        return getObject(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getObject(int, java.util.Map)
     */
    public Object getObject(int columnIndex, Map map) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getObject(java.lang.String, java.util.Map)
     */
    public Object getObject(String columnName, Map map) throws SQLException {
        return getObject(findColumn(columnName), map);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getRef(int)
     */
    public Ref getRef(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getRef(java.lang.String)
     */
    public Ref getRef(String columnName) throws SQLException {
        return getRef(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getShort(int)
     */
    public short getShort(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getShort(java.lang.String)
     */
    public short getShort(String columnName) throws SQLException {
        return getShort(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getString(int)
     */
    public String getString(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        return ((Object[])rows[currentRow])[columnIndex - 1].toString();
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getString(java.lang.String)
     */
    public String getString(String columnName) throws SQLException {
        return getString(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getTime(int)
     */
    public Time getTime(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getTime(java.lang.String)
     */
    public Time getTime(String columnName) throws SQLException {
        return getTime(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getTime(int, java.util.Calendar)
     */
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getTime(java.lang.String, java.util.Calendar)
     */
    public Time getTime(String columnName, Calendar cal) throws SQLException {
        return getTime(findColumn(columnName), cal);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getTimestamp(int)
     */
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getTimestamp(java.lang.String)
     */
    public Timestamp getTimestamp(String columnName) throws SQLException {
        return getTimestamp(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getTimestamp(int, java.util.Calendar)
     */
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getTimestamp(java.lang.String, java.util.Calendar)
     */
    public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
        return getTimestamp(findColumn(columnName), cal);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getUnicodeStream(int)
     */
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getUnicodeStream(java.lang.String)
     */
    public InputStream getUnicodeStream(String columnName) throws SQLException {
        return getUnicodeStream(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getURL(int)
     */
    public URL getURL(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getURL(java.lang.String)
     */
    public URL getURL(String columnName) throws SQLException {
        return getURL(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#getWarnings()
     */
    public SQLWarning getWarnings() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#clearWarnings()
     */
    public void clearWarnings() throws SQLException {
    // TODO Auto-generated method stub

    }

    /**
     * @see java.sql.ResultSet#getRow()
     */
    public int getRow() throws SQLException {
        if(currentRow < 0) {
            return 0;
        }
        /* XXX: getRow() is 1 based */
        return currentRow + 1;
    }


    /* (non-Javadoc)
     * @see java.sql.ResultSet#rowUpdated()
     */
    public boolean rowUpdated() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#rowInserted()
     */
    public boolean rowInserted() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#rowDeleted()
     */
    public boolean rowDeleted() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

/**********************************
 * ResultSet Row Updating Methods *
 **********************************/
    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateArray(int, java.sql.Array)
     */
    public void updateArray(int columnIndex, Array x) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateArray(java.lang.String, java.sql.Array)
     */
    public void updateArray(String columnName, Array x) throws SQLException {
        updateArray(findColumn(columnName), x);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, int)
     */
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, int)
     */
    public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
        updateAsciiStream(findColumn(columnName), x, length);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, int)
     */
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, int)
     */
    public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
        updateBinaryStream(findColumn(columnName), x, length);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateBigDecimal(int, java.math.BigDecimal)
     */
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateBigDecimal(java.lang.String, java.math.BigDecimal)
     */
    public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
        updateBigDecimal(findColumn(columnName), x);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateBlob(int, java.sql.Blob)
     */
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateBlob(java.lang.String, java.sql.Blob)
     */
    public void updateBlob(String columnName, Blob x) throws SQLException {
        updateBlob(findColumn(columnName), x);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateBoolean(int, boolean)
     */
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateBoolean(java.lang.String, boolean)
     */
    public void updateBoolean(String columnName, boolean x) throws SQLException {
        updateBoolean(findColumn(columnName), x);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateByte(int, byte)
     */
    public void updateByte(int columnIndex, byte x) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateByte(java.lang.String, byte)
     */
    public void updateByte(String columnName, byte x) throws SQLException {
        updateByte(findColumn(columnName), x);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateBytes(int, byte[])
     */
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateBytes(java.lang.String, byte[])
     */
    public void updateBytes(String columnName, byte[] x) throws SQLException {
        updateBytes(findColumn(columnName), x);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, int)
     */
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, int)
     */
    public void updateCharacterStream(String columnName, Reader x, int length) throws SQLException {
        updateCharacterStream(findColumn(columnName), x, length);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateClob(int, java.sql.Clob)
     */
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateClob(java.lang.String, java.sql.Clob)
     */
    public void updateClob(String columnName, Clob x) throws SQLException {
        updateClob(findColumn(columnName), x);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateDate(int, java.sql.Date)
     */
    public void updateDate(int columnIndex, Date x) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateDate(java.lang.String, java.sql.Date)
     */
    public void updateDate(String columnName, Date x) throws SQLException {
        updateDate(findColumn(columnName), x);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateDouble(int, double)
     */
    public void updateDouble(int columnIndex, double x) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateDouble(java.lang.String, double)
     */
    public void updateDouble(String columnName, double x) throws SQLException {
        updateDouble(findColumn(columnName), x);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateFloat(int, float)
     */
    public void updateFloat(int columnIndex, float x) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateFloat(java.lang.String, float)
     */
    public void updateFloat(String columnName, float x) throws SQLException {
        updateFloat(findColumn(columnName), x);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateInt(int, int)
     */
    public void updateInt(int columnIndex, int x) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateInt(java.lang.String, int)
     */
    public void updateInt(String columnName, int x) throws SQLException {
        updateInt(findColumn(columnName), x);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateLong(int, long)
     */
    public void updateLong(int columnIndex, long x) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateLong(java.lang.String, long)
     */
    public void updateLong(String columnName, long x) throws SQLException {
        updateLong(findColumn(columnName), x);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateNull(int)
     */
    public void updateNull(int columnIndex) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateNull(java.lang.String)
     */
    public void updateNull(String columnName) throws SQLException {
        updateNull(findColumn(columnName));
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateObject(int, java.lang.Object)
     */
    public void updateObject(int columnIndex, Object x) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object)
     */
    public void updateObject(String columnName, Object x) throws SQLException {
        updateObject(findColumn(columnName), x);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateObject(int, java.lang.Object, int)
     */
    public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object, int)
     */
    public void updateObject(String columnName, Object x, int scale) throws SQLException {
        updateObject(findColumn(columnName), x, scale);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateRef(int, java.sql.Ref)
     */
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateRef(java.lang.String, java.sql.Ref)
     */
    public void updateRef(String columnName, Ref x) throws SQLException {
        updateRef(findColumn(columnName), x);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateShort(int, short)
     */
    public void updateShort(int columnIndex, short x) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateShort(java.lang.String, short)
     */
    public void updateShort(String columnName, short x) throws SQLException {
        updateShort(findColumn(columnName), x);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateString(int, java.lang.String)
     */
    public void updateString(int columnIndex, String x) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateString(java.lang.String, java.lang.String)
     */
    public void updateString(String columnName, String x) throws SQLException {
        updateString(findColumn(columnName), x);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateTime(int, java.sql.Time)
     */
    public void updateTime(int columnIndex, Time x) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateTime(java.lang.String, java.sql.Time)
     */
    public void updateTime(String columnName, Time x) throws SQLException {
        updateTime(findColumn(columnName), x);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateTimestamp(int, java.sql.Timestamp)
     */
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throwBadCellException(columnIndex);
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateTimestamp(java.lang.String, java.sql.Timestamp)
     */
    public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
        updateTimestamp(findColumn(columnName), x);
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#insertRow()
     */
    public void insertRow() throws SQLException {
    // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#updateRow()
     */
    public void updateRow() throws SQLException {
    // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#deleteRow()
     */
    public void deleteRow() throws SQLException {
    // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#refreshRow()
     */
    public void refreshRow() throws SQLException {
    // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#cancelRowUpdates()
     */
    public void cancelRowUpdates() throws SQLException {
    // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#moveToInsertRow()
     */
    public void moveToInsertRow() throws SQLException {
    // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.sql.ResultSet#moveToCurrentRow()
     */
    public void moveToCurrentRow() throws SQLException {
    // TODO Auto-generated method stub

    }

    /**
     * @see java.sql.ResultSet#getStatement()
     */
    public java.sql.Statement getStatement() throws SQLException {
        return (java.sql.Statement)stmt;
    }


 /*****************
  * Extra Methods *
  *****************/
    private int getStatementPointer() {
        return statementPointer;
    }

    private void setStatementPointer(int p) {
        statementPointer = p;
    }

    private void throwBadCellException(int columnIndex) throws SQLException {
        /* XXX: Row count is based on 1 based index, not 0 based index */
        if(currentRow < 0 || rows[currentRow] == null) {
            throw new SQLException("Invalid row");
        }
        if(columnIndex > ((Object[])rows[currentRow]).length) {
            throw new SQLException("Invalid column");
        }

    }
    /**
     * Inserts a row into the backend representation of the ResultSet.
     *
     * @param index integer representing the index of the row.  This is
     *        the current page, not the entire ResultSet
     * @throws SQLException if the row is not in the current page.
     */
    private void insertRowIntoResultSet(int index) throws SQLException {
        if(index < 0 || index > rows.length) {
            throw new SQLException("Could not insert row, Index out of bounds");
        }
        int colCount = metaData.getColumnCount();
        Object[] row = new Object[colCount];
        rows[index] = row;
        /* XXX: I'm not really all that sure I want the currentRow set here */
        currentRow = index;
    }

    /**
     * Scroll the ResultSet.  If the ResultSet is unidirectional, the whole
     * ResultSet is renewed.  If it is bidirectional, the first half of the
     * ResultSet is filled with the last half of the previous values.
     */
    private void scrollResultSet(int where) {
        /* Scroll a full page size for ResultSet.TYPE_FORWARD_ONLY */
        int start;
        int end;
        rows = new Object[fetchSize];
        if(resultSetType == ResultSet.TYPE_FORWARD_ONLY) {
            start = pageMax + 1;
        } else {
            start = where - (fetchSize / 2);
            if(start < 0) {
                start = 0;
            }
        }
        end = start + fetchSize;
        rows = new Object[fetchSize];
        /* Repopulate the ResultSet with the current page settings */
        pageMin = start;
        pageMax = end;
        populateRows(start, end);
    }


    /* Native Methods. */
    /* This is possibly somewhat misnamed.  Each ResultSet is associated with
     * SQLite3 statement object.  When the result set is closed, the statement
     * object should be closed on the native side.
     */
    private native void proxyCloseStatement() throws SQLException;

    /* Fill the the column col of the current row with a String value */
    private void fillColumnWithString(int col, String value) {
        ((Object[])rows[currentRow])[col] = value;
    }

    /* Fill the the column col of the current row with a Integers value */
    private void fillColumnWithInt(int col, int value) {
        ((Object[])rows[currentRow])[col] = new Integer(value);
    }

    /* Fill the the column col of the current row with a Float value */
    private void fillColumnWithFloat(int col, float value) {
        ((Object[])rows[currentRow])[col] = new Float(value);
    }

    /* Fill the the column col of the current row with a Blob value */
    /* FIXME: This one's all fucked up at this point */
    private void fillColumnWithBlob(int col, byte[] value) {
        ((Object[])rows[currentRow])[col] = value;
    }

    /* Fill the the column col of the current row with the NULL value */
    private void fillColumnWithNull(int col) {
        ((Object[])rows[currentRow])[col] = null;
    }

    /* Native Methods */
    private native void populateRows(int startRow, int finishRow);
}
