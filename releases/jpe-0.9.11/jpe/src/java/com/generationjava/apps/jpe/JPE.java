package com.generationjava.apps.jpe;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;

/**
* core class for JPE, works like a hub to all the handlers that do
* most of the work. Also can be started as a java application because
* it implements the main interface.
*/
public class JPE extends Object {

	// hashtable that holds the loadable program parts
	private Hashtable handlers = new Hashtable();

	// Text handler, loadable part that handles the edit window        
	private Editor editor;

	// Menu bar of this application
	private MenuBar bar;

	// If we get a System.exit() this boolean is checked if its a wanted
	// exit by the Security Manager
	public boolean wantexit = false;

	// New Classloader (unused in this release) 
	//        private static JPEClassloader loader;

	// JPE homedir as detected by classpath
	private String jpehome;

	/**
	* Main JPE constructor starts JPE, all the handler and reads
	* the config.xml. Can be created by a other class or from the 
	* commandline by the Main method.
	*/
	public JPE() {

		// setup the new security manager
		if (!(System.getSecurityManager() instanceof SecManager)) {
			try {
				SecManager sm = new SecManager(this);
				System.setSecurityManager(sm);
			} catch (SecurityException se) {
				System.out.println("JPE -> SecurityManager already loaded");
			}
		}

		// create the menu bar of JPE
		bar = new MenuBar();

		// create the config handler (read config.xml)
		addHandler(new ConfigHandler(this));

// QN: Why did I want this to be configurable??
//		String i18n = getProperty("setup", "i18n");
//		if (i18n == null) {
//			i18n = getMyPath() + "jpe.properties";
//			getConfigHandler().putProperty("setup", "i18n", i18n);
//		}

		// init text controller
		TextController tc = TextController.getInstance();

		// startup the needed Handler (will change in future release)
		Handler handler = new FileHandler(this);
		handler.createMenu(bar);
		addHandler(handler);

		handler = new EditHandler(this);
		handler.createMenu(bar);
		addHandler(handler);

		handler = new LogHandler(this);
		handler.createMenu(bar);
		addHandler(handler);

		handler = new ExtraHandler(this);
		handler.createMenu(bar);
		addHandler(handler);

		handler = new HelpHandler(this);
		handler.createMenu(bar);
		addHandler(handler);

//		System.err.println(i18n);

		// create the titlebar handler
		addHandler(new TitleBarHandler(this));

		// create the edit window and init it.
		editor = new Editor(bar, this);
		editor.init();

		// read if we had opened files in the last run of JPE if we did
		// then restart them again.
		Vector vec = getProperties("openfile");
		if (vec != null) {

			// we had opened file so start the openfile handler
			OpenFileHandler ofh = (OpenFileHandler) getOpenFileHandler();
			if (ofh == null) {
				// add the openfile handler to the menu
				ofh = (OpenFileHandler) addHandler(new OpenFileHandler(this));
				ofh.createMenu(bar);
			}

			// now we have the handler open the files
			ofh.openFiles();

			// was one of the files active in the editor ?
			String selfile = getProperty("setup", "selectedfile");
			if (selfile != null) {
				// yes then lets make it active again.
				ofh.switchOpenFile(selfile);
			}
		}

	}

	/**
	* main method needed to start the application
	*/
	public static void main(String[] args) {
		// create a instance of JPE to start the app
		JPE p = new JPE();
	}

	/**
	* return the text handler
	*/
	public Editor getEditor() {
		return (editor);
	}

	/**
	* return the config handler
	*/
	public ConfigHandler getConfigHandler() {
		return (ConfigHandler) getHandler("Config");
	}

	/**
	* return the config handler
	*/
	public TitleBarHandler getTitleBarHandler() {
		return (TitleBarHandler) getHandler("TitleBar");
	}

	public OpenFileHandler getOpenFileHandler() {
		return (OpenFileHandler) getHandler("Opened");
	}

	/**
	* return the requested handler
	*/
	public Handler getHandler(String name) {
		return (Handler) handlers.get(name);
	}

	/**
	* add a new Handler to the application, the must follow the
	* handler interface to be accepted
	*/
	public Handler addHandler(Handler handler) {
		// add it to the list of running handlers
		handlers.put(handler.getName(), handler);
		// return the handler again, caller might wanne use it
		return handler;
	}

	/**
	* return the active menu bar
	*/
	public MenuBar getMenuBar() {
		return bar;
	}

	/**
	* the say commands allows the different parts to give feedback
	* to the user. This call reroutes to the handler that implements
	* it at the moment. no param means display what the app thinks
	* is best to display at this time.
	*/
	public void say() {
		// at the moment hardwired to titlebar handler so give it to
		// that handler
		getTitleBarHandler().say();
	}

