package org.cyberiantiger.mudclient.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import org.cyberiantiger.console.*;
import org.cyberiantiger.mudclient.MudClient;
import org.cyberiantiger.mudclient.net.*;
import org.cyberiantiger.mudclient.parser.*;

public class MudClientUI extends JPanel implements ConsoleModelListener {

    private ConsoleModel model;
    private ConsoleView view;
    private JScrollBar scrollBar;
    private String viewID;
    private MudClient client;

    public MudClientUI(String viewID) {
	this.viewID = viewID;
	model = new ConsoleModel(80,25,2000);
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

    public String getViewID() {
	return viewID;
    }

    public void consoleChanged() {
	scrollBar.setBlockIncrement(model.getHeight());
	scrollBar.setValues(
		model.getBufferSize(),
		model.getHeight(),
		0,
		model.getHeight() + model.getBufferSize()
		);
	if(client != null) {
	    client.setWindowSize(model.getWidth(),model.getHeight());
	}
    }

    public void consoleAction(ConsoleAction action) {
	view.consoleAction(action);
    }

    public void sendSizeChangeEvents(MudClient client) {
	this.client = client;
    }
}
