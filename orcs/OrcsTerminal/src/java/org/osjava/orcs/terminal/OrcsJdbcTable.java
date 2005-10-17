/*
 * Created on Oct 14, 2005
 */
package org.osjava.orcs.terminal;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.dbutils.BasicRowProcessor;

/**
 * TODO: Hook this class up. 
 * 
 * @author hyandell
 */
public class OrcsJdbcTable {

    private List list = new LinkedList();
    private String[] headers;
    private boolean[] hiddenColumns;
    
    public OrcsJdbcTable(ResultSet rs) throws SQLException {
        BasicRowProcessor brp = BasicRowProcessor.instance();
        ResultSetMetaData rsmd = rs.getMetaData();
        while(rs.next()) {
            Object[] array = brp.toArray(rs);
            
            if(headers == null) {
                headers = new String[rsmd.getColumnCount()];
                for(int i=0; i<array.length; i++) {
                    headers[i] = rsmd.getColumnName(i + 1);
                }
            }
            
            for(int i=0; i<array.length; i++) {
                list.add( new Object[] { array[i ]} );
            }
        }
    }
    
    public OrcsJdbcTable(List list, String[] headers) {
        this.list = list;
        this.headers = headers;
    }
    
    public void setHiddenColumn(int idx) {
        if(this.hiddenColumns == null) {
            this.hiddenColumns = new boolean[this.headers.length];
        }
        this.hiddenColumns[idx] = true;
    }
    
    public boolean isHidden(int idx) {
        return this.hiddenColumns[idx];
    }
    
}
