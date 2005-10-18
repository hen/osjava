/*
 * Created on Oct 18, 2005
 */
package org.osjava.orcs.setup;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;
import org.osjava.orcs.terminal.OrcsProcedures;

import com.generationjava.jdbc.JdbcW;

/**
 * @author hyandell
 */
public class SetupProcedures {

    public static String postfix = "_$ORCS";
    private static Set noTypeSet = new HashSet();
    
    static { 
        noTypeSet.add("DATE");
    }
    
    public static void main(String[] args) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            setupOrcsForTable(conn, "GENSCAPE", "ENTITY");
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }
    
    public static void setupOrcsForTable(Connection conn, String schema, String table) throws SQLException {
        
        String fqTable = schema + "." + table;
        String fqOrcsTable = fqTable + postfix;
        
        // create insert trigger
        // create update trigger
        // create delete trigger

        // create _$ORCS table
        String createSql = 
            "CREATE TABLE " + fqOrcsTable + " ( \n" + 
            "  ORCS_ID              NUMBER(19)               NOT NULL, \n" +
            "  ORCS_REVISION        INTEGER                  NOT NULL, \n" +
            "  ORCS_DELETED         INTEGER, \n" +
            "  ORCS_INSERTION_TIME  DATE                     DEFAULT CURRENT_TIMESTAMP     NOT NULL, \n" +
            "  ORCS_TRANSACTION_ID  VARCHAR2(100 BYTE)       NOT NULL, \n" +
            "  ORCS_ROW_ID          NUMBER(19)               NOT NULL, \n" +
            generateCreateColumns(conn, schema, table) + 
            "\n)";
            
        System.err.println("CREATE: " + createSql);
        
        
    }
    
    public static String generateCreateColumns(Connection conn, String schema, String table) throws SQLException {
        ResultSet columns = null;
        try {
            DatabaseMetaData dmd = conn.getMetaData();
            columns = dmd.getColumns("", schema, table, null);
            BasicRowProcessor brp = BasicRowProcessor.instance();
            
            StringBuffer create = new StringBuffer();
            
            while(columns.next()) {
                String type = columns.getString(6);
                create.append(columns.getString(4));
                create.append("     ");
                create.append(type);
                if(!noTypeSet.contains(type)) {
                    create.append("(");
                    create.append(columns.getString(7));
                    create.append(")");
                }
                create.append(", \n");
            }
            
            String ret = create.toString();
            return ret.substring(0, ret.length() - ", \n".length());
        } finally {
            DbUtils.closeQuietly(columns);
        }        
    }
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch(ClassNotFoundException cnfe) {
            throw new RuntimeException("Unable to load oracle driver");
        }
        return DriverManager.getConnection("jdbc:oracle:thin:@devdb.genscape.com:1532:dev", "genscape", "beer123");
    }

    public static final String updateTriggerSql = 
      "CREATE OR REPLACE TRIGGER GENSCAPE.orcs_update_${table} " + 
      "AFTER UPDATE ON ${table} " + 
      "FOR EACH ROW " +
      "DECLARE  " +
      "   v_orcs_row_id               NUMBER(11);  " +
      "   v_orcs_revision             INTEGER; " + 
      "BEGIN  " +
      "  SELECT orcs_row_id, MAX(orcs_revision)  " +
      "  INTO v_orcs_row_id, v_orcs_revision  " +
      "  FROM ${table}_$orcs WHERE ${primaryKeys} group by orcs_row_id; " + 
      "  v_orcs_revision := v_orcs_revision + 1;  " +
      "  INSERT INTO ${table}_$orcs (orcs_id, orcs_revision, orcs_transaction_id, orcs_row_id, ${columns}) " + 
      "  VALUES (seq_orcs_id.nextval, v_orcs_revision, dbms_transaction.local_transaction_id, v_orcs_row_id, ${new:columns} " + 
      "    ); " +  
      "END;";
    
    public static void test(Connection conn, String schema, String table) throws SQLException {
        String fqTable = schema + "." + table;
        String[] columnsArray = OrcsProcedures.getColumnNamesForTable(schema, table);
        String[] primaryKeys = JdbcW.getPrimaryKeys(fqTable, conn);
        
        String columns = StringUtils.join(columnsArray, ",");
        String newColumns = ":new." + StringUtils.join(columnsArray, ", :new.");
        String oldColumns = ":old." + StringUtils.join(columnsArray, ", :old.");
        
        StringBuffer primaryKeysEquals = new StringBuffer();
        for(int i=0; i<primaryKeys.length; i++) {
            primaryKeysEquals.append(primaryKeys[i]);
            primaryKeysEquals.append("=:old.");
            primaryKeysEquals.append(primaryKeys[i]);
            if(i != primaryKeys.length - 1) {
                primaryKeysEquals.append(" AND ");
            }
        }
    }
    
    public static final String deleteTriggerSql = 
      "CREATE OR REPLACE TRIGGER GENSCAPE.orcs_delete_${table} " +  
      "AFTER DELETE ON ${table}  " + 
      "FOR EACH ROW " + 
      "DECLARE  " + 
      "   v_orcs_row_id               NUMBER(11); " +  
      "   v_orcs_revision             INTEGER;  " + 
      "BEGIN  " + 
      "  SELECT orcs_row_id, MAX(orcs_revision) " +  
      "  INTO v_orcs_row_id, v_orcs_revision  " + 
      "  FROM ${table}_$orcs WHERE ${primaryKeys}  " + 
      "  GROUP BY orcs_row_id;  " + 
      "  v_orcs_revision := v_orcs_revision + 1; " +  
      "  INSERT INTO ${table}_$orcs (orcs_id, orcs_revision, orcs_transaction_id, orcs_row_id, orcs_deleted, ${columns} ) " +  
      "  VALUES (seq_orcs_id.nextval, v_orcs_revision, dbms_transaction.local_transaction_id, v_orcs_row_id, 1, ${old:columns} ); " +  
      "END; ";


    public static final String insertTriggerSql = 
      "CREATE OR REPLACE TRIGGER GENSCAPE.orcs_insert_${table} " +  
      "AFTER INSERT ON ${table}  " + 
      "FOR EACH ROW " + 
      "DECLARE  " + 
      "   v_orcs_row_id               NUMBER(11); " +  
      "   v_orcs_revision             INTEGER;  " + 
      "BEGIN  " + 
      "  SELECT orcs_row_id, MAX(orcs_revision) " +  
      "  INTO v_orcs_row_id, v_orcs_revision  " + 
      "  FROM ${table}_$orcs WHERE ${primaryKeys}  " + 
      "  GROUP BY orcs_row_id; " + 
      "  " + 
      "  v_orcs_revision := v_orcs_revision + 1; " +  
      "  " + 
      "  INSERT INTO ${table}_$orcs (orcs_id, orcs_revision, orcs_transaction_id, orcs_row_id, ${table}) " +  
      "  VALUES (seq_orcs_id.nextval, v_orcs_revision, dbms_transaction.local_transaction_id, v_orcs_row_id, ${new:columns} ); " +  
      "  " +   
      "  EXCEPTION " + 
      "    WHEN no_data_found THEN " + 
      "      DBMS_OUTPUT.PUT_LINE('NO DATA FOUND'); " + 
      "      INSERT INTO ${table}_$orcs (orcs_id, orcs_revision, orcs_transaction_id, orcs_row_id, ${columns}) " +  
      "      VALUES (seq_orcs_id.nextval, 1, dbms_transaction.local_transaction_id, seq_orcs_row_id.nextval, ${new:columns} ); " + 
      "END; ";
    
}
