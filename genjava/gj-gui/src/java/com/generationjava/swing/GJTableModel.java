package com.generationjava.swing;

import javax.swing.table.AbstractTableModel;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Comparator;
import com.generationjava.compare.BeanComparator;
import com.generationjava.compare.NumericStringComparator;
import org.apache.commons.collections.comparators.ReverseComparator;

import org.apache.commons.collections.MultiHashMap;

public class GJTableModel extends AbstractTableModel {

    // contains the name of the headers. doesn't change order
    private List headers;

    // contains the list of rows. changes order on sorting
    private List rows;

    // visual order of columns
    private int[] headerToColumn;

    // Remember the last column to be sorted, so next sort can 
    // reverse.
    private int lastColumn = -1;

    // is this an editable model
    private boolean editable;

    // an empty row. this is cloned when a new row is made
    private ArrayList emptyRow;

    // Used for submitting
    private HashSet deletedRows = new HashSet();
    private HashSet newRows = new HashSet();
    private HashSet modifiedRows = new HashSet();

    // list of sort-listeners
    private List sortListeners;

    // a listener to just the table
    private List tableListeners;

    // index used in sorting when there's a listener
    private ArrayList sortIndex = new ArrayList();

    public GJTableModel(List headers, MultiHashMap map) {
        super();
        updateModel(headers,map);
    }

    public Iterator[] getChanges() {
// no longer necessary....
//        modifiedRows.removeAll(newRows);
//        modifiedRows.removeAll(deletedRows);
        return new Iterator[] { newRows.iterator(), 
                                deletedRows.iterator(), 
                                modifiedRows.iterator() 
                              };
    }

    public void clearChanges() {
        this.newRows.clear();
        this.modifiedRows.clear();
        this.deletedRows.clear();
    }

    public Iterator iterateHeaders() {
        return this.headers.iterator();
    }

    public void addSortListener(GJTableSortListener listener) {
        this.sortListeners.add(listener);
    }

    public void addTableListener(GJTableListener listener) {
        this.tableListeners.add(listener);
    }

    public void updateModel(List headers, MultiHashMap map) {
        this.headers = headers;
        this.rows = new ArrayList();
        this.emptyRow = new ArrayList();

        int width = headers.size();

        // setup the header to column 'mapping'.
        // also the visible columns
        this.headerToColumn = new int[width];
        for(int i=0;i<width;i++) {
            headerToColumn[i] = i;
            emptyRow.add("");
        }

        // setup listeners
        this.tableListeners = new LinkedList();
        this.sortListeners = new LinkedList();

        // setup the rows
        Iterator iterator = headers.iterator();
        if(iterator.hasNext()) {
            Object key = iterator.next();
            List list = (List)map.get(key);
            Iterator it2 = list.iterator();
            while(it2.hasNext()) {
                ArrayList sublist = new ArrayList(headers.size());
                sublist.add(it2.next());
                rows.add(sublist);
            }
        }
        while(iterator.hasNext()) {
            Object key = iterator.next();
            List list = (List)map.get(key);
            Iterator it2 = list.iterator();
            int i=0;
            while(it2.hasNext()) {
                ArrayList sublist = (ArrayList)rows.get(i);
                sublist.add(it2.next());
                i++;
            }
        }
        fireTableStructureChanged();
    }

    // get from column y, and row x
    public Object getValueAt(int x, int y) {
        return ((List)rows.get(x)).get(y);
    }

    public void setValueAt(Object value, int x, int y) {
        ((List)rows.get(x)).set(y, value);
        Object obj = rows.get(x);
        if(!contains(newRows,obj)) {
            modifiedRows.add(rows.get(x));
        }
    }

    public int getRowCount() {
        return rows.size();
    }
    
    public int getColumnCount() {
        return headers.size();
    }

    public String getColumnName(int column) {
        return (String)headers.get(column);
    }

    public boolean isCellEditable(int x, int y) {
        return this.editable;
    }

