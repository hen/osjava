/*
 * org.osjava.jdbc.sqlite.ResultSetMetaData
 * $Id$
 * $Rev$ 
 * $Date$ 
 * $Author$
 * $URL$
 * 
 * Created on Jul 10, 2005
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

import java.sql.SQLException;
import java.sql.Types;

/**
 * @author rzigweid
 *
 */
public class ResultSetMetaData implements java.sql.ResultSetMetaData {
    
    private ResultSet resultSet;
    
    /**
     * The number of columns in the ResultSet
     */
    private int columnCount = -1;
    
   /** 
    * Array containing the names of columns for the ResultSet
    */
    String[] columnNames = null;
    
    /**
     * Array containing the declared SQL types of columns for the ResultSet.
     */
    String[] columnTypeNames = null;

    public ResultSetMetaData(ResultSet rs) {
        super();
        resultSet = rs;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSetMetaData#getColumnCount()
     */
    public int getColumnCount() throws SQLException {
        return columnCount;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSetMetaData#isAutoIncrement(int)
     */
    public boolean isAutoIncrement(int column) throws SQLException {
        if(column >= columnCount) {
            throw new SQLException("Invalid column");
        }
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSetMetaData#isCaseSensitive(int)
     */
    public boolean isCaseSensitive(int column) throws SQLException {
        if(column >= columnCount) {
            throw new SQLException("Invalid column");
        }
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSetMetaData#isSearchable(int)
     */
    public boolean isSearchable(int column) throws SQLException {
        if(column >= columnCount) {
            throw new SQLException("Invalid column");
        }
        // TODO Auto-generated method stub
        return true;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSetMetaData#isCurrency(int)
     */
    public boolean isCurrency(int column) throws SQLException {
        if(column >= columnCount) {
            throw new SQLException("Invalid column");
        }
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSetMetaData#isNullable(int)
     */
    public int isNullable(int column) throws SQLException {
        if(column >= columnCount) {
            throw new SQLException("Invalid column");
        }
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSetMetaData#isSigned(int)
     */
    public boolean isSigned(int column) throws SQLException {
        if(column >= columnCount) {
            throw new SQLException("Invalid column");
        }
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSetMetaData#getColumnDisplaySize(int)
     */
    public int getColumnDisplaySize(int column) throws SQLException {
        if(column >= columnCount) {
            throw new SQLException("Invalid column");
        }
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSetMetaData#getColumnLabel(int)
     */
    public String getColumnLabel(int column) throws SQLException {
        if(column >= columnCount) {
            throw new SQLException("Invalid column");
        }
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSetMetaData#getColumnName(int)
     */
    public String getColumnName(int column) throws SQLException {
        if(column >= columnCount) {
            throw new SQLException("Invalid column");
        }
        /* column is 1 based, column names are 0 based. */
        return columnNames[column - 1];
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSetMetaData#getSchemaName(int)
     */
    public String getSchemaName(int column) throws SQLException {
        if(column >= columnCount) {
            throw new SQLException("Invalid column");
        }
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSetMetaData#getPrecision(int)
     */
    public int getPrecision(int column) throws SQLException {
        if(column >= columnCount) {
            throw new SQLException("Invalid column");
        }
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSetMetaData#getScale(int)
     */
    public int getScale(int column) throws SQLException {
        if(column >= columnCount) {
            throw new SQLException("Invalid column");
        }
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSetMetaData#getTableName(int)
     */
    public String getTableName(int column) throws SQLException {
        if(column >= columnCount) {
            throw new SQLException("Invalid column");
        }
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSetMetaData#getCatalogName(int)
     */
    public String getCatalogName(int column) throws SQLException {
        if(column >= columnCount) {
            throw new SQLException("Invalid column");
        }
        /* TODO: This will need to be implemented at a later point, but the
         *       utility of this method is not very high IMHO */
        return "";
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSetMetaData#getColumnType(int)
     */
    public int getColumnType(int column) throws SQLException {
        if(column >= columnCount) {
            throw new SQLException("Invalid column");
        }
        /* This is based upon the type of the declaration type of the column.
         * This approach was taken because of the way that SQLITE 3 deals with
         * types.  Firstly, there are only 5 real types in the underlying 
         * database, and even then they can be acquired as different types,
         * usually strings.  This is going to produce a more accurae result.*/
        /* Lowercase the declared type and use that as the key to everything.*/
        String declType = columnTypeNames[column - 1].toLowerCase();
        if(declType.equals("array")) {
            return Types.ARRAY;
        } else if(declType.equals("bigint")) {
            return Types.BIGINT;
        } else if(declType.equals("binary")) {
            return Types.BINARY;
        } else if(declType.equals("bit")) {
            return Types.BIT;
        } else if(declType.equals("blob")) {
            return Types.BLOB;
        } else if(declType.equals("boolean")) {
            return Types.BOOLEAN;
        } else if(declType.equals("char")) {
            return Types.CHAR;
        } else if(declType.equals("clob")) {
            return Types.CLOB;
        } else if(declType.equals("datalink")) {
            return Types.DATALINK;
       } else if(declType.equals("date")) {
            return Types.DATE;
        } else if(declType.equals("decimal")) {
            return Types.DECIMAL;
        } else if(declType.equals("double")) {
            return Types.DOUBLE;
        } else if(declType.equals("float")) {
            return Types.FLOAT;
        } else if(declType.equals("integer")) { 
            return Types.INTEGER;
        } else if(declType.equals("java_object")) {
            return Types.JAVA_OBJECT;
        } else if(declType.equals("longvarbinary")) {
            return Types.LONGVARBINARY;
        } else if(declType.equals("longvarchar")) {
            return Types.LONGVARCHAR;
        } else if(declType.equals("numeric")) {
            return Types.NUMERIC;
        } else if(declType.equals("real")) {
            return Types.REAL;
        } else if(declType.equals("ref")) {
            return Types.REF;
        } else if(declType.equals("smallint")) {
            return Types.SMALLINT;
        } else if(declType.equals("struct")) {
            return Types.STRUCT;
        } else if(declType.equals("time")) {
            return Types.TIME;
        } else if(declType.equals("timestamp")) {
            return Types.TIMESTAMP;
        } else if(declType.equals("tinyint")) {
            return Types.TINYINT;
        } else if(declType.equals("varbinary")) {
            return Types.VARBINARY;
        } else if(declType.startsWith("varchar")) {
            return Types.VARCHAR;
        } else {
            return Types.OTHER;
        }
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSetMetaData#getColumnTypeName(int)
     */
    public String getColumnTypeName(int column) throws SQLException {
        if(column >= columnCount) {
            throw new SQLException("Invalid column");
        }
        return columnTypeNames[column - 1];
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSetMetaData#isReadOnly(int)
     */
    public boolean isReadOnly(int column) throws SQLException {
        if(column >= columnCount) {
            throw new SQLException("Invalid column");
        }
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSetMetaData#isWritable(int)
     */
    public boolean isWritable(int column) throws SQLException {
        if(column >= columnCount) {
            throw new SQLException("Invalid column");
        }
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSetMetaData#isDefinitelyWritable(int)
     */
    public boolean isDefinitelyWritable(int column) throws SQLException {
        if(column >= columnCount) {
            throw new SQLException("Invalid column");
        }
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.sql.ResultSetMetaData#getColumnClassName(int)
     */
    public String getColumnClassName(int column) throws SQLException {
        if(column >= columnCount) {
            throw new SQLException("Invalid column");
        }
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * Support Methods, not in the spec.
     */
    /**
     * Set the number of columns in the ResultSet.
     */
    private void setColumnCount(int count) {
        /* FIXME: Throw a SQLException if the count is changed.  It should
         *        never change.
         */
        columnCount = count;
        columnNames = new String[columnCount];
        columnTypeNames = new String[columnCount];
    }
    
    /* Ugh, package private */
    /**
     * Get the 1-based index of the column with the name <code>colName</code>.
     * The first match is the column index returned if there are duplicates.
     * -1 is returned if no match is found. 
     * 
     * @param colName the name of the column being searched for
     * @return 0 
     */
    int getColumnForName(String colName) {
        for(int i = 0; i < columnNames.length; i++) {
            if(columnNames[i].equals(colName)) {
                return i+1;
            }
        }
        return -1;
    }
    
    /** 
     * Set the name of a column
     */
    private void setColumnName(int col, String name) throws SQLException {
        /* XXX: Note that column counts are 1 based in the api and 0 based here. */
        if(col >= columnCount) {
            throw new SQLException("Invalid column");
        }
        columnNames[col] = name;
    }
    
    /**
     * Set the SQL type of a column.  The value stored is a string directly
     * from the declared type and will need to be parsed. 
     */
    private void setColumnTypeName(int col, String type) throws SQLException {
        /* XXX: Note that column counts are 1 based in the api and 0 based here. */
        if(col >= columnCount) {
            throw new SQLException("Invalid column");
        }
        columnTypeNames[col] = type;
    }
}
