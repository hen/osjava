package org.cyberiantiger.nio;

/**
 * An interface to represent a simple data input
 */
public interface Input {

    /**
     * Specify an output for this input to write to
     */
    public void writeTo(Output out);

}
