package code316.charlotte;


public class Value {
    private FieldDefinition fieldDefinition;
    private long raw;
    private double expanded;
    
    public double getExpanded() {
        return expanded;
    }

    public void setExpanded(double expanded) {
        this.expanded = expanded;
    }

    public long getRaw() {
        return raw;
    }

    public void setRaw(long raw) {
        this.raw = raw;
    }
    
    public FieldDefinition getFieldDefinition() {
        return fieldDefinition;
    }

    public void setFieldDefinition(FieldDefinition fieldDefinition) {
        this.fieldDefinition = fieldDefinition;
    }
}
