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
 * + Neither the name of Genjava-Core nor the names of its contributors 
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
package com.generationjava.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public final class JdbcW {

    // assumes next() has been called already
    static Object[] resultSetToArray(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int sz = meta.getColumnCount();
        Object[] objs = new Object[sz];
        for(int i=0; i<sz; i++) {
            Object obj = rs.getObject(i+1);
            if(rs.wasNull()) {
                obj = null;
            }
            objs[i] = obj;
        }
        return objs;
    }

    static public String[] getColumnNames(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int sz = rsmd.getColumnCount();
        String[] names = new String[sz];
        for(int i=0; i<sz; i++) {
            names[i] = rsmd.getColumnName(i+1);
//            System.err.println(names[i]);
        }
        return names;
    }

    static public int getColumnType(String column, Connection conn) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        String catalog = null;
        String schema = null;
        String table = null;
        String columnName = null;
        String[] args = StringUtils.split(column, ".");
        if(args.length == 4) {
            catalog = args[0];
            schema = args[1];
            table = args[2];
            columnName = args[3];
        } else
        if(args.length == 3) {
            schema = args[0];
            table = args[1];
            columnName = args[2];
        } else {
            table = args[0];
            columnName = args[1];
        }
//        System.err.println("Getting from: "+catalog+";"+schema+";"+table);
        ResultSet rs = meta.getColumns(catalog, schema, table, columnName);
        if(rs.next()) {
            return rs.getInt(5);
        } else {
            return -1;
        }
    }

    static public String[] getPrimaryKeys(String tablename, Connection conn) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        String catalog = null;
        String schema = null;
        String table = null;
        String[] args = StringUtils.split(tablename, ".");
        if(args.length == 3) {
            catalog = args[0];
            schema = args[1];
            table = args[2];
        } else
        if(args.length == 2) {
            schema = args[0];
            table = args[1];
        } else {
            table = args[0];
        }
//        System.err.println("Getting from: "+catalog+";"+schema+";"+table);
        ResultSet rs = meta.getPrimaryKeys(catalog, schema, table);
        ArrayList list = new ArrayList();
        while(rs.next()) {
            rs.getObject(1);
            rs.getObject(2);
            rs.getObject(3);
            list.add(rs.getObject(4));
        }
        return (String[])list.toArray(new String[0]);
    }

    static public Object getAutoIncrement(String tablename, Connection conn, String pkey) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet rs = meta.getPrimaryKeys(null, null, tablename);
        ResultSetMetaData rsmd = rs.getMetaData();
        HashMap map = new HashMap();
        while(rs.next()) {
            rs.getObject(1);
            rs.getObject(2);
            rs.getObject(3);
            String pk = rs.getString(4);
            if(pkey.equals(pk)) {
                return rs.getObject(5);
            }
        }

        return null;
    }

    static public Map getAutoIncrements(String tablename, Connection conn) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getPrimaryKeys(null, null, tablename);
            ResultSetMetaData rsmd = rs.getMetaData();
            HashMap map = new HashMap();
            while(rs.next()) {
                System.err.println(rs.getObject(1));
                System.err.println(rs.getObject(2));
                System.err.println(rs.getObject(3));
                System.err.println(rs.getObject(4));
                map.put(rs.getObject(4), rs.getObject(5));
                System.err.println(rs.getObject(6));
            }
            return map;
        } catch(SQLException sqle) {
            sqle.printStackTrace();
            return null;
        }
    }

    static public void printWarnings(SQLWarning warning) {
        if(warning == null) {
            return;
        }
        System.err.println( formatWarnings(warning) );
    }

    static public String formatWarnings(SQLWarning warning) {
        StringBuffer buffer = new StringBuffer();
        while(warning != null) {
            buffer.append("SQLWarning: ");
            buffer.append( warning.getMessage() );
            buffer.append("\nSQL State: ");
            buffer.append( warning.getSQLState( ));
            buffer.append("\nErrorCode: ");
            buffer.append( warning.getErrorCode( ));
            buffer.append("\n");
            warning = warning.getNextWarning();
        }
        return buffer.toString();
    }

}