	/**
	* the say commands allows the different parts to give feedback
	* to the user. This call reroutes to the handler that implements
	* it at the moment. 
	*/
	public void say(String msg) {
		// at the moment hardwired to titlebar handler so give it to
		// that handler
		getTitleBarHandler().say(msg);
	}

	/**
	* A brief is a temporary say. It lasts n seconds and then 
	* should vanish, putting back the previous message.
	*/
	public void brief(String msg) {
		getTitleBarHandler().say(msg);
	}

	/**
	* get a property from the config handler defined by its
	* nodename and key. Allways returns the value as a string.
	*/
	public String getProperty(String nodename, String key) {
		if (getConfigHandler() != null) {
			// reroute this call to the current config hander
			return (getConfigHandler().getProperty(nodename, key));
		}
		return null;
	}

	/**
	* store a property in the defined node by its given key, replaces
	* the value if it was allready defined.
	*/
	public void putProperty(String nodename, String key, String value) {
		if (getConfigHandler() != null) {
			// reroute this call to the loaded config handler
			getConfigHandler().putProperty(nodename, key, value);
		}
	}

	/**
	* get a properties vector from the config handler defined by its
	* nodename. Allways returns the a vector with hashtables with
	* keys/values.
	*/
	public Vector getProperties(String nodename) {
		if (getConfigHandler() != null) {
			return (getConfigHandler().getProperties(nodename));
		}
		return (null);
	}

	public boolean getAskMode() {
		return (editor.getAskMode());
	}

	public void setAskMode(
		AskInterface askCaller,
		String command,
		String usermsg) {
		editor.setAskMode(askCaller, command, usermsg, false);
	}

	public void setAskMode(
		AskInterface askCaller,
		String command,
		String usermsg,
		boolean oneshot) {
		editor.setAskMode(askCaller, command, usermsg, oneshot);
	}

	/**
	* store a properties Vector, replaces the whole vector if it was
	* allready defined by its nodename so make sure you have merged
	* the nodes yourself if needed !
	*/
	public void putProperties(String nodename, Vector values) {
		if (getConfigHandler() != null) {
			getConfigHandler().putProperties(nodename, values);
		}
	}

	/**
	* return the file path to JPE itself, should be defined in env. variable
	* home.jpe and defaults to c:\JPE
	*/
	public String getMyPath() {
		if (jpehome != null)
			return (jpehome);

		// get defined path
		String cp = System.getProperty("java.class.path");
		StringTokenizer tok = new StringTokenizer(cp, ";\n\r");
		while (tok.hasMoreTokens()) {
			String tmp = tok.nextToken();
			if (tmp.indexOf('?') == -1 && tmp.indexOf(".jar") == -1) {
				try {
					File file =
						new File(tmp + "com/generationjava/apps/jpe/JPE.class");
					if (file.isFile()) {
						jpehome = tmp;
						return tmp;
					}
				} catch (Exception e) {
				}
			} else if (
				tmp.indexOf('?') == -1 && tmp.indexOf("JPE.jar") != -1) {
				try {
					tmp = tmp.substring(0, tmp.length() - 8);
					File file =
						new File(tmp + "com/generationjava/apps/jpe/JPE.class");
					if (file.isFile()) {
						jpehome = tmp;
						return tmp;
					}
				} catch (Exception e) {
				}
			}
		}
		// keep this in just so people can still set it if they really want
		String tmp = System.getProperty("jpe.home");
		if (tmp != null && !tmp.equals("")) {
			// path defined so return it
			return tmp;
		}
		tmp = System.getProperty("user.dir");
		if (tmp != null && !tmp.equals("")) {
			return tmp + "/";
		}
		// path not defined so return default, should never happen
		return "C:\\system\\apps\\jpe";
	}

	public void setCompileState(boolean state) {
		getTitleBarHandler().setCompileState(state);
	}

	public void setSaveState(boolean state) {
		getTitleBarHandler().setSaveState(state);
	}

	public void setLockState(boolean state) {
		getTitleBarHandler().setLockState(state);
		editor.setEditable(!state);
	}

	public String requestConsume(KeyEvent e) {
		if (!e.isControlDown()) {
			return null;
		}
		char c = e.getKeyChar();
		if ((c == '.') || (c == ',')) {
			if ((getOpenFileHandler()).endOfList(c)) {
				return "No more open files in that direction.";
			}
		}
		return null;
	}

	public void requestExit() {
		wantexit = true;
		say("Exit called, goodbye");
		getEditor().dispose();
		System.exit(0);
	}

}
