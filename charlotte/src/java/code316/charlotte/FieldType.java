package code316.charlotte;


public class FieldType {
    public static final String INTEGER_NAME = "int";
    public static final String FLOAT_NAME = "float";
    public static final FieldType INTEGER = new FieldType(INTEGER_NAME);
    public static final FieldType FLOAT = new FieldType(FLOAT_NAME);

    private String name;
        
    public FieldType(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public String toString() {
        return this.name;
    }  
    
    public static FieldType getTypeByName(String name) {
        if ( INTEGER_NAME.equals(name) ) {
            return FieldType.INTEGER;
        }
        else if ( FLOAT_NAME.equals(name) ) {
            return FieldType.FLOAT;
        }
        
        return null;
    }
    
    public Object clone() {
        return new FieldType(getName());        
    }
    
    public FieldType copy() {
        return (FieldType) clone();
    }
    
    public boolean equals(Object obj) {
        if ( obj == null || !(obj instanceof FieldType) ) {
            return false;
        }
        FieldType ft = (FieldType) obj;
        
        if ( !this.name.equals(ft.name) ) {
            return false;
        }
        return true;
    }
}