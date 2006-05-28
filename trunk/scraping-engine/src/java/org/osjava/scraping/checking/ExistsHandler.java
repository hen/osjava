package org.osjava.scraping.checking;

import java.sql.*;

import org.apache.commons.dbutils.*;

public class ExistsHandler implements ResultSetHandler {

    public Object handle(ResultSet rs) throws SQLException {
        return new Boolean(rs.next());
    }

}
