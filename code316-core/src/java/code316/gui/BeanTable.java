package code316.gui;



import java.awt.Component; 


import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


import code316.beans.BeanProperty;
import code316.beans.BeanUtil;


public class BeanTable extends JTable {
    private boolean editable; 
    

    public BeanTable() {        
        DefaultTableCellRenderer o = new DefaultTableCellRenderer(){

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {                
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        
        setDefaultRenderer(long.class, getDefaultRenderer(Long.class));
        setDefaultRenderer(int.class, getDefaultRenderer(Integer.class));
        setDefaultRenderer(boolean.class, getDefaultRenderer(Boolean.class));
        setDefaultRenderer(short.class, getDefaultRenderer(Short.class));
        setDefaultRenderer(byte.class, getDefaultRenderer(Byte.class));
        setDefaultRenderer(char.class, getDefaultRenderer(Character.class));
        setDefaultRenderer(double.class, getDefaultRenderer(Double.class));
        setDefaultRenderer(float.class, getDefaultRenderer(Float.class));
        
        setDefaultEditor(long.class, getDefaultEditor(Long.class));
        setDefaultEditor(int.class, getDefaultEditor(Integer.class));
        setDefaultEditor(boolean.class, getDefaultEditor(Boolean.class));
        setDefaultEditor(short.class, getDefaultEditor(Short.class));
        setDefaultEditor(byte.class, getDefaultEditor(Byte.class));
        setDefaultEditor(char.class, getDefaultEditor(Character.class));
        setDefaultEditor(double.class, getDefaultEditor(Double.class));
        setDefaultEditor(float.class, getDefaultEditor(Float.class));
    }
    
    
    public BeanTable(Class beanClass) {
        this();
        BeanProperty properties[] = BeanUtil.getProperties(beanClass);
        ModelDescription md = new ModelDescription(properties);
        setModel(new BeanTableModel(md));         
    }
    
    public static BeanTable forObject(Object o) {
        return new BeanTable(o.getClass());
    }   
    
    public static BeanTable forType(Class _class) {
        return new BeanTable(_class);
    }

    public boolean isCellEditable(int row, int column) {
        return this.editable;
    }

    public void setEditable(boolean b) {
        this.editable = b;
    }
    
    public boolean isEditable() {
        return editable;
    }
}
