package code316.gui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.table.AbstractTableModel;

import code316.beans.BeanProperty;

public class BeanTableModel extends AbstractTableModel implements EasyTableModel {
	private final List beans = new ArrayList();	
	private ModelDescription modelDescription;
	private boolean allowDuplicates;
    private Map methods = new HashMap();
    private Map propertyNames = new HashMap();
    private Map methodNames = new HashMap();

	public BeanTableModel(ModelDescription model) {
		this.modelDescription = model;
	}
	
    
    /**
     * @deprecated use getAll() instead
     * @return
     */
	public List getBeans() {
		return new ArrayList(this.beans);
	}
	

    public Collection getAll() {
        return new ArrayList(this.beans);
    }


	public void add(Object bean) {
	    synchronized (this.beans) {	        
            insert(beans.size(), bean);
	    }   
	}
    



    public void addAll(Collection beans) {
        Iterator tor = beans.iterator();
        
        while (tor.hasNext()) {
            add(tor.next());
        }    
    }

    /**
     * @deprecated use addAll() instead
     * @param beans
     */
	public void add(List beans) {
        addAll(beans);
	}
	
	public Object get(int index) {
        if ( index >= this.beans.size() ) {
            return null;
        }
		return this.beans.get(index);
	}


    
	public void setData(Collection beans) {
		clear();
		addAll(beans);
	}
    
	
	public void clear() {
        int size = this.beans.size();
		this.beans.clear();
        fireTableRowsDeleted(0, 0); 
	}
	
	
	public int indexOf(Object o) {
		return this.beans.indexOf(o);
	}
	
    public Object getValueAt(int rowIndex, int columnIndex) {
    	String defaultValue = "could not get property";	
    	
    	try {
            String propertyString = this.modelDescription.getPropertyNames()[columnIndex];
            String names[] = (String[]) this.propertyNames.get(propertyString);
            
            if ( names == null ) {
                names = parsePropertyNames(this.modelDescription.getPropertyNames()[columnIndex]);
                this.propertyNames.put(propertyString, names);                
            } 
            
            Object callee = this.beans.get(rowIndex);
            Object result = null;
            
            for (int i = 0; i < names.length; i++) {                
                String propertyName = names[i];
                Method m = findMethod(propertyName, callee);
                callee = result = m.invoke(callee, null);            
            }
            
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            defaultValue += ": " + e;
        }
    	
    	return defaultValue;
    }
    
    private Method findMethod(String propertyName, Object bean) throws NoSuchMethodException {
        if ( bean == null ) {
            throw new IllegalArgumentException("bean is null");
        }
        String methodId = bean.getClass() + ":" + propertyName;
        String methodName = (String) this.methodNames.get(methodId);

        if ( methodName == null && propertyName.endsWith("()") ) {
            String newName = propertyName.substring(0, propertyName.length() - 2);
            Method m = bean.getClass().getMethod(newName, null);
            this.methodNames.put(methodId, newName);
            this.methods.put(methodId, m);
            return m;
        }
                
        if ( methodName == null ) {
            methodName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1); 
            this.methodNames.put(methodId, methodName);
        }           

        Method m = (Method) this.methods.get(methodId);
            
        // try to find regular get method
        try {
            if (m == null) {
                m = bean.getClass().getMethod(methodName, null);
                this.methods.put(methodId, m);                
            }           
        }
        catch (NoSuchMethodException e) {
            // try to find boolean named method
            methodName = "is" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
            this.methodNames.put(methodId, methodName);
            m = bean.getClass().getMethod(methodName, null);
            this.methods.put(methodId, m);                
        }                        
        
        return m;
    }
    
  
    public int getColumnCount() {
        return modelDescription.getColumnCount();
    }

    public int getRowCount() {
        return this.beans.size();
    }
    
    public String getColumnName(int columnIndex) {
    	try {
            return (this.modelDescription.getColumnNames())[columnIndex];
        }
        catch (ArrayIndexOutOfBoundsException e) {
        	throw new IllegalArgumentException("unknown property index: " + columnIndex);
        }
    }
    
    public void removeItem(Object bean) {
    	synchronized (this.beans) {
	    	int index = this.beans.indexOf(bean);

	    	if ( index >= 0 ) {
	    		this.beans.remove(index);
	    		fireTableRowsDeleted(index - 1, index - 1);
	    	}
    	}
    }
    
    public Class getColumnClass(int columnIndex) {
    	try {
        	return (this.modelDescription.getClasses())[columnIndex];    
        }
        catch (ArrayIndexOutOfBoundsException e) {
        	throw new IllegalArgumentException("unknown column index: " + columnIndex);
        }    	
    } 
    
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Object bean = get(rowIndex);
        BeanProperty bp = this.modelDescription.getFields()[columnIndex];
        try {
            bp.getSetter().invoke(bean, new Object[]{value});
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public boolean getAllowDuplicates() {
        return allowDuplicates;
    }

    public void setAllowDuplicates(boolean allowDuplicates) {
        this.allowDuplicates = allowDuplicates;
    }
    
    public void insert(int index, Object o) {
        synchronized (this.beans) {         
            if ( !allowDuplicates && this.beans.contains(o) ) {
                return;
            }

            this.beans.add(index, o);
        }
        
        fireTableRowsInserted(index, index);   
    }
    
    
    static String[] parsePropertyNames(String propertyName) {
        List names = new ArrayList();
        
        StringTokenizer st = new StringTokenizer(propertyName, ".");
        while (st.hasMoreTokens()) {
            names.add(st.nextToken());
        }
        
        return (String[]) names.toArray(new String[0]);    
    }
}
