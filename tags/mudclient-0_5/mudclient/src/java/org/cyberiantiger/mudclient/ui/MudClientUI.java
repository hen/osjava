package org.cyberiantiger.mudclient.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import org.cyberiantiger.console.*;
import org.cyberiantiger.mudclient.net.*;
import org.cyberiantiger.mudclient.config.*;
import org.cyberiantiger.mudclient.parser.*;

public class MudClientUI extends JPanel 
implements ConsoleModelListener, ConsoleWriter {

    private ConsoleModel model;
    private ConsoleView view;
    private JScrollBar scrollBar;
    private ControlWindow control;
    private OutputConfiguration config;

    public MudClientUI(ControlWindow control,OutputConfiguration config) {
	this.control = control;
	this.config = config;
	model = new ConsoleModel(
		config.getWidth(),
		config.getHeight(),
		config.getBuffer()
		);
	view = new ConsoleView(model);
	scrollBar = new JScrollBar(JScrollBar.VERTICAL);
	
	model.addView(this);

	scrollBar.addAdjustmentListener(
		new AdjustmentListener() {
		    public void adjustmentValueChanged(AdjustmentEvent ae) {
			model.setLineOffset(scrollBar.getValue());
			view.repaint();
		    }
		});

	setLayout(new BorderLayout());
	add(BorderLayout.CENTER,view);
	add(BorderLayout.EAST,scrollBar);
    }

    public void consoleChanged() {
	scrollBar.setBlockIncrement(model.getHeight());
	scrollBar.setValues(
		model.getLineOffset(),
		model.getHeight(),
		0,
		model.getHeight() + model.getBufferSize()
		);
    }

    public void consoleAction(ConsoleAction action) {
	view.consoleAction(action);
	if(getParent() == null && config.getVisibleOnOutput()) {
	    control.makeVisible(this);
	}
	control.viewUpdated(this);
    }

    public void setConnection(Connection connection) {
	view.setConnection(connection);
    }

    public OutputConfiguration getConfiguration() {
	return config;
    }

    public void lineUp() {
	model.setLineOffset(model.getLineOffset()-1);
	consoleChanged();
	view.repaint();
    }

    public void lineDown() {
	model.setLineOffset(model.getLineOffset()+1);
	consoleChanged();
	view.repaint();
    }

    public void pageUp() {
	model.setLineOffset(model.getLineOffset() - model.getHeight());
	consoleChanged();
	view.repaint();
    }

    public void pageDown() {
	model.setLineOffset(model.getLineOffset() + model.getHeight());
	consoleChanged();
	view.repaint();
    }
}
