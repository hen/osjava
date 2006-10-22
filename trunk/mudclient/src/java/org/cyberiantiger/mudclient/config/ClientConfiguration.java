package org.cyberiantiger.mudclient.config;

import java.util.*;
import java.io.*;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

import org.cyberiantiger.mudclient.input.Alias;

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
    private Map aliases;
    private boolean useProxy;
    private boolean useJvmProxy;
    private String proxyHost;
    private int proxyPort;
    private String characterEncoding;

    public ClientConfiguration() {
    }

    public static int getInt(Properties props, String key, int def)
    {
        String prop = props.getProperty(key);
        if (prop != null) {
            try {
                return Integer.parseInt(prop);
            } catch (NumberFormatException nfe) {
            }
        }
        return def;
    }

    public static String getString(Properties props, String key, String def)
    {
        String prop = props.getProperty(key);
        if (prop == null) {
            return def;
        } else {
            return prop;
        }
    }

    public static boolean getBoolean(Properties props, String key, boolean def)
    {
        return "yes".equals(props.getProperty(key));
    }

    public void load(InputStream in) throws IOException {
	StringTokenizer tmp;
	Iterator i;
	Properties props = new Properties();
	props.load(in);
	host = getString(props,"net.host","elephant.org");
	port = getInt(props,"net.port",4444);
	term = getString(props,"net.terminal","ansi");
        useProxy = getBoolean(props,"net.useProxy",false);
        useJvmProxy = getBoolean(props,"net.useJvmProxy",true);
        proxyHost = getString(props,"net.proxyHost","");
        proxyPort = getInt(props,"net.proxyPort",0);
        characterEncoding = getString(props,"net.characterEncoding","UTF-8");

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

	/* Load aliases */
	aliases = new HashMap();
	i = outputConfigs.keySet().iterator();
	while (i.hasNext()) {
	    String keyName = (String) i.next();
	    int count = Integer.parseInt(props.getProperty("alias."+keyName+".count", "0"));
	    if (count == 0) {
		continue;
	    }
	    int c = 0;
	    List aliases = new ArrayList();
	    while(c++ < count) {
		String regexp = props.getProperty("alias."+keyName+"."+c+".regexp");
		String replace = props.getProperty("alias."+keyName+"."+c+".replace");
		boolean terminate = "yes".equals(props.getProperty("alias."+keyName+"."+c+".terminate"));
		aliases.add(new Alias(regexp, replace, terminate));
	    }
	    this.aliases.put(keyName, aliases);
	}
    }

    /**
     * Get the hostname to connect to
     */
    public String getHost() {
	return host;
    }

    /**
     * Set the hostname to connect to
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Get the port to connect to
     */
    public int getPort() {
	return port;
    }

    /**
     * Set the port to connect to
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Should we use a webproxy server
     */
    public boolean getUseProxy() {
        return useProxy;
    }

    /**
     * Set whether to use a proxy.
     */
    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    /**
     * Get use jvm proxy flag.
     */
    public boolean getUseJvmProxy() {
        return useJvmProxy;
    }

    /**
     * Set use jvm proxy flag.
     */
    public void setUseJvmProxy(boolean useJvmProxy) {
        this.useJvmProxy = useJvmProxy;
    }

    /**
     * Get webproxy address.
     */
    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * Set proxy address.
     */
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    /**
     * Get jvm proxy address.
     */
    public String getJvmProxyHost() {
        return System.getProperty("http.proxyHost", "");
    }

    /**
     * Get proxy port.
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * Set proxy port
     */
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    /**
     * Get jvm proxy port.
     */
    public int getJvmProxyPort() {
        try {
            return Integer.parseInt(System.getProperty("http.proxyPort", "80"));
        } catch (NumberFormatException nfe) {
            return 80;
        }
    }

    /**
     * Get character encoding for the connection.
     */
    public String getCharacterEncoding() {
        return characterEncoding;
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

    /**
     * Get a list of aliases for a specific input id.
     */
    public List getAliases(String sourceId) {
	return (List) aliases.get(sourceId);
    }
}
