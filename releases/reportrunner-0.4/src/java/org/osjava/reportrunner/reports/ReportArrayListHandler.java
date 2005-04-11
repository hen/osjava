package org.osjava.reportrunner.reports;

import java.sql.*;
import org.apache.commons.dbutils.handlers.*;
import org.osjava.reportrunner.Report;
import org.osjava.reportrunner.Column;

class ReportArrayListHandler extends ArrayListHandler {
        
    private Report report;

    ReportArrayListHandler(Report report) {
        this.report = report;
    }

    public Object handle(ResultSet rs) throws SQLException {
        Object obj = super.handle(rs);
        if(this.report.getColumns().length == 0) {
            // use meta data to guess column names
            ResultSetMetaData meta = rs.getMetaData();
            int sz = meta.getColumnCount();
            for(int i=1; i<=sz; i++) {
                Column column = new Column();
                column.setName(meta.getColumnName(i));
                column.setLabel(meta.getColumnLabel(i));
                this.report.addColumn(column);
            }
        }
        return obj;
    }

}
