package code316.charlotte;
import java.math.BigInteger;
import java.util.List;

public interface EncodingDefinition {
	String getName();
	void setName(String name);
    void addFieldDefinition(FieldDefinition fd);
	int getLength();
	List getFieldDefinitions();	
    Value[] unpackValues(BigInteger bits);
}
