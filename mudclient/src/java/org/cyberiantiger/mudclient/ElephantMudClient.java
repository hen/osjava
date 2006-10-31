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
            Properties props = config.save();
            props.store(new FileOutputStream(configFile),"Elephant Mud Client");
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

    private static final File getConfigFile() {
        File userHome = new File(System.getProperty("user.home"));
        return new File(userHome, ".mudclientrc");
    }

    private static final InputStream getDefaultConfig() {
        return ElephantMudClient.class.getResourceAsStream(
                "/org/cyberiantiger/mudclient/config.properties"
                );
    }



    public static void main(String[] args) {
        Properties props = new Properties();
        /* Load defaults */
        try {
            props.load(getDefaultConfig());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
	if(args.length == 1) {
            /* Load args[0] if set */
            try {
                props.load(new FileInputStream(args[0]));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            /* Load config file if exists */
            File configFile = getConfigFile();
            if (configFile.exists()) {
                try {
                    props.load(new FileInputStream(configFile));
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        ClientConfiguration config = new ClientConfiguration();
        config.load(props);
        new ElephantMudClient(config);
    }
}
