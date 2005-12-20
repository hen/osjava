package org.osjava.reportrunner.util.tablelayout;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class TableRow {

    private List items;
    private List startAndEndCols;
    private int rowSpan;
    
    TableRow() {
        items = new ArrayList(4);
        startAndEndCols = new ArrayList(4);
        this.rowSpan = 1;
    }
        
    TableRow(int rowSpan) {
    	this.items = new ArrayList(4);
        this.startAndEndCols = new ArrayList(4);
        this.rowSpan = rowSpan;
    }
        
    void addItem(Component c, int startCol, int endCol) {    	
        Dimension d = new Dimension(startCol, endCol);        
        this.items.add(c);
        this.startAndEndCols.add(d);
    }
    
    void addItem(Component c, int col) {
    	this.addItem(c, col, col);
    }
    
    int getItemCount() {
    	return this.items.size();
    }
    
    int getRowSpan() {
    	return this.rowSpan;
    }
    
    Component getItem(int i) {
    	return (Component) this.items.get(i);
    }
    
    Dimension getStartAndEndCols(int i) {
    	return (Dimension) this.startAndEndCols.get(i);
    }
        
}
