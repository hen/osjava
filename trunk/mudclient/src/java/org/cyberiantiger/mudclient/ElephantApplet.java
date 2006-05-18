package org.cyberiantiger.mudclient;

import java.applet.Applet;

import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.AWTEvent;

import java.lang.reflect.Method;

import java.net.URL;
import java.net.MalformedURLException;

import java.util.StringTokenizer;

public class ElephantApplet extends Applet {

    public void init() {
	setLayout(new BorderLayout());
	if(checkVersion(System.getProperty("java.version"))) {
	    add(new LaunchButton(), BorderLayout.CENTER);
	} else {
	    add(new GetJavaButton(), BorderLayout.CENTER);
	}
    }

    private boolean checkVersion(String version) {
	StringTokenizer tokens = new StringTokenizer(version, ".");

	if (!tokens.hasMoreTokens() || !"1".equals(tokens.nextToken()))
	    return false;

	if (!tokens.hasMoreTokens() || Integer.parseInt(tokens.nextToken()) < 4)
	    return false;

	return true;
    }

    private class GetJavaButton extends Button {

	public GetJavaButton() {
	    super("Get Java");
	    enableEvents(AWTEvent.ACTION_EVENT_MASK);
	}

	protected void processActionEvent(ActionEvent ae) {
	    try {
		getAppletContext().showDocument(
			new URL("http://www.java.com/")
			);
	    } catch (MalformedURLException mue) {
	    }
	}
    }

    private class LaunchButton extends Button {

	public LaunchButton() {
	    super("Launch Mudclient");
	    enableEvents(AWTEvent.ACTION_EVENT_MASK);
	}

	protected void processActionEvent(ActionEvent ae) {
	    try {
		Class clazz = Class.forName("org.cyberiantiger.mudclient.ElephantMudClient");
		Method m = clazz.getMethod("main", new Class[] { String[].class });
		m.invoke(null, new Object[] { new String[0] });
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }
}
