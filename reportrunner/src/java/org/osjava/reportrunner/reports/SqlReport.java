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

public class SqlReport extends AbstractReport {

    private static String DEFAULT_RESOURCE = "SqlReportDS";

    private String dsName = DEFAULT_RESOURCE;
    private String sql;
    private String params;

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return this.sql;
    }

    public void setResource(String name, String resourceName) {
        if(DEFAULT_RESOURCE.equals(name)) {
            this.dsName = resourceName;
        }
    }

    protected String getDsName() {
        return this.dsName;
    }

    public Result execute() throws ReportException {
        ReportGroup group = super.getReportGroup();
        Resource resource = group.getResource(this.dsName);
        DataSource ds = (DataSource) resource.accessResource();

        try {
            QueryRunner runner = new QueryRunner(ds);

            Param[] params = getParams();
            ArrayList values = new ArrayList();
            for(int i=0; i<params.length; i++) {

                // hack to handle ?? preprocessing for arrays
                if(Object[].class.isAssignableFrom(params[i].getType())) {
                    Object[] array = (Object[]) params[i].getValue();
                    String marks = StringUtils.chomp(StringUtils.repeat("?,", array.length), ",");
                    this.sql = StringUtils.replaceOnce(this.sql, "??", marks);
                    for(int j=0; j<array.length; j++) {
                        values.add( array[j] );
                    }
                } else {
                    values.add( params[i].getValue() );
                }
            }

            ResultSetHandler handler = new ReportArrayListHandler(this);

            List list = (List) runner.query(this.sql, values.toArray(), handler);

            if(getColumns().length == 0) {
                // should goto the ResultSetMetaData and get the column names
                // this means not using the DBUtils code above
            }

            Object[] array = list.toArray(new Object[0]);

            if(array == null) {
                return new NullResult();
            }
            if(array.length == 0) {
                return new NullResult();
            }
            if(array[0] == null) {
                return new NullResult();
            }
            if(!(array[0] instanceof Object[])) {
                throw new RuntimeException("The array consists of "+array[0].getClass() );
            }

            return new ArrayResult(array);
        } catch(SQLException sqle) {
            throw new ReportException("Unable to run SQL report", sqle);
        }
    }

    // returns a Choice[]
    public Choice[] getParamChoices(Param param) {
        String binding = param.getBinding();

        if(binding == null) {
            return null;
        }

        // assume binding is an SQL statement that returns 
        // 2 columns. The first is key, the second is value.
        
        ReportGroup group = super.getReportGroup();
        Resource resource = group.getResource(this.dsName);
        if(resource == null) {
            throw new RuntimeException("Unable to obtain resource named "+this.dsName+" from the "+group.getName()+" group. ");
        }
        DataSource ds = (DataSource) resource.accessResource();

        try {
            QueryRunner runner = new QueryRunner(ds);
            List list = (List) runner.query(binding, new ArrayListHandler());
            Choice[] choices = new Choice[list.size()];
            for(int i=0; i<choices.length; i++) {
                Object[] array = (Object[]) list.get(i);
                Choice c = new Choice(""+array[0], ""+array[1]);
                choices[i] = c;
            }
            return choices;
        } catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }

    public String[] getResourceNames() {
        return new String[] { DEFAULT_RESOURCE };
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
