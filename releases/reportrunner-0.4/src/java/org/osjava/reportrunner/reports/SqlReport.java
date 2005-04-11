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

}
