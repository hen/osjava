package org.cyberiantiger.mudclient.config;

import java.util.*;
import java.io.*;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

public class ClientConfiguration {

    public static final String DEFAULT_VIEW = "DEFAULT";
    public static final String CURRENT_VIEW = "CURRENT";

    private String host;
    private int port;
    private String term;
    private List outputs;
    private String defaultOutput;
    private Map redirects;
    private Map outputConfigs;
    private InputMap keyBindings;


    public ClientConfiguration() {
    }

    public void load(InputStream in) throws IOException {
	StringTokenizer tmp;
	Iterator i;
	Properties props = new Properties();
	props.load(in);
	host=props.getProperty("net.host");
	port=Integer.parseInt(props.getProperty("net.port"));
	term=props.getProperty("net.terminal");

	tmp = new StringTokenizer(props.getProperty("output"),",");

	outputs = new ArrayList();
	while(tmp.hasMoreTokens()) {
	    outputs.add(tmp.nextToken());
	}

	outputConfigs = new HashMap();

	i = outputs.iterator();
	while(i.hasNext()) {
	    String name = (String) i.next();
	    OutputConfiguration outputConfig = new OutputConfiguration();
	    outputConfig.load(props,name);
	    outputConfigs.put(name,outputConfig);
	}

	defaultOutput = props.getProperty("output.default");

	redirects = new HashMap();

	tmp = new StringTokenizer(props.getProperty("redirect"),",");

	Set redirectTypes = new HashSet();
	while(tmp.hasMoreTokens()) {
	    redirectTypes.add(tmp.nextToken());
	}

	i = redirectTypes.iterator();
	while(i.hasNext()) {
	    String type = (String) i.next();
	    tmp = new StringTokenizer(
		    props.getProperty("redirect."+type),","
		    );
	    Set targets = new HashSet();
	    while(tmp.hasMoreTokens()) {
		targets.add(tmp.nextToken());
	    }
	    redirects.put(type,targets);
	}

	tmp = new StringTokenizer(props.getProperty("action"),",");

	Set actionKeys = new HashSet();
	while(tmp.hasMoreTokens()) {
	    actionKeys.add(tmp.nextToken());
	}

	keyBindings = new InputMap();
	i = actionKeys.iterator();
	while(i.hasNext()) {
	    String actionKey = (String) i.next();

	    tmp = new StringTokenizer(
		    props.getProperty("action."+actionKey),",");
	    while(tmp.hasMoreTokens()) {
		String token = tmp.nextToken();
		KeyStroke ks = KeyStroke.getKeyStroke(token);
		if(ks == null) {
		    System.out.println("Invalid KeyStroke: "+token+" for: "+actionKey);
		} else {
		    keyBindings.put(ks,actionKey);
		}
	    }
	}
    }

    /**
     * Get the hostname to connect to
     */
    public String getHost() {
	return host;
    }

    /**
     * Get the port to connect to
     */
    public int getPort() {
	return port;
    }

    /**
     * Get the terminal type to send to the mud
     */
    public String getTerminalType() {
	return term;
    }

    /**
     * Get the name of the default output
     */
    public String getDefaultOutputName() {
	return defaultOutput;
    }

    /**
     * Get the list of output names.
     */
    public List getOutputNames() {
	return outputs;
    }

    /**
     * Get the OutputConfiguration for a specific Output
     */
    public OutputConfiguration getOutputConfiguration(String name) {
	return (OutputConfiguration) outputConfigs.get(name);
    }

    /**
     * Get the list of outputs which a message of type msgClass should be
     * sent to.
     */
    public Set getOutputFor(String msgClass) {
	return (Set) redirects.get(msgClass);
    }

    /**
     * Get a map of key name to action name
     */
    public InputMap getKeyBindings() {
	return keyBindings;
    }
}
