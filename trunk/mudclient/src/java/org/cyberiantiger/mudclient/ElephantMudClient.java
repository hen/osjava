package org.cyberiantiger.mudclient;

import java.util.*;
import java.io.*;
import org.cyberiantiger.console.*;
import org.cyberiantiger.mudclient.parser.*;
import org.cyberiantiger.mudclient.net.*;
import org.cyberiantiger.mudclient.ui.ControlWindow;
import org.cyberiantiger.mudclient.ui.MudClientUI;

public class ElephantMudClient implements MudClient {

    private boolean echo = true;
    private ControlWindow control;
    private MudConnection connection;
    private Map customOutputTypes = new HashMap();
    private Map customOutputNames = new HashMap();

    public ElephantMudClient(Properties config) {
	connection = 
	new MudConnection(
		this,
		config.getProperty("host"),
		Integer.parseInt(config.getProperty("port"))
		);
	setParser(new ANSIParser());
	//defaultWindow = createOutputWindow("default");
	control = new ControlWindow(this);
	control.show();

	Iterator i;
	
	i = config.keySet().iterator();
	while(i.hasNext()) {
	    String propName = (String) i.next();
	    if(propName.startsWith("outputs.")) {
		String outputName = propName.substring("outputs.".length());
		List tmp = new ArrayList();
		StringTokenizer msgClasses = new StringTokenizer(
			config.getProperty(propName),
			","
			);
		while(msgClasses.hasMoreTokens()) {
		    tmp.add(msgClasses.nextToken());
		}
		customOutputTypes.put(outputName, tmp);
	    }
	}

	i = customOutputTypes.entrySet().iterator();
	while(i.hasNext()) {
	    Map.Entry entry = (Map.Entry) i.next();
	    String name = (String) entry.getKey();
	    Iterator j = ((List)entry.getValue()).iterator();
	    while(j.hasNext()) {
		customOutputNames.put(j.next(),name);
	    }
	}
    }

    public void consoleAction(ConsoleAction action) {
	control.consoleAction(action);
    }

    public void exit() {
	connection.disconnect();
	connection.stop(); // Deprecated.
	control.hide();
	System.exit(0); // Don't like using this method.
    }

    public void setParser(Parser parser) {
	connection.setParser(parser);
    }

    public void connect() {
	connection.connect();
    }
    
    public void disconnect() {
	connection.disconnect();
    }


    public void command(String sourceId, String msg) {
	connection.command(msg);
	if(echo) {
	    MudClientUI ui = control.getView(sourceId);
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

    public void setWindowSize(int w, int h) {
	connection.setWindowSize(w,h);
    }

    public String getCustomOutputName(String pClass) {
	return (String)customOutputNames.get(pClass);
    }

    public List getCustomOutputTypes(String outputName) {
	return (List)customOutputTypes.get(outputName);
    }

    public void connectionStatusChanged(int newStatus) {
	control.connectionStatusChanged(newStatus);
    }

    public void connectionDoLocalEcho(boolean echo) {
	this.echo = echo;
    }

    public static void main(String[] args) {
	if(args.length == 1) {
	    try {
		Properties props = new Properties();
		props.load(new FileInputStream(args[0]));
		new ElephantMudClient(props);
	    } catch (IOException ioe) {
		System.out.println("Failed to load config!");
	    }
	}  else {
	    try {
		Properties config = new Properties();
		config.load(ElephantMudClient.class.getResourceAsStream("config.properties"));
		new ElephantMudClient(config);
	    } catch (IOException ioe) {
		System.out.println("Failed to load default config!");
	    }
	}
    }
}
