package org.osjava.payload;

import java.util.Properties;

import junit.framework.*;

public class InterpolationTest extends TestCase {

    Interpolation interpolation = null;

    public void setUp() {
        this.interpolation = Interpolation.DEFAULT;
    }

    public void tearDown() {
        this.interpolation = null;
    }

    public void testInterpolatable() {
        assertTrue( this.interpolation.interpolatable("xml") );
        assertTrue( this.interpolation.interpolatable("jcml") );
        assertTrue( this.interpolation.interpolatable("properties") );
        assertTrue( this.interpolation.interpolatable("txt") );
        assertTrue( this.interpolation.interpolatable("conf") );
        assertFalse( this.interpolation.interpolatable("sh") );
        assertFalse( this.interpolation.interpolatable("pl") );
        assertFalse( this.interpolation.interpolatable("") );
    }
    public void testInterpolate() {
        String before = "Fred ${BANG} ${FOO} Joe";
        Properties props = new Properties();
        props.setProperty("BANG", "boom");
        props.setProperty("foo", "fail");
        assertEquals( "Fred boom ${FOO} Joe", this.interpolation.interpolate(before, props) );
    }

}
