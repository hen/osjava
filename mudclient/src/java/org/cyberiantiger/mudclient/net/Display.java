package org.cyberiantiger.mudclient.net;

import org.cyberiantiger.mudclient.config.*;
import org.cyberiantiger.console.ConsoleWriter;

public interface Display {

    public ClientConfiguration getConfiguration();

    public void connectionStatusChanged(int status);

    public void connectionDoLocalEcho(boolean echo);

    public ConsoleWriter getConsoleWriter();

}
