package org.cyberiantiger.telnet;

import java.io.*;

public interface TelnetSession {

    public void receiveOption(TelnetOption opt) 
    throws IOException;

    public void setOutputStream(OutputStream out)
    throws IOException;

}
