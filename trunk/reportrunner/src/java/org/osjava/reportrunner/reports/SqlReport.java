package org.osjava.reportrunner.reports;

import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.*;
import org.osjava.reportrunner.*;

import java.sql.*;
import java.util.List;
import javax.sql.DataSource;
import javax.naming.*;

public class SqlReport extends AbstractReport {

    private String dsName;
    private String sql;
    private String params;

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return this.sql;
    }

    public void setDataSource(String dsName) {
        this.dsName = dsName;
    }

    public Result execute() {
        // how do we choose the datasource?
        // how do we get the sql?
        DataSource ds = null;
        try {
            Context ctxt = new InitialContext();
            Context envCtxt = (Context) ctxt.lookup("java:comp/env");
            ds = (DataSource) envCtxt.lookup(dsName);
        } catch(NamingException ne) {
            ne.printStackTrace();
        }
        try {
            QueryRunner runner = new QueryRunner(ds);

            Param[] params = getParams();
            Object[] values = new Object[params.length];
            for(int i=0; i<params.length; i++) {
                values[i] = params[i].getValue();
            }

            List list = (List) runner.query(this.sql, values, new ArrayListHandler() );

            // TODO: if getColumns().length == 0
            // should goto the ResultSetMetaData and get the column names
            // this means not using the DBUtils code above

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
            sqle.printStackTrace();
        }
        return null;
    }

    // returns a Choice[]
    public Choice[] getParamChoices(Param param) {
        String binding = param.getBinding();

        if(binding == null) {
            return null;
        }

        // assume binding is an SQL statement that returns 
        // 2 columns. The first is key, the second is value.
        
        DataSource ds = null;
        try {
            Context ctxt = new InitialContext();
            Context envCtxt = (Context) ctxt.lookup("java:comp/env");
            ds = (DataSource) envCtxt.lookup(dsName);
        } catch(NamingException ne) {
            ne.printStackTrace();
        }
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

}