    public void setEditable(boolean bool) {
        this.editable = bool;
    }

    public void sortBy(int column) {
        if(!this.sortListeners.isEmpty()) {
            // need to inform of all changes. HOW?!?
            // maybe run an initial sort. then after the real sort, 
            // inform of the results of the initial sort?
            // or even move rows dependant on initial sort?

            // ensure sortIndex matches the size
            int size = this.rows.size();
            if(this.sortIndex.size() < size) {
//                System.err.println("INcreasing size: "+this.sortIndex.size()+"->"+size);
                for(int i=this.sortIndex.size(); i<size; i++) {
//                    System.err.print("*");
                    this.sortIndex.add( new GJTableSortIndex() );
                }
//                System.err.println("");
            } else
            if(this.sortIndex.size() > size) {
//                System.err.println("Decreasing size: "+this.sortIndex.size()+"->"+size);
                for(int i=this.sortIndex.size(); i>=size; i--) {
//                    System.err.print("*");
                    this.sortIndex.remove(i);
                }
//                System.err.println("");
            }

            // sortIndex is now the same size as this.rows, copy data in.
            for(int i=0; i<size; i++) {
                GJTableSortIndex index = (GJTableSortIndex)this.sortIndex.get(i);
                index.setIndex(i);
                index.setData(((List)this.rows.get(i)).get(column));
            }

            // important to be same as sort below... extract-method
            Comparator cmp = BeanComparator.getInstance("data");
            ((BeanComparator)cmp).setComparator(new NumericStringComparator());
            if(column == lastColumn) {
                cmp = new ReverseComparator(cmp);
            }

            Collections.sort(sortIndex, cmp);
//            System.err.println("SORTED: "+sortIndex);
        }
        Comparator cmp = BeanComparator.getInstance("["+column+"]");
        ((BeanComparator)cmp).setComparator(new NumericStringComparator());
        if(column == lastColumn) {
            cmp = new ReverseComparator(cmp);
            lastColumn = -1;
        } else {
            lastColumn = column;
        }
//        for(int i=0;i<rows.size();i++) {
//            System.err.println("SORTING: "+((List)rows.get(i)).get(column));
//        }
        Collections.sort(rows, cmp);

        if(!this.sortListeners.isEmpty()) {
            // inform of sortIndex's nature
            GJTableEvent event = new GJTableEvent();
            event.setNewOrder(this.sortIndex);
            Iterator iterator = this.sortListeners.iterator();
            while(iterator.hasNext()) {
                GJTableSortListener listener = (GJTableSortListener)iterator.next();
                listener.rowsMoved(event);
            }
        }

        fireTableDataChanged();
    }

    public void newRow() {
        int idx = rows.size();
        ArrayList sublist = new ArrayList(emptyRow);
        rows.add(sublist);
        newRows.add(sublist);
        fireTableRowsInserted(idx,idx);
    }

    public void cloneRow(int row) {
        int idx = rows.size();
        ArrayList sublist = new ArrayList( (List)rows.get(row) );
        rows.add(sublist);
        newRows.add(sublist);
        fireTableRowsInserted(idx,idx);
    }

    public void removeRow(int row) {
        Object obj = rows.get(row);
        if(contains(newRows,obj)) {
            newRows.remove(rows.get(row));
        } else {
            deletedRows.add(rows.get(row));
        }
        rows.remove(row);
        if(!this.tableListeners.isEmpty()) {
            GJTableEvent event = new GJTableEvent();
            event.setRemoved(row);
            Iterator iterator = this.tableListeners.iterator();
            while(iterator.hasNext()) {
                GJTableListener listener = (GJTableListener)iterator.next();
                listener.rowRemoved(event);
            }
        }
        fireTableRowsDeleted(row, row);
    }

    static private boolean contains(Collection collection, Object obj) {
        Iterator iterator = collection.iterator();
        while(iterator.hasNext()) {
            Object row = iterator.next();
            if(row == obj) {
                return true;
            }
        }
        return false;
    }

}
