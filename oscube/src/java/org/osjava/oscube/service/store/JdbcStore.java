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
 * + Neither the name of OSJava nor the names of its contributors 
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
package org.osjava.oscube.service.store;

import java.util.Iterator;
import javax.sql.DataSource;
import java.sql.*;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

import com.generationjava.config.Config;
import org.osjava.oscube.container.Session;
import org.osjava.oscube.container.Header;
import org.osjava.oscube.container.Result;

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

        QueryRunner queryRunner = new QueryRunner();
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
            int count = queryRunner.update( conn, sql, row );
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
