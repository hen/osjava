/*
 * Copyright (c) 2005, Henri Yandell
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
package org.osjava.scraping.checking;

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

public class JdbcChecker implements Checker {

    private static Logger logger = Logger.getLogger(JdbcChecker.class);

    public boolean exists(Header header, Config cfg, Session session) throws CheckingException {
        Connection conn = null;
        try {
            String dsname = cfg.getString("jdbc.DS");
            DataSource ds = (DataSource)cfg.getAbsolute(dsname);
            conn = ds.getConnection();
            String sql = cfg.getString("jdbc.sql");
            if(sql == null) {
                throw new CheckingException(cfg.getContext()+".jdbc.sql or "+cfg.getContext()+".jdbc.table must be specified. ");
            }

            QueryRunner queryRunner = new QueryRunner();
            Object[] value = (Object[]) header.getValue();
            return executeSql( conn, sql, value, queryRunner );
        } catch(SQLException sqle) {
            throw new CheckingException("JDBC Checking Error: "+sqle.getMessage(), sqle);
        } finally {
            DbUtils.closeQuietly( conn );
        }
    }

    protected boolean executeSql(Connection conn, String sql, Object[] header, QueryRunner queryRunner ) throws SQLException {
        return ((Boolean) queryRunner.query( conn, sql, header, new ExistsHandler() )).booleanValue();
    }

}
