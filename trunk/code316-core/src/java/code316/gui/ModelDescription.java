package code316.gui;

import code316.beans.BeanProperty;


public class ModelDescription {
    private Class classes[];
    private String columnNames[];
    private String propertyNames[];
    private BeanProperty fields[]; 
    
    public ModelDescription(BeanProperty[] properties) {
        this.fields = properties;
        int count = properties.length;
        this.classes = new Class[count]; 
        this.propertyNames = new String[count];
        this.columnNames = new String[count];
        
        for (int i = 0; i < count; i++) {
            BeanProperty prop = properties[i];
            classes[i] = prop.getType();
            propertyNames[i] = prop.getName();
            columnNames[i] = prop.getName();
        }        
    }
    
    
    public ModelDescription(String properties[]) {
        Class classes[] = new Class[properties.length];
        for (int i = 0; i < classes.length; i++) {
            classes[i] = String.class;
        }
        
        setClasses(classes);
        setProperties(properties);
        setColumnNames(properties);
    }

	public ModelDescription(Class classes[], String properties[]) {
        this(classes, properties, properties);
	}

	public ModelDescription(Class classes[], 
                String properties[], 
                String columnHeaderNames[]) {
		setClasses(classes);
		setProperties(properties);
		setColumnNames(columnHeaderNames);
	}

    public BeanProperty[] getFields() {
        return fields;
    }

    public void setFields(BeanProperty[] fields) {
        this.fields = fields;
    }


    public Class[] getClasses() {
        return classes;
    }

    public int getColumnCount() {        
        return propertyNames.length;
    }


    /**
     * @deprecated Use getColumnCount() instead.
     * 
     * @return Number of columns represented by this description
     */
    public int getColumns() {
        return propertyNames.length;
    }

    public void setClasses(Class[] columnClasses) {
        this.classes = columnClasses;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }


    /**
     * @deprecated Use getColumnNames() instead
     * @param columnNames
     */
    public String[] getNames() {
        return columnNames;
    }

    /**
     * @deprecated Use setColumnNames() instead
     * @param columnNames
     */
    public void setNames(String[] columnNames) {
        this.columnNames = columnNames;
    }


    /**
     * @deprecated Use getPropertyNames instead.
     * @return
     */
    public String[] getProperties() {
        return propertyNames;
    }

    /**
     * @deprecated Use setPropertyNames instead.
     * @return
     */
    public void setProperties(String[] propertyNames) {
        this.propertyNames = propertyNames;
    }
    
    public String[] getPropertyNames() {
        return propertyNames;
    }

    public void setPropertyNames(String[] propertyNames) {
        this.propertyNames = propertyNames;
    }
}
