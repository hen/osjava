package code316.charlotte;


public interface FieldDefinition {
    String getName();
    void setName(String name);
    
    int getWidth();
    void setWidth(int width);
    
    
    String getOperands();
    void setOperands(String string);

    int getOffset();
    void setOffset(int i);
}