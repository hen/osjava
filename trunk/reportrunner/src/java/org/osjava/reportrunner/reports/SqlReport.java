package org.osjava.reportrunner.reports;

import org.apache.commons.lang.*;
import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.*;
import org.osjava.reportrunner.*;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import javax.sql.DataSource;
import javax.naming.*;

public class SqlReport extends AbstractSqlReport {

    protected Object[] executeSql(DataSource ds) throws ReportException {
        try {
            Param[] params = getParams();
            ArrayList values = new ArrayList();
            for(int i=0; i<params.length; i++) {

                // hack to handle ?? preprocessing for arrays
                if(Object[].class.isAssignableFrom(params[i].getType())) {
                    Object value = params[i].getValue();
                    Object[] array = null;
                    if(value instanceof Object[]) {
                        array = (Object[]) params[i].getValue();
                    } else {
                        array = new Object[] { value };
                    }
                    System.out.println("?? MARK HACK "+array.length);
                    String marks = StringUtils.chomp(StringUtils.repeat("?,", array.length), ",");
                    setSql(StringUtils.replaceOnce(getSql(), "??", marks));
                    for(int j=0; j<array.length; j++) {
                        values.add( array[j] );
                    }
                } else {
                    values.add( params[i].getValue() );
                }
            }

            QueryRunner runner = new QueryRunner(ds);

            ResultSetHandler handler = new ReportArrayListHandler(this);

            List list = (List) runner.query(getSql(), values.toArray(), handler);

            return list.toArray(new Object[0]);
        } catch(SQLException sqle) {
            throw new ReportException("Unable to run SQL report", sqle);
        }
    }

    private class ReportArrayListHandler extends ArrayListHandler {
        
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


}
