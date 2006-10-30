package org.cyberiantiger.mudclient;

import java.util.*;
import java.io.*;
import org.cyberiantiger.console.*;
import org.cyberiantiger.mudclient.config.*;
import org.cyberiantiger.mudclient.parser.*;
import org.cyberiantiger.mudclient.net.*;
import org.cyberiantiger.mudclient.input.InputProcessor;
import org.cyberiantiger.mudclient.ui.ControlWindow;
import org.cyberiantiger.mudclient.ui.Connection;

public class ElephantMudClient implements Display, Connection {

    private boolean echo = true;
    private ControlWindow control;
    private MudConnection connection;
    private ConsoleWriter writer;
    private InputProcessor processor;

    private ClientConfiguration config;

    public ElephantMudClient(ClientConfiguration config) {
	this.config = config;
	writer = new ElephantConsoleWriter();
	processor = new InputProcessor(this, this);
	connection = new MudConnection(this);
	control = new ControlWindow(processor);
	control.show();
    }

    public void exit() {
	connection.stop(); // Deprecated.
        File configFile = getConfigFile();
        try {
            config.write(new FileOutputStream(configFile));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        System.exit(0); // Evil, but necessary.
    }

    public void connect() {
	connection.setParser(new ANSIParser());
	connection.connect();
    }
    
    public void disconnect() {
	connection.disconnect();
    }


    public void command(String sourceId, String msg) {
	connection.command(msg);
    }

    public void setWindowSize(int w, int h) {
	connection.setWindowSize(w,h);
    }

    public void connectionStatusChanged(int newStatus) {
	control.connectionStatusChanged(newStatus);
    }

    public void connectionDoLocalEcho(boolean echo) {
	this.echo = echo;
    }

    public boolean getEcho() {
	return echo;
    }

    public ClientConfiguration getConfiguration() {
	return config;
    }

    public ConsoleWriter getConsoleWriter() {
	return writer;
    }

    public void localEcho(String msg) {
	if(echo) {
	    ConsoleWriter ui = control.getCurrentView();
	    ui.consoleAction(
		    new StringConsoleAction(msg.toCharArray(),0,msg.length())
		    );
	    ui.consoleAction(
		    new SetCursorXConsoleAction(0)
		    );
	    ui.consoleAction(
		    new MoveCursorYConsoleAction(1)
		    );
	}
    }

    private class ElephantConsoleWriter implements ConsoleWriter {

	protected ConsoleWriter getView(String name) {
	    if(name.equals(ClientConfiguration.DEFAULT_VIEW)) {
		return control.getDefaultView();
	    } else if(name.equals(ClientConfiguration.CURRENT_VIEW)) {
		return control.getCurrentView();
	    } else {
		return control.getView(name);
	    }
	}

	public void consoleAction(ConsoleAction action) {
	    if(action instanceof ElephantMUDConsoleAction) {
		ElephantMUDConsoleAction eAction =
		(ElephantMUDConsoleAction) action;

		String pClass = eAction.getPrimaryClass();

		Set dests = config.getOutputFor(pClass);


		if(dests == null) {
		    control.getDefaultView().consoleAction(action);
		} else {
		    Set tmp = new HashSet();
		    Iterator i = dests.iterator();
		    while(i.hasNext()) {
			tmp.add(getView((String)i.next()));
		    }

		    tmp.remove(null);

		    i = tmp.iterator();
		    while(i.hasNext()) {
			((ConsoleWriter)i.next()).consoleAction(action);
		    }
		}

	    } else {
		control.getDefaultView().consoleAction(action);
	    }
	}

    }

    public static final File getConfigFile() {
        File userHome = new File(System.getProperty("user.home"));
        return new File(userHome, ".mudclientrc");
    }

    public static void main(String[] args) {
	if(args.length == 1) {
	    try {
		ClientConfiguration config = new ClientConfiguration();
		config.load(new FileInputStream(args[0]));
		new ElephantMudClient(config);
	    } catch (IOException ioe) {
		ioe.printStackTrace();
	    }
	}  else {
	    try {
		ClientConfiguration config = new ClientConfiguration();
                File configFile = getConfigFile();
                if (configFile.exists()) {
                    config.load(new FileInputStream(configFile));
                } else {
                    config.load(
                            ElephantMudClient.class.getResourceAsStream(
                                "/org/cyberiantiger/mudclient/config.properties"
                                )
                            );
                }
		new ElephantMudClient(config);
	    } catch (IOException ioe) {
		ioe.printStackTrace();
	    }
	}
    }
}
