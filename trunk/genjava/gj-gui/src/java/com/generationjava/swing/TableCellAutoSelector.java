package com.generationjava.swing;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.*;
import java.awt.event.*;
import java.awt.Component;

public class TableCellAutoSelector extends DefaultTableCellRenderer implements KeyListener {

    private JTable table;
    private Object value;
    private int row;
    private int column;
    private boolean isSelected;

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
        if(hasFocus) { 
            this.value = value;
            this.isSelected = isSelected;
            this.row = row;
            this.column = column;
            TableCellEditor editor = table.getCellEditor(row, column);
            if(editor != null) {
                Component c = editor.getTableCellEditorComponent(table, value, isSelected, row, column);
                if(c instanceof JTextComponent) {
                    JTextComponent jtf = (JTextComponent)c;
                    jtf.selectAll();
                    jtf.requestFocus();
                }
                return c;
            }
        }

        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

    public void actOn(JTable table) {
        table.setDefaultRenderer(Object.class, this);
        table.addKeyListener(this);
        this.table = table;
    }
    public void keyReleased(KeyEvent event) {
    }
    public void keyPressed(KeyEvent event) {
        int code = event.getKeyCode();

        if(event.isControlDown() ) {
            if( (code != KeyEvent.VK_V) &&
                (code != KeyEvent.VK_X) &&
                (code != KeyEvent.VK_C) 
              )
            {
                return;
            } else {
                // do copy/paste/cut
                TableCellEditor editor = this.table.getCellEditor(row, column);
                Component c = editor.getTableCellEditorComponent(this.table, this.value, this.isSelected, this.row, this.column);
                if(c instanceof JTextComponent) {
                    JTextComponent jtf = (JTextComponent)c;
                    if(code == KeyEvent.VK_V) {
                        jtf.setText("");
                        jtf.paste();
                        String txt = jtf.getText();
                        event.consume();  // we did the paste

                        this.table.setValueAt(txt, this.row, this.column);


                        // get selected columns and set to the paste.
                        int[] rows = this.table.getSelectedRows();
                        int size = rows.length;
                        if(size != 1) {
                            for(int i=0; i<size; i++) {
                                if(rows[i] == this.row) {
                                    continue;
                                }
                                editor = this.table.getCellEditor(rows[i], this.column);
                                Object subValue = table.getValueAt(rows[i], this.column);
                                c = editor.getTableCellEditorComponent(this.table, subValue, true, rows[i], this.column);
                                if(c instanceof JTextComponent) {
                                    jtf = (JTextComponent)c;
                                    jtf.setText(txt);
                                    this.table.setValueAt(txt, rows[i], this.column);
                                }
                            }
                        }
                    } else
                    if(code == KeyEvent.VK_X) {
                        jtf.selectAll();
                        jtf.cut();
                        this.table.setValueAt("", this.row, this.column);
                        event.consume();  // we did the cut
                    } else
                    if(code == KeyEvent.VK_C) {
                        jtf.selectAll();
                        jtf.copy();
                        event.consume();  // we did the copy
                    }
                }
                TableModel model = this.table.getModel();
                if(model instanceof AbstractTableModel) {
                    ((AbstractTableModel)model).fireTableDataChanged();
                }
                return;
            }
        }
    }

    public void keyTyped(KeyEvent event) {
        // Note, 'keyCode' is useless in keyTyped methods.
        int chr = event.getKeyChar();

        if(event.isControlDown() ) {
            return;
        }
        if(chr == '\t') {
            return;
        }

        if(event.isMetaDown() ||
           event.isAltDown()
          )
        {
            // don't interfere with special stuff
            return;
        }

        TableCellEditor editor = this.table.getCellEditor(row, column);
        Component c = editor.getTableCellEditorComponent(this.table, this.value, this.isSelected, this.row, this.column);
        if(c instanceof JTextComponent) {
            JTextComponent jtf = (JTextComponent)c;
            jtf.setText(String.valueOf(event.getKeyChar()));
            jtf.requestFocus();
            event.consume();  // we dealt with the typing
        }
    }

}
