package com.generationjava.swing;

import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.Component;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;
import javax.swing.JPopupMenu;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.HashMap;
import org.apache.commons.collections.MultiHashMap;
import javax.swing.table.TableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.JTableHeader;

// tmp
import java.util.ArrayList;
import java.util.List;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.DefaultCellEditor;

public class GJTable extends JTable implements MouseListener, ActionListener {

    static public void main(String[] strs) {
        JFrame frame = new JFrame("Test");
        final GJTable table = demoTable();
        table.setEditable(true);
        TableCellAutoSelector tcas = new TableCellAutoSelector();
        tcas.actOn(table);
        JScrollPane pane = new JScrollPane(table);
        frame.getContentPane().add(pane);
        frame.pack();
        frame.show();
    }

    static public String HIDDEN_TAG = "_";
    
    static public GJTable demoTable() {
        ArrayList list = new ArrayList();
        list.add("_id");
        list.add("name");
        list.add("place");
        list.add("age");
        list.add("small-place-there"); //-over-yonder");
        MultiHashMap map = new MultiHashMap();
        map.put("_id","1");
        map.put("_id","2");
        map.put("_id","3");
        map.put("_id","4");
        map.put("name","fred");
        map.put("name","jim");
        map.put("name","jim");
        map.put("name","bob");
        map.put("place","US");
        map.put("place","UK");
        map.put("place","NO");
        map.put("place","HU");
        map.put("age","10");
        map.put("age","20");
        map.put("age","20");
        map.put("age","30");
        map.put("small-place-there","30");
        map.put("small-place-there","30");
        map.put("small-place-there","30");
        map.put("small-place-there","30");
        GJTableModel model = new GJTableModel(list,map);
        GJTable table = new GJTable(model, list);
        return table;
    }

    private JPopupMenu popup;
    private JPopupMenu hidePopup;
    private List headers;

    // row clicked on to clone.
    // IS THIS EVEN NEEDED? The rows get Selected!
    private int lastRow;

    // keep a record of the widths before they were hidden.
    private HashMap columnWidths = new HashMap();

    public GJTable() {
        this(new ArrayList(), new MultiHashMap());
    }
    public GJTable(List headers, MultiHashMap map) {
        this(new GJTableModel(headers, map), headers);
    }
    public GJTable(GJTableModel model, List headers) {
        super(model);
        hideColumns(headers);

        /*
        // make headers size to fit
        // THIS IS QUITE A HACK. HAVE TO SEE IF IT WORKS WHEN 
        // DEPLOYED INSIDE A JSCROLLPANE. IF WIDTH EXCEEDS THAT 
        // OF THE CONTAINER, IE APPLET, THEN NPExceptions.
        Enumeration enum = getColumnModel().getColumns();
        while(enum.hasMoreElements()) {
            TableColumn column = (TableColumn)enum.nextElement();
            String name = column.getHeaderValue().toString();
            JLabel label = new JLabel(name);
            int width = label.getPreferredSize().width;
            int oldwidth = column.getPreferredWidth();
            if(width > oldwidth) {
                System.err.print("Width was: "+column.getPreferredWidth());
                column.setPreferredWidth(width);
                System.err.println(" - Setting width to : "+width);
            }
        }
        */

        // add single click column sorting
        getTableHeader().addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent me) {
                if(me.isPopupTrigger()) {
                    return;
                }
                int col = columnAtPoint(me.getPoint());
                ((GJTableModel)getModel()).sortBy(col);
            }
            public void mouseReleased(MouseEvent me) {
            }
            public void mouseEntered(MouseEvent me) {
            }
            public void mouseExited(MouseEvent me) {
            }
            public void mousePressed(MouseEvent me) {
                if( (me.isPopupTrigger()) ||
                    ( (me.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
                  )
                {
                    popup.show(me.getComponent(), me.getX(), me.getY());
                }
            }
        });
        addMouseListener(this);
        popup = new JPopupMenu("Modify Table");
        getColumnModel().addColumnModelListener(
            new TableColumnModelListener() {
                public void columnAdded(TableColumnModelEvent tcme) {
                }
                public void columnMarginChanged(ChangeEvent ce) {
                }
                public void columnRemoved(TableColumnModelEvent tcme) {
                }
                public void columnSelectionChanged(ListSelectionEvent lse) {
                }
                public void columnMoved(TableColumnModelEvent tcme) {
                    int idx = tcme.getFromIndex();
                    Component comp = GJTable.this.popup.getComponent(idx);
                    GJTable.this.popup.remove(idx);
                    GJTable.this.popup.insert(comp, tcme.getToIndex());
                }

            }
        );

