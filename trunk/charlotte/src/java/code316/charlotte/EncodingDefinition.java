package code316.charlotte;
import java.util.List;

public interface EncodingDefinition {
	String getName();
	void setName(String name);
    void addFieldDefinition(FieldDefinition fd);
	int getLength();
	List getFieldDefinitions();	
}
