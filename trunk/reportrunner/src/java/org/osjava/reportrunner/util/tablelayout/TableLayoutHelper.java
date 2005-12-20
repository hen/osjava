package org.osjava.reportrunner.util.tablelayout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JLabel;

public class TableLayoutHelper {
	
	private Collection tableRows;
	private Container container;
	private double[][] size;
	private double borderWidth;
	private double rowHeight;
    private double[] colWidths;
	
	public TableLayoutHelper(Container container) {
		this.tableRows = new LinkedList();
		this.container = container;
		this.size = new double[2][];                   
	}
	
	public void setRowHeight(double rowHeight) {
		this.rowHeight = rowHeight;
	}
	
	public void setColWidths(double[] colWidths) {
		this.colWidths = colWidths;
	}
	
	public void setBorderWidth(double borderWidth) {
		this.borderWidth = borderWidth;
	}
	
	public void addRow(TableRow row) {
		this.tableRows.add(row);
	}
	
	public void addRowsToContainer() {              
		if (this.borderWidth > 0) {
			this.size[0] = new double[2 + colWidths.length];
			this.size[0][0] = borderWidth;
			for (int i = 0; i < colWidths.length; i++) {
				this.size[0][i + 1] = colWidths[i];
			}
			this.size[0][colWidths.length + 1] = borderWidth;
			
            int rowCount = this.getNumberOfRows();
			this.size[1] = new double[2 + rowCount];
			this.size[1][0] = this.borderWidth;           
			for (int i = 0; i < rowCount; i++) {
				this.size[1][i + 1] = this.rowHeight;
			}
			this.size[1][rowCount + 1] = this.borderWidth;            
		} else {
			this.size[0] = new double[colWidths.length];
			for (int i = 0; i < colWidths.length; i++) {
				this.size[0][i] = colWidths[i];
			}
			
			int rowCount = this.getNumberOfRows();
            this.size[1] = new double[rowCount];
			for (int i = 0; i < rowCount; i++) {
				this.size[1][i] = this.rowHeight;
			}
		}                
		
		this.container.setLayout(new TableLayout(this.size));
		
		int rowNumber = 0;
		if (this.borderWidth > 0) {
			rowNumber++;
		}
		
		Iterator i = tableRows.iterator();
		while (i.hasNext()) {
			TableRow row = (TableRow) i.next();
			int count = row.getItemCount();
			int startRow = rowNumber;
			int endRow = rowNumber + (row.getRowSpan() - 1);
			
			for (int j = 0; j < count; j++) {
				Component c = row.getItem(j);               
				Dimension d = row.getStartAndEndCols(j);
				int startCol = d.width;
				int endCol = d.height;     
				
				if (this.borderWidth > 0) {
					startCol++;
					endCol++;
				}
                               
				StringBuffer constraint = new StringBuffer();
				constraint.append(startCol).append(',').append(startRow).append(',').append(endCol).append(',').append(endRow);                
				container.add(c, constraint.toString());				
			}
			rowNumber = endRow + 1;
		}        
	}
    
    public void addRow(int rowSpan, Object[] rowData) {
    	int startCol = 0;
        TableRow row = new TableRow(rowSpan);
        for (int i = 0; i < rowData.length; i++) {            
    		if (rowData[i] instanceof Component) {                
    			row.addItem((Component) rowData[i], startCol);
                startCol++;
            } else if (rowData[i] instanceof Object[]) {                
            	Component c = (Component) ((Object[]) rowData[i])[0];
                int colSpan = ((Integer) ((Object[]) rowData[i])[1]).intValue();
                int endCol = startCol + colSpan - 1;
                row.addItem(c, startCol, endCol);
                startCol = endCol + 1;
            } else {
                startCol++;            	
            }
        }
        this.addRow(row);
    }
    
    public void addRow(Object[] rowData) {
    	this.addRow(1, rowData);
    }
    
    public void addRow() {
    	TableRow row = new TableRow();
        row.addItem(new JLabel(""), 0);
        this.addRow(row);
    }
    
    private int getNumberOfRows() {
    	int rowCount = 0;
        Iterator i = this.tableRows.iterator();
        while (i.hasNext()) {
        	TableRow row = (TableRow) i.next();
            rowCount += row.getRowSpan();
        }
        return rowCount;                
    }
	
}
