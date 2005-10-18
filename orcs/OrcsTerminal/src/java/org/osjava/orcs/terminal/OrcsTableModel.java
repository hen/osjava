/*
 * Created on Oct 13, 2005
 */
package org.osjava.orcs.terminal;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * @author hyandell
 */
public class OrcsTableModel extends AbstractTableModel {
    
    private String[] headers = new String[0];
    private List data = new ArrayList();
    
    public void setData(String[] headers, List data) {
        this.headers = headers;
        this.data = data;
        fireTableStructureChanged();
        fireTableDataChanged();
    }

    public int getColumnCount() {
        return headers.length;
    }

    public int getRowCount() {
        return data.size();
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public Object getValueAt(int row, int column) {
        // first column in each row is the orcs_row_id
        return ((Object[])data.get(row))[column + 1];
    }

    public void setValueAt(Object obj, int row, int column) {
        ((Object[])data.get(row))[column] = obj;
        fireTableCellUpdated(row, column);
    }

    public String getColumnName(int column) {
        return headers[column];
    }
    
    Object getOrcsRowId(int row) {
        return ((Object[])data.get(row))[0];
    }

}
