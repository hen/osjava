package org.osjava.reportrunner.reports;

import org.apache.commons.lang.*;
import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.*;
import org.osjava.reportrunner.*;

import java.sql.*;
import java.util.*;
import javax.sql.DataSource;
import javax.naming.*;

public class NamedSqlReport extends AbstractSqlReport {

    protected String variableToNamed(String variable) {
        return ":"+variable;
    }

    private static int[] indexesOf(String target, String search) {
        int[] ret = new int[StringUtils.countMatches(target, search)];

        int i=0;
        int idx = -1;
        while( (idx = target.indexOf(search)) != -1 ) {
            ret[i] = idx;
            i++;
        }

        return ret;
    }

    protected Object[] executeSql(DataSource ds) throws ReportException {
        try {
            Param[] params = getParams();
            ArrayList values = new ArrayList();
            String sql = getSql();
            Map idxsMap = new HashMap();
            // TODO: Remove this when report's offer a param key inteface
            Map paramsMap = new HashMap();

            // Step 1: Find the index order of each named parameter
            for(int i=0; i<params.length; i++) {

                String named = variableToNamed(params[i].getName());
                int[] idxs = indexesOf( sql, named );
                for(int j=0; j<idxs.length; j++) {
                    idxsMap.put( new Integer(j), named );
                }
                paramsMap.put(named, params[i]);

            }

            // Step 2: Order these 
            List list = new ArrayList();
            list.addAll( idxsMap.keySet() );
            Collections.sort(list);

            Map markMap = new HashMap();

            // Step 3: Walk backwards through these, storing up the values to be used later 
            //         and the question marks to put in the sql in place of the named params
            for(int j = list.size() - 1; j >= 0; j--) {
                String named = (String) idxsMap.get( list.get(j) );
                Param param = (Param) paramsMap.get( named );

                if(Object[].class.isAssignableFrom(param.getType())) {
                    Object value = param.getValue();
                    Object[] array = null;
                    if(value instanceof Object[]) {
                        array = (Object[]) param.getValue();
                    } else {
                        array = new Object[] { value };
                    }
                    String marks = StringUtils.chomp(StringUtils.repeat("?,", array.length), ",");
                    markMap.put(named, marks);
                    for(int k=0; k<array.length; k++) {
                        values.add( k, array[k] );
                    }
                } else {
                    values.add( 0, params[j].getValue() );
                    markMap.put(named, "?");
                }
            }

            // Step 4: Convert the sql from named paramters to question mark parameters
            Collection keys = markMap.keySet();
            Iterator iterator = keys.iterator();
            while(iterator.hasNext()) {
                String named = (String) iterator.next();
                String mark = (String) markMap.get( named );
                sql = StringUtils.replace( sql, named, mark);
            }

            QueryRunner runner = new QueryRunner(ds);

            ResultSetHandler handler = new ReportArrayListHandler(this);

            List retList = (List) runner.query(sql, values.toArray(), handler);

            return retList.toArray(new Object[0]);
        } catch(SQLException sqle) {
            throw new ReportException("Unable to run SQL report", sqle);
        }
    }

}
