package code316.gui;

import java.util.Collection;

import javax.swing.table.TableModel;

public interface EasyTableModel extends TableModel {
    public abstract void add(Object o);
    public abstract void addAll(Collection c);
    public abstract void setData(Collection c);
    public abstract Object get(int index);
    public abstract Collection getAll();
    public abstract void insert(int index, Object o);
}
