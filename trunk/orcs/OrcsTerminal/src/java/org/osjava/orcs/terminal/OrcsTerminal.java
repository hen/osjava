/*
 * Created on Oct 11, 2005
 */
package org.osjava.orcs.terminal;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.osjava.dswiz.DataSourceWizard;

import com.generationjava.awt.WindowUtilities;
import com.generationjava.io.CsvWriter;

import net.sf.nachocalendar.CalendarFactory;
import net.sf.nachocalendar.components.DateField;

/**
 * @author hyandell
 */
public class OrcsTerminal extends JFrame {
    
    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException("Unable to set system look & feel");
        }
        
        JFrame frame = new JFrame("DataSourceWizard Example");
        DataSourceWizard dsw = new DataSourceWizard(frame);
        WindowUtilities.centreWindowOnScreen(dsw);
        dsw.show();
        DataSource ds = dsw.getDataSource();
        frame.dispose();
        
        OrcsProcedures.setDataSource(ds);
        
        OrcsTerminal ot = new OrcsTerminal();
        WindowUtilities.centreWindowOnScreen(ot);
        ot.show();
    }
    
    // widgets that need to be seen by anonymous listeners
    private JList tableList; 
    private JTable revisionTable;
    private JTable dataTable;
    private JTable rowDataTable;
    
    // the current state of the application
    private String currentSchema;
    private String currentTable;
    private Object currentRevision;
    private Date currentDate = new Date();
    private Object currentOrcsRowId;
    private boolean currentDeleted;
    
    public OrcsTerminal() {
        super("Orcs Terminal");
        
        JPanel schemaTablePanel = new JPanel();
        schemaTablePanel.setLayout(new BorderLayout());
        schemaTablePanel.add( createSchemaPanel(), BorderLayout.NORTH);
        schemaTablePanel.add( createTablePanel(), BorderLayout.CENTER);

        JPanel commandDataPanel = new JPanel();
        commandDataPanel.setLayout(new BorderLayout());
        commandDataPanel.add( createCommandPanel(), BorderLayout.NORTH);
        commandDataPanel.add( createDataPanel(), BorderLayout.CENTER);
        commandDataPanel.add( createActionPanel(), BorderLayout.SOUTH);

        JPanel rowDataPanel = new JPanel();
        rowDataPanel.setLayout(new BorderLayout());
        rowDataPanel.add( createRowDataPanel(), BorderLayout.CENTER);
        rowDataPanel.add( createRollbackPanel(), BorderLayout.SOUTH);
        
        getContentPane().add(
          GuiHelper.horizontalSplit(
              GuiHelper.horizontalSplit(
                  schemaTablePanel,
                  commandDataPanel,
                  0
              ),
              GuiHelper.horizontalSplit(
                  createRevisionPanel(),
                  rowDataPanel,
                  100
              ),
             100
          )
        );
        
        pack();
    }
    
    private JComponent createSchemaPanel() {
        String[] schemas = null;
        try {
            schemas = OrcsProcedures.getSchemaNames();
        } catch(SQLException sqle) {
            sqle.printStackTrace();
        }

        String[] schemas2 = new String[schemas.length+1];
        schemas2[0] = "";
        System.arraycopy(schemas, 0, schemas2, 1, schemas.length);
        JComboBox jcb = new JComboBox(schemas2);
        jcb.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JComboBox box = (JComboBox) event.getSource();
                OrcsTerminal.this.currentSchema = (String) box.getSelectedItem();
                fireSchemaChanged();
            } 
        });
        return jcb;
    }
    
    private void fireSchemaChanged() {
        String[] tables = null;
        try {
            tables = OrcsProcedures.getTableNamesForSchema(this.currentSchema, "_$ORCS");
        } catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        DefaultListModel model = ((DefaultListModel)this.tableList.getModel());
        model.clear();
        model.addElement("");
        for(int i=0; i<tables.length; i++) {
            model.addElement(tables[i].substring(0, tables[i].length() - "_$ORCS".length()));
        }
    }
    
    private JComponent createTablePanel() {
        this.tableList = new JList(new DefaultListModel());
        this.tableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.tableList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    JList list = (JList) event.getSource();
                    OrcsTerminal.this.currentTable = (String) list.getSelectedValue();
                    fireTableChanged();
                }
            } 
        });
        return new JScrollPane(this.tableList);
    }
    
    private void fireTableChanged() {
        List list = null;
        String[] headers = null;
        try {
            list = OrcsProcedures.getTableAtDate(this.currentSchema, this.currentTable, this.currentDate, this.currentDeleted);
            headers = OrcsProcedures.getColumnNamesForTable(this.currentSchema, this.currentTable);
        } catch(SQLException sqle) {
            sqle.printStackTrace();
        }

        OrcsTableModel otm = (OrcsTableModel)this.dataTable.getModel();
        otm.setData(headers, list);
        
        // Painful. Time to write a custom table model?
        DefaultTableColumnModel dcm = (DefaultTableColumnModel)this.dataTable.getColumnModel();
        int size = dcm.getColumnCount();
        for(int i=size-1; i>=0; i--) {
            dcm.removeColumn(dcm.getColumn(i));
        }
        for(int i=0; i<headers.length; i++) {
            TableColumn tc = new TableColumn(i);
            tc.setHeaderValue(headers[i]);
            dcm.addColumn(tc);
        }
        
    }
    
    private JComponent createCommandPanel() {
        JPanel panel = new JPanel();
        
        final DateField datefield = CalendarFactory.createDateField();
        
        panel.add(datefield);
        
        final JTimeField timefield = new JTimeField(this.currentDate, new SpinnerListener() {
            public void valueOverflowed(SpinnerEvent event) {
                Date d = (Date) datefield.getValue();
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                c.add(Calendar.DATE, 1);
                datefield.setValue(c.getTime());
            }

            public void valueUnderflowed(SpinnerEvent event) {
                Date d = (Date) datefield.getValue();
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                c.add(Calendar.DATE, -1);
                datefield.setValue(c.getTime());
            }
        });

        panel.add(timefield);

        // TODO: Have this disabled until a table is chosen
        JButton button = new JButton("Table at date");
        
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Date d = (Date) datefield.getValue();
                timefield.applyTimeToDate(d);
                currentDate = d;
                fireTableChanged();
            }
        }); 
        
        panel.add(button);
        
        JCheckBox box = new JCheckBox("Include deleted rows?", false);
        box.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JCheckBox box = (JCheckBox) event.getSource();
                currentDeleted = box.isSelected();
                fireTableChanged();
            }
        });
        panel.add(box);

        return panel;
    }
    
    private JComponent createActionPanel() {
        JPanel panel = new JPanel();
        JButton button = new JButton("Save to CSV");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                int returnVal = jfc.showSaveDialog(OrcsTerminal.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = jfc.getSelectedFile();
                    FileWriter writer = null;
                    try {
                        writer = new FileWriter(file);
                        CsvWriter csv = new CsvWriter(writer);
                        TableModel model = dataTable.getModel();
                        for(int i=0; i<model.getColumnCount(); i++) {
                            csv.writeField(model.getColumnName(i));
                        }
                        csv.endBlock();
                        for(int i=0; i<model.getRowCount(); i++) {
                            for(int j=0; j<model.getColumnCount(); j++) {
                                csv.writeField(model.getValueAt(i, j).toString());
                            }
                            csv.endBlock();
                        }
                    } catch(IOException ioe) {
                        ioe.printStackTrace();
                    } finally { 
                        if(writer != null) {
                            try {
                                writer.close();
                            } catch(IOException ioe) {
                                // ignore
                            }
                        }
                    }
                }
            }
        });
        panel.add(button);
        return panel;
    }
    
    private JComponent createDataPanel() {
        
        ListSelectionModel lsm = new DefaultListSelectionModel();
        lsm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        lsm.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                // TODO: Switch to get the source from the event?
                if(dataTable != null) {
                    int row = dataTable.getSelectedRow();
                    if(row > 0) {
                        currentOrcsRowId = ((OrcsTableModel)dataTable.getModel()).getOrcsRowId(row);
                        fireSelectedRowChanged();
                    }
                }
            } 
        });
        
        this.dataTable = new JTable(new OrcsTableModel(), new DefaultTableColumnModel(), lsm);
        GuiHelper.calcColumnWidths(this.dataTable);
        
        return new JScrollPane(this.dataTable);
    }
    
    void fireSelectedRowChanged() {
        List list = null;
        String[] headers = new String[] { "Revision", "Revision date" }; 
        try {
            list = OrcsProcedures.getRevisionsForOrcsRowId(this.currentSchema, this.currentTable, this.currentOrcsRowId);
        } catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        
        OrcsTableModel otm = (OrcsTableModel)this.revisionTable.getModel();
        otm.setData( headers , list );
        
    }
    
    private JComponent createRevisionPanel() {
        ListSelectionModel lsm = new DefaultListSelectionModel();
        lsm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        lsm.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                // TODO: Switch to get the source from the event?
                if(revisionTable != null) {
                    int row = revisionTable.getSelectedRow();
                    if(row >= 0) {
                        /*
                        // TODO: Fix this hack. This is actually orcs_deleted
                        Object deleted = ((OrcsTableModel)revisionTable.getModel()).getOrcsRowId(row);
                        if(deleted == null) {
                            // make the background RED or something
                        }
                        */
                        currentRevision = dataTable.getModel().getValueAt(row, 0);
                        fireSelectedRevisionChanged();
                    }
                }
            } 
        });
        
        DefaultTableColumnModel dcm = new DefaultTableColumnModel();

        TableColumn tc = new TableColumn(0);
        tc.setHeaderValue("Revision");
        dcm.addColumn(tc);
        tc = new TableColumn(1);
        tc.setHeaderValue("Revision date");
        dcm.addColumn(tc);

        this.revisionTable = new JTable(new OrcsTableModel(), dcm, lsm);
        GuiHelper.calcColumnWidths(this.revisionTable);
                        
        return new JScrollPane(this.revisionTable);
    }
    
    void fireSelectedRevisionChanged() {
        List list = null;
        try {
            list = OrcsProcedures.getTableRowAtRevision(this.currentSchema, this.currentTable, this.currentOrcsRowId, this.currentRevision);
        } catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        OrcsTableModel otm = (OrcsTableModel)this.rowDataTable.getModel();
        otm.setData(new String[] { "Column", "Value"}, list);
    }
    
    private JComponent createRowDataPanel() {
        DefaultTableColumnModel dcm = new DefaultTableColumnModel();

        TableColumn tc = new TableColumn(0);
        tc.setHeaderValue("Column");
        dcm.addColumn(tc);
        tc = new TableColumn(1);
        tc.setHeaderValue("Value");
        dcm.addColumn(tc);

        this.rowDataTable = new JTable(new OrcsTableModel(), dcm);
        GuiHelper.calcColumnWidths(this.rowDataTable);
        return new JScrollPane(this.rowDataTable);
    }
    
    private JComponent createRollbackPanel() {
        JButton button = new JButton("Rollback");
        button.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO: Handle transactions
                
                // don't allow rollbacks to the latest revision               
                int row = revisionTable.getSelectedRow();
                if(row == revisionTable.getModel().getRowCount() - 1) {
                    System.out.println("Illegal to rollback to the latest revision. ");
                    return;
                }
                
                // don't allow rollbacks to deleted columns
                // TODO: Fix this hack. This is actually orcs_deleted
                Object deleted = ((OrcsTableModel)revisionTable.getModel()).getOrcsRowId(row);
                if(deleted != null) {
                    System.out.println("Illegal to rollback to a deleted revision. ");
                    return;
                }
                    
                TableModel model = rowDataTable.getModel();
                int size = model.getRowCount();
                String[] columnNames = new String[size];
                Object[] values = new Object[size];
                for(int i=0; i<size; i++) {
                    columnNames[i] = (String) model.getValueAt(i, 0);
                    values[i] = model.getValueAt(i, 1);
                }
                try {
                    OrcsProcedures.rollback(currentSchema, currentTable, columnNames, values);
                } catch(SQLException sqle) {
                    sqle.printStackTrace();
                }
                fireTableChanged();
                fireSelectedRowChanged();
            } 
        });
        return button;
    }
    
}