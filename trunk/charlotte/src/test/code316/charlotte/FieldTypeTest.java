package code316.charlotte;

import junit.framework.TestCase;


public class FieldTypeTest extends TestCase {
    public void testGetByName() {
        assertEquals(FieldType.FLOAT, FieldType.getTypeByName(FieldType.FLOAT_NAME));
        assertEquals(FieldType.INTEGER, FieldType.getTypeByName(FieldType.INTEGER_NAME));
        assertEquals(null, FieldType.getTypeByName(""));
        assertEquals(null, FieldType.getTypeByName(null));
        assertEquals(null, FieldType.getTypeByName("nothing"));
    }
    
    public void testEquals() {
        FieldType ft = new FieldType("basic");
        FieldType ft2 = new FieldType("basic");
        
        assertTrue(ft.equals(ft2));        
    }
    
    public void testCopyAndClone() {
        FieldType ft = new FieldType("basic");
        FieldType ft2 = ft.copy();
        
        assertEquals(ft, ft2);
        
        ft2 = (FieldType) ft.clone();    
        assertEquals(ft, ft2);
    }
}
