package code316.core;

import java.util.Properties;

import junit.framework.TestCase;

public class FilterPropertiesTest extends TestCase {
    public FilterPropertiesTest(String arg0) {
        super(arg0);
    }
    
    
    public void test() throws Exception {
        Properties props = new Properties();
        props.put("want1", "value");
        props.put("dontwant", "value");
        props.put("want2", "value");
        props.put("want3", "value");
        props.put("dontwant", "value");
        props.put("youwant2", "value");
        props.put("notwant1", "value");
        props.put("want_dashed", "value");
        props.put("want5", "value");
        props.put("want4", "value");
        props.put("dontwant", "value");
        props.put("keyname", "value");

		Properties filtered = PropertiesUtil.filter(props, "want");
		
		assertTrue(filtered.size() == 6);
    }
    

}
