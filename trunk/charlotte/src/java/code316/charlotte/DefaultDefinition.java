package code316.charlotte;
public class DefaultDefinition implements FieldDefinition {    
    private String name;
    private String operands = "";
    private int offset;
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
    
    public int getWidth() {
        return this.length;
    }
    
    public int getOffset() {
        return offset;
    }

    public void setOffset(int i) {
        offset = i;
    }
    
    public void setWidth(int length) {
        this.length = length;
    }
}
