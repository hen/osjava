/*
 * Created on Oct 11, 2005
 */
package org.osjava.orcs.terminal;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * For GJ-GUI at some point
 * @author hyandell
 */
public class GuiHelper {

    public static JSplitPane verticalSplit(JComponent jc1, JComponent jc2) {
        JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        jsp.add(jc1);
        jsp.add(jc2);
        return jsp;
    }
        
    public static JSplitPane verticalSplit(JComponent jc1, JComponent jc2, int weight) {
        JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        jsp.setResizeWeight(weight/100);
        jsp.add(jc1);
        jsp.add(jc2);
        return jsp;
    }

    public static JSplitPane horizontalSplit(JComponent jc1, JComponent jc2) {
        JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        jsp.add(jc1);
        jsp.add(jc2);
        return jsp;
    }
    public static JSplitPane horizontalSplit(JComponent jc1, JComponent jc2, int weight) {
        JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        jsp.setResizeWeight(weight/100);
        jsp.add(jc1);
        jsp.add(jc2);
        return jsp;
    }

    // taken from: http://www.chka.de/swing/table/cell-sizes.html
    public static void calcColumnWidths(JTable table)
    {
        JTableHeader header = table.getTableHeader();

        TableCellRenderer defaultHeaderRenderer = null;

        if (header != null)
            defaultHeaderRenderer = header.getDefaultRenderer();

        TableColumnModel columns = table.getColumnModel();
        TableModel data = table.getModel();

        int margin = columns.getColumnMargin(); 
        
        // HACK BY HEN
        margin += 10;

        int rowCount = data.getRowCount();

        int totalWidth = 0;

        for (int i = columns.getColumnCount() - 1; i >= 0; --i)
        {
            TableColumn column = columns.getColumn(i);
                
            int columnIndex = column.getModelIndex();
                
            int width = -1; 

            TableCellRenderer h = column.getHeaderRenderer();
              
            if (h == null)
                h = defaultHeaderRenderer;
                
            if (h != null) // Not explicitly impossible
            {
                Component c = h.getTableCellRendererComponent
                       (table, column.getHeaderValue(),
                        false, false, -1, i);
                        
                width = c.getPreferredSize().width;
            }
           
            for (int row = rowCount - 1; row >= 0; --row)
            {
                TableCellRenderer r = table.getCellRenderer(row, i);
                     
                Component c = r.getTableCellRendererComponent
                   (table,
                    data.getValueAt(row, columnIndex),
                    false, false, row, i);
            
                    width = Math.max(width, c.getPreferredSize().width);
            }

            if (width >= 0) {
                column.setPreferredWidth(width + margin); 
            }
                
            totalWidth += column.getPreferredWidth();
        }

    }

}
