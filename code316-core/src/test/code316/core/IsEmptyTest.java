package code316.core;
import code316.core.PropertiesUtil;

import junit.framework.TestCase;

public class IsEmptyTest extends TestCase {

    public IsEmptyTest(String arg0) {
        super(arg0);
    }
    
    public void test() throws Exception {
    	String emptyString = "";
    	String nullString = null;
    	String whiteString = "  \t   ";
    	String goodString = " good ";
    	String goodString2 = "good";
    	
    	assertTrue(PropertiesUtil.isEmpty(emptyString));
    	assertTrue(PropertiesUtil.isEmpty(nullString));
    	assertTrue(PropertiesUtil.isEmpty(whiteString));
    	assertTrue(!PropertiesUtil.isEmpty(goodString));
    	assertTrue(!PropertiesUtil.isEmpty(goodString2));
    }

}
