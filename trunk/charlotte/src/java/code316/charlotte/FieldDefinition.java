package code316.charlotte;


public interface FieldDefinition {
    String getName();
    void setName(String name);
    
    int getLength();
    void setLength(int length);
    
    
    String getOperands();
    void setOperands(String string);

    int getOffset();
    void setOffset(int i);
}