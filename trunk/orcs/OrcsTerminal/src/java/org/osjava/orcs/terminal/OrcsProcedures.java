/*
 * Created on Oct 11, 2005
 */
package org.osjava.orcs.terminal;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;

import com.generationjava.jdbc.JdbcW;

/**
 * @author hyandell
 */
public class OrcsProcedures {
    
    private static DataSource dataSource;
    
    public static void setDataSource(DataSource ds) {
        dataSource = ds;
    }

    /**
     * List of Object[]
     */
    public static List getTableAtDate(String schema, String table, Date date, boolean deleted) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String columns = StringUtils.join(getColumnNamesForTable(schema, table), ",");

            String sql =                     
                "SELECT orcs_row_id, " + columns + " FROM " + schema + "." + table + 
                "_$ORCS WHERE (orcs_row_id, orcs_revision) IN ( " +
                "    SELECT orcs_row_id, MAX(orcs_revision) FROM " + schema + "." + table + "_$ORCS " +
                "     WHERE orcs_insertion_time <= ? " +
                "     GROUP BY orcs_row_id ) ";
            
            if(deleted == false) {
                sql += " AND orcs_deleted IS NULL";
            }

            ps = conn.prepareStatement(sql);
                    
            ps.setTimestamp(1, new java.sql.Timestamp(date.getTime()));
            rs = ps.executeQuery();
            
            BasicRowProcessor brp = BasicRowProcessor.instance();
            
            List list = new LinkedList();
            
            while(rs.next()) {
                list.add(brp.toArray(rs));
            }
            
            return list;
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }
    }
    
    public static List getTableRowAtRevision(String schema, String table, Object orcsRowId, Object revision) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = getConnection();
            String columns = StringUtils.join(getColumnNamesForTable(schema, table), ",");
            ps = conn.prepareStatement("SELECT " + columns + " FROM " + table +"_$ORCS WHERE orcs_row_id=? AND orcs_revision=?");
            ps.setObject(1, orcsRowId);
            ps.setObject(2, revision);
            rs = ps.executeQuery();
            
            BasicRowProcessor brp = BasicRowProcessor.instance();
            
            List list = new LinkedList();
            
            if(rs.next()) {
                Object[] array = brp.toArray(rs);
                ResultSetMetaData rsmd = rs.getMetaData();
                for(int i=0; i<array.length; i++) {
                    list.add( new Object[] { orcsRowId, rsmd.getColumnName(i + 1), array[i]} );
                }
            }
            
            return list;
        } finally {
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(conn, ps, rs);
        }
    }
    
    public static List getRevisionsForOrcsRowId(String schema, String table, Object orcsRowId) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            
            String sql = "SELECT orcs_deleted, orcs_revision, orcs_insertion_time FROM " + schema + "." + table + "_$ORCS WHERE orcs_row_id=? ORDER BY orcs_revision";
            ps = conn.prepareStatement(sql);
            ps.setObject(1, orcsRowId);
            rs = ps.executeQuery();
            List list = new LinkedList();
            while(rs.next()) {
                Object[] tmp = new Object[] { rs.getObject(1), rs.getObject(2), rs.getObject(3) };
                if(tmp[0] != null) {
                    tmp[1] = "DELETED";
                }
                list.add( tmp );
            }
            rs.close();
            return list;
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }
    }
    
    public static String[] getSchemaNames() throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet schemas = dmd.getSchemas();
            List list = new LinkedList();
            while(schemas.next()) {
                list.add(schemas.getObject(1));
            }
            schemas.close();
            return (String[]) list.toArray(new String[0]);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }
    
    public static String[] getTableNamesForSchema(String schema, String filter) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet tables = dmd.getTables("", schema, "%"+filter, null);
            List list = new LinkedList();
            while(tables.next()) {
                list.add(tables.getObject(3));
            }
            tables.close();
            return (String[]) list.toArray(new String[0]);
        } finally {
            DbUtils.closeQuietly(conn);
        }        
    }
    
    public static String[] getColumnNamesForTable(String schema, String table) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet columns = dmd.getColumns("", schema, table, null);
            List list = new LinkedList();
            while(columns.next()) {
                list.add(columns.getObject(4));
            }
            columns.close();
            return (String[]) list.toArray(new String[0]);
        } finally {
            DbUtils.closeQuietly(conn);
        }        
    }
    
    public static void rollback(String schema, String table, String[] columnNames, Object[] values) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            
            String schemaTable = schema + "." + table;

            String[] primaryKeys = JdbcW.getPrimaryKeys(schemaTable, conn);
            int[] primaryIndexes = new int[primaryKeys.length];
            String primaryClause = "";
            for(int i=0; i<columnNames.length; i++) {
                for(int j=0; j<primaryKeys.length; j++) {
                    if(primaryKeys[j].equalsIgnoreCase(columnNames[i])) {
                        primaryIndexes[j] = i;
                        primaryClause = primaryKeys[j] + "=?,";
                    }
                }
            }
            primaryClause = primaryClause.substring(0, primaryClause.length() - 1);
            
            // select code
            String selectSql = "SELECT count(*) FROM " + schemaTable + " WHERE " + primaryClause;
            ps = conn.prepareStatement(selectSql);
            for(int i=0; i<primaryKeys.length; i++) {
                ps.setObject(i + 1, values[primaryIndexes[i]]);
            }
            rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            ps.close();

            if(count == 1) {
                // update code
                String updateSql = "UPDATE " + schemaTable +" SET ";
                for(int i=0; i<values.length; i++) {
                    updateSql += columnNames[i] + "=?,";
                }
                updateSql = updateSql.substring(0, updateSql.length() - 1);
                updateSql += " WHERE "+primaryClause;
                ps = conn.prepareStatement(updateSql);
                for(int i=0; i<values.length; i++) {
                    ps.setObject(i+1, values[i]);
                }
                for(int i=0; i<primaryKeys.length; i++) {
                    ps.setObject(i + 1 + values.length, values[primaryIndexes[i]]);
                }
                int rows = ps.executeUpdate();
                if(rows != 1) {
                    throw new SQLException("Should have rolled back 1 row via UPDATE");
                }
            } else 
            if(count == 0) {
                // insert code
                String columns = StringUtils.join(columnNames, ",");            
                String[] array = new String[columnNames.length];
                Arrays.fill(array, "?");
                String questions = StringUtils.join(array, ",");            
                String insertSql = "INSERT INTO " + schemaTable + " (" + columns + ") VALUES("+questions+")";
                ps = conn.prepareStatement(insertSql);
                for(int i=0; i<values.length; i++) {
                    ps.setObject(i+1, values[i]);
                }
                int rows = ps.executeUpdate();
                if(rows != 1) {
                    throw new SQLException("Should have rolled back 1 row via INSERT");
                }
            }
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch(ClassNotFoundException cnfe) {
            throw new RuntimeException("Unable to load oracle driver");
        }
        return dataSource.getConnection();
//        return DriverManager.getConnection("jdbc:oracle:thin:@devdb.genscape.com:1532:dev", "genscape", "beer123");
    }

}
