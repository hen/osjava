package code316.core;

import junit.framework.TestCase;


public class FileUtilTest extends TestCase {

    public FileUtilTest(String arg0) {
        super(arg0);
    }
    
    public void testGetExtension() {
        String fileName = "a.b.c.d.e.f.txt";
        assertEquals("txt", FileUtil.getExtension(fileName));
    }
}
