package org.cyberiantiger.mudclient;

import java.util.*;
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

    public ElephantMudClient() {
	connection = new MudConnection(this,"elephant.org",4444);
	setParser(new ANSIParser());
	//defaultWindow = createOutputWindow("default");
	control = new ControlWindow(this);
	control.show();

	// Lame, hard coding, till I can be bothered to setup a config file.
	List tmp;

	tmp = new ArrayList();
	tmp.add("cleric");
	tmp.add("fighter");
	tmp.add("warmage");
	tmp.add("monk");
	tmp.add("druid");
	tmp.add("ranger");
	tmp.add("rogue");
	customOutputTypes.put("Guilds",tmp);
	tmp = new ArrayList();
	tmp.add("ip");
	tmp.add("advice");
	tmp.add("bugs");
	customOutputTypes.put("Events",tmp);
	tmp = new ArrayList();
	tmp.add("boom");
	tmp.add("shout");
	customOutputTypes.put("Spam",tmp);
	tmp = new ArrayList();
	tmp.add("gossip");
	tmp.add("hline");
	tmp.add("pline");
	tmp.add("council");
	tmp.add("newbie");
	customOutputTypes.put("Other channels",tmp);

	Iterator i = customOutputTypes.entrySet().iterator();
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

    public static void main(String[] args) {
	new ElephantMudClient();
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
}
