package org.cyberiantiger.telnet;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A static class for parsing telnet options, coz it's stateless.
 */
public class OptionParser {

    // No-One can instantiate the class
    private OptionParser() {}

    public static TelnetOption parseOption(InputStream in, int option) 
    throws IOException 
    {
	if(option < 240) {
	    return new UnknownTelnetOption(option);
	} else if(option < 250) {
	    return new SimpleTelnetOption(option);
	} else if(option == TelnetOption.SB) {
	    int suboption = in.read();
	    // Check for end of stream.
	    if(suboption == -1) return null;
	    ByteArrayOutputStream out = new ByteArrayOutputStream(40);
	    while(true) {
		int i = in.read();
		// Check for end of stream.
		if(i == -1) return null;
		if(i == TelnetOption.IAC) {
		    int j = in.read();
		    // Check for end of stream.
		    if(j == -1) return null;
		    if(j == TelnetOption.IAC) {
			out.write(TelnetOption.IAC);
		    } else if(j == TelnetOption.SE) {
			return new TelnetSubOption(suboption, out.toByteArray());
		    } else {
			// It's a broken telnet implementation, assume they
			// just forgot to double the TelnetOption.IAC.
			out.write(TelnetOption.IAC);
			out.write(j);
		    }
		} else {
		    out.write(i);
		}
	    }
	} else {
	    int suboption = in.read();
	    if(suboption == -1) return null;
	    return new TelnetNegotiation(option, suboption);
	}
    }
}