        Iterator iterator = headers.iterator();
        while(iterator.hasNext()) {
            String header = (String)iterator.next();
            boolean checked = !header.startsWith(HIDDEN_TAG);
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(header,checked);
            popup.add(item);
            item.addActionListener(this);
        }
        popup.setInvoker(this);

        hidePopup = new JPopupMenu("Hide Column");
        {
            JMenuItem item = new JMenuItem("New");
            item.addActionListener(this);
            hidePopup.add( item );

            item = new JMenuItem("Clone");
            item.addActionListener(this);
            hidePopup.add( item );

            item = new JMenuItem("Remove");
            item.addActionListener(this);
            hidePopup.add( item );

            hidePopup.setInvoker(this);
        }
    }

    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    // need to update the popup
    public void updateModel(List headers, MultiHashMap contents) {
        ((GJTableModel)getModel()).updateModel(headers, contents);
        popup.removeAll();
        Iterator iterator = headers.iterator();
        while(iterator.hasNext()) {
            String header = (String)iterator.next();
            boolean checked = !header.startsWith(HIDDEN_TAG);
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(header,checked);
            popup.add(item);
            item.addActionListener(this);
        }
        hideColumns(headers);
    }
    public void setEditable(boolean bool) {
            ((GJTableModel)getModel()).setEditable(bool);
    }

    private void hideColumns(List columns) {
        if(columns == null) {
            return;
        }

        Iterator iterator = columns.iterator();
        while(iterator.hasNext()) {
            String name = (String)iterator.next();
            if(!name.startsWith(HIDDEN_TAG)) {
                continue;
            }
            TableColumn column = getColumn(name);
            if(column != null) {
                hideColumn(name, column);
            }
        }
    }
    
    private void hideColumn(String name, TableColumn column) {
        int[] wid = new int[3];
        wid[0] = column.getMinWidth();
        wid[1] = column.getMaxWidth();
        wid[2] = column.getPreferredWidth();
        columnWidths.put(name,wid);
        column.setMinWidth(0);
        column.setMaxWidth(0);
        column.setPreferredWidth(0);
    }

    // ActionListener interface
    public void actionPerformed(ActionEvent ae) {
        JMenuItem item = (JMenuItem)ae.getSource();
        String name = item.getLabel();
        if(item instanceof JCheckBoxMenuItem) {
            TableColumn column = getColumn(name);
            if(column.getWidth() == 0) {
                int[] wid = (int[])columnWidths.get(name);
                column.setMinWidth(wid[0]);
                column.setMaxWidth(wid[1]);
                column.setPreferredWidth(wid[2]);
                columnWidths.remove(name);
            } else {
                hideColumn(name, column);
            }
        } else {
            GJTableModel model = (GJTableModel)getModel();
            int idx = model.getRowCount();
            if("New".equals(name)) {
                model.newRow();
                editCellAt(idx,0);
                changeSelection(idx,0,false,false);
            } else
            if("Clone".equals(name)) {
                model.cloneRow(lastRow);
                editCellAt(idx,0);
                changeSelection(idx,0,false,false);
            } else
            if("Remove".equals(name)) {
                int count = getSelectedRowCount();
                if(count > 0) {
                    // remove other selected rows
                    int[] rows = getSelectedRows();
                    // go from top so we don't change the indexes
                    for(int i=count-1; i>=0; i--) {
                        model.removeRow(rows[i]);
                    }
                }
//                model.removeRow(lastRow);
                lastRow -= count;
                if(lastRow < 0) {
                    lastRow = 0;
                }
                editCellAt(lastRow,0);
                changeSelection(idx,0,false,false);
            }

        }
    }
    // End of ActionListener interface

    // MouseListener interface
    public void mousePressed(MouseEvent me) {
        if( (me.isPopupTrigger()) ||
            ( (me.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
          )
        {
            lastRow = rowAtPoint(me.getPoint());
            hidePopup.show(me.getComponent(), me.getX(), me.getY());
        }
    }
    public void mouseReleased(MouseEvent me) {
    }
    public void mouseEntered(MouseEvent me) {
    }
    public void mouseExited(MouseEvent me) {
    }
    public void mouseClicked(MouseEvent me) {
    }
    // End of MouseListener interface

}
