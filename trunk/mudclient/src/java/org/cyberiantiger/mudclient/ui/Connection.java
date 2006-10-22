package org.cyberiantiger.mudclient.ui;

import org.cyberiantiger.mudclient.config.*;

public interface Connection {

    public void connect();

    public void disconnect();

    public void command(String viewId, String msg);

    public void setWindowSize(int w, int h);

    public ClientConfiguration getConfiguration();

    public void exit();

}
