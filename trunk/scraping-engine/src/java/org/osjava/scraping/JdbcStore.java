package org.osjava.scraping;

import java.util.Iterator;
import javax.sql.DataSource;
import java.sql.*;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

public class JdbcStore implements Store {

    private static Logger logger = Logger.getLogger(JdbcStore.class);

    public void store(Result result, Config cfg, Session session) throws StoringException {
        Connection conn = null;
try {
        // get DataSource from cfg
        String dsname = cfg.getString("jdbc.DS");
//        System.err.println("DSNAME: "+dsname);
//        System.err.println("/DSNAME: "+cfg.getAbsolute(dsname));
        DataSource ds = (DataSource)cfg.getAbsolute(dsname);
//        System.err.println("DataSource: "+ds);
        conn = ds.getConnection();
//        System.err.println("CONN: "+conn);
        String sql = cfg.getString("jdbc.sql");
//        System.err.println("SQL: "+sql);
        String table = cfg.getString("jdbc.table");
//        System.err.println("Table: "+table);
        if(sql == null) {
            if(table == null) {
                throw new StoringException(cfg.getContext()+".jdbc.sql or "+cfg.getContext()+".jdbc.table must be specified. ");
            }
        }

        Iterator iterator = result.iterateRows();
        while(iterator.hasNext()) {
            Object[] row = (Object[])iterator.next();
            if(row.length == 0) {
                logger.error("Empty row found. Skipping. ");
                continue;
            }
            if(sql == null) {
                if(table != null) {  
                    sql = "INSERT INTO " + table + " VALUES(?"+ StringUtils.repeat(", ?", row.length-1) + ")";
                }
            }
            int count = DbUtils.executeUpdate( conn, sql, row );
        }

} catch(SQLException sqle) {
    throw new StoringException("JDBC Storing Error: "+sqle.getMessage(), sqle);
} finally {
        DbUtils.closeQuietly( conn );
}
    }

    public boolean exists(Header header, Config cfg, Session session) throws StoringException {
        // need to write some kind of checking in here
        return false;
    }
}
