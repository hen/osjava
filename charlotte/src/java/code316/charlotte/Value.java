package code316.charlotte;


public class Value {
    private double expanded;
    private FieldDefinition fieldDefinition;
    private long raw;

    public FieldDefinition getFieldDefinition() {
        return fieldDefinition;
    }


    public void setFieldDefinition(FieldDefinition fieldDefinition) {
        this.fieldDefinition = fieldDefinition;
    }

    public void setRaw(long raw) {
        this.raw = raw;
    }
    
    /**
     * Returns the long value represented by a fields bits within a set
     * of encoded bits.  To receive the value of the field  with any
     * expressions applied to it, see getExpanded
     */
    public long getRaw() {
        return raw;
    }    
    
    public double getExpandedAsDouble() {
        return this.expanded;
    }

    public long getExpandedAsLong() {
        return (long) this.expanded;
    }

    public int getExpandedAsInt() {
        return (int) this.expanded;
    }

    public float getExpandedAsFloat() {
        return (float) this.expanded;
    }

    public void setExpanded(double expanded) {
        this.expanded = expanded;
    }
}
