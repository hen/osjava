package org.osjava.runsql;

import java.io.*;
import java.sql.*;
import javax.sql.*;
import javax.naming.*;
import org.apache.commons.dbutils.*;
import org.apache.commons.lang.*;
import com.generationjava.io.*;

public class RunSql {

    public static void main(String[] args) throws Exception {
        String dsname = args[0];
        InitialContext ctxt = new InitialContext();
        DataSource ds = (DataSource)ctxt.lookup(dsname);
        Connection conn = ds.getConnection();
        long start = System.currentTimeMillis();
        try {
            runScript(conn, System.in);
        } catch(SQLException sqle) {
            System.err.println("Script failure. ");
            sqle.printStackTrace();
        } finally {
            DbUtils.closeQuietly(conn);
        }
        long duration = System.currentTimeMillis() - start;
        System.err.println("Script executed in : " + duration);
    }

    static public void runScript(Connection conn, InputStream in) throws SQLException {
        runScript(conn, FileW.loadFile(in));
    }

    static public void runScript(Connection conn, String script) throws SQLException {
        // TODO: Improve so a ; inside a string is ignored.
        String[] stmts = StringUtils.split(script, ";");
        Statement stmt = conn.createStatement();
        try {
            long start = System.currentTimeMillis();
            for(int i=0; i<stmts.length; i++) {
                boolean resultset = stmt.execute(stmts[i]);
//                System.err.println("Executing: "+stmts[i]);
                if(resultset) {
//                    System.out.println("Result: ");
                    ResultSet rs = stmt.getResultSet();
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int count = rsmd.getColumnCount();
                    count++;
                    for(int j=1; j<count; j++) {
                        System.out.print(""+rsmd.getColumnName(j));
                        System.out.print(" | ");
                    }
                    System.out.println();
                    while(rs.next()) {
                        for(int j=1; j<count; j++) {
                            System.out.print(""+rs.getObject(j));
                            System.out.print(" | ");
                        }
                        System.out.println();
                    }
                } else {
//                    System.err.println("Rows changed: "+stmt.getUpdateCount());
                }
            }
            long duration = System.currentTimeMillis() - start;
            System.err.println("Statement executed in : " + duration);
        } finally {
            DbUtils.closeQuietly(stmt);
        }
    }

}
