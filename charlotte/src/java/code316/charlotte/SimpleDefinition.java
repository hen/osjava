package code316.charlotte;
public class SimpleDefinition implements FieldDefinition {    
    private String name;
    private String operands;
    private int offset;
    private FieldType type;
    private int length;

    public String getName() {
        return name;
    }

    public String getOperands() {
        return operands;
    }

    public void setName(String string) {
        name = string;
    }

    public void setOperands(String string) {
        operands = string;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public int getOffset() {
        return offset;
    }

    public void setOffset(int i) {
        offset = i;
    }
    
    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
