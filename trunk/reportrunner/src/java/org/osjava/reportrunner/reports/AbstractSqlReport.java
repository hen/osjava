package org.osjava.reportrunner.reports;

import org.apache.commons.lang.*;
import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.*;
import org.osjava.reportrunner.*;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.*;
import javax.sql.DataSource;
import javax.naming.*;

public abstract class AbstractSqlReport extends AbstractReport {

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

    public final Result execute() throws ReportException {
        ReportGroup group = super.getReportGroup();
        Resource resource = group.getResource(this.dsName);
        DataSource ds = (DataSource) resource.accessResource();

        // build sql from variants
        Variant[] variants = getVariants();
        for(int i=0; i<variants.length; i++) {
            String snippet = variants[i].getSelected().getValue();
            this.sql = StringUtils.replace( this.sql, "?"+variants[i].getName(), snippet);
        }

        Object[] array = executeSql(ds);

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

        array = hackOracleTimestamp(array);

        return postExecute( new ArrayResult( getColumns(), array ) );
    }

    protected Result postExecute(Result result) throws ReportException {
        return result;
    }

    protected abstract Object[] executeSql(DataSource ds) throws ReportException;

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

    private static Method method = null;
    private static Object[] hackOracleTimestamp(Object[] array) throws ReportException {
        Object[] firstRow = ((Object[]) array[0]);
        int sz = firstRow.length;
        boolean[] columns = new boolean[sz];
        boolean found = false;
        for(int i=0; i<sz; i++) {
            if("oracle.sql.TIMESTAMP".equals(firstRow[i].getClass().getName())) {
                if(method == null) {
                    try {
                        method = firstRow[i].getClass().getMethod("timestampValue", new Class[0] );
                    } catch(NoSuchMethodException nsme) {
                        throw new ReportException("Unable to convert Oracle timestamp", nsme);
                    }
                }
                columns[i] = true;
                found = true;
            }
        }
        if(found) {
            int len = array.length;
            for(int i = 0; i < len; i++) {
                for(int j = 0; j < sz; j++) {
                    if(columns[j]) {
                        try {
                            ((Object[]) array[i])[j] = method.invoke( ((Object[]) array[i])[j], new String[0] );
                        } catch(IllegalAccessException iae) {
                            throw new ReportException("Unable to convert Oracle timestamp", iae);
                        } catch(IllegalArgumentException iae) {
                            throw new ReportException("Unable to convert Oracle timestamp", iae);
                        } catch(InvocationTargetException ite) {
                            throw new ReportException("Unable to convert Oracle timestamp", ite);
                        }
                    }
                }
            }
        }

        return array;
    }
}
