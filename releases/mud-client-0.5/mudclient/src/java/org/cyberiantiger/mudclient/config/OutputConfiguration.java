package org.cyberiantiger.mudclient.config;

import java.util.Properties;

public class OutputConfiguration {

    private String name;
    private int width;
    private int height;
    private int buffer;
    private boolean floating;
    private boolean visible;
    private boolean visibleOnOutput;
    private boolean keepInput;

    public OutputConfiguration() {
    }

    public void load(Properties props, String name) {
	this.name = name;
	this.width = Integer.parseInt(
		props.getProperty("output."+name+".width","80"));
	this.height = Integer.parseInt(
		props.getProperty("output."+name+".height","25"));
	this.buffer = Integer.parseInt(
		props.getProperty("output."+name+".buffer","2000"));
	this.floating = "yes".equals(
		props.getProperty("output."+name+".float","no"));
	this.visible = "yes".equals(
		props.getProperty("output."+name+".visible","yes"));
	this.visibleOnOutput = "yes".equals(
		props.getProperty("output."+name+".visibleOnOutput","no"));
	this.keepInput = "yes".equals(
		props.getProperty("output."+name+".keepInput","no"));
    }

    public String getName() {
	return name;
    }

    public int getWidth() {
	return width;
    }

    public int getHeight() {
	return height;
    }

    public int getBuffer() {
	return buffer;
    }

    public boolean getFloating() {
	return floating;
    }

    public boolean getVisible() {
	return visible;
    }

    public boolean getVisibleOnOutput() {
	return visibleOnOutput;
    }

    public boolean keepInput() {
	return keepInput;
    }
}
