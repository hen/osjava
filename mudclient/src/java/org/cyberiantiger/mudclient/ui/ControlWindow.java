package org.cyberiantiger.mudclient.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Color;
import java.awt.Font;
import java.awt.AWTEvent;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import org.cyberiantiger.mudclient.net.MudConnection;
import org.cyberiantiger.mudclient.parser.*;
import org.cyberiantiger.mudclient.config.*;
import org.cyberiantiger.console.*;

public class ControlWindow extends JFrame {

    private Connection client;

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu helpMenu;
    private JMenuItem connectMenuItem;
    private JMenuItem disconnectMenuItem;
    private JMenuItem exitMenuItem;
    private JMenuItem helpMenuItem;
    private JMenuItem aboutMenuItem;
    private JTabbedPane tabbedOutputPane;
    private JTextField inputField;
    private Map views = new HashMap();
    private ConsoleWriter defaultView;
    private List otherFrames = new ArrayList();

    public ControlWindow(Connection client) {
	super("Elephant Mud Client");
	this.client = client;
	initComponents();
    }

    private void initComponents() {
	menuBar = new JMenuBar();
	fileMenu = new JMenu("File");
	helpMenu = new JMenu("Help");
	connectMenuItem = new JMenuItem("Connect");
	disconnectMenuItem= new JMenuItem("Disconnect");
	exitMenuItem = new JMenuItem("Exit");
	helpMenuItem = new JMenuItem("Help");
	aboutMenuItem = new JMenuItem("About");

	menuBar.add(fileMenu);
	menuBar.add(helpMenu);

	fileMenu.add(connectMenuItem);
	fileMenu.add(disconnectMenuItem);
	fileMenu.add(exitMenuItem);

	helpMenu.add(helpMenuItem);
	helpMenu.add(aboutMenuItem);

	connectMenuItem.addActionListener(
		new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			client.connect();
		    }
		});
	disconnectMenuItem.setEnabled(false);
	disconnectMenuItem.addActionListener(
		new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			client.disconnect();
		    }
		});
	exitMenuItem.addActionListener(
		new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			exit();
		    }
		});
	helpMenuItem.setEnabled(false);
	aboutMenuItem.setEnabled(false);
	setJMenuBar(menuBar);

	getContentPane().setLayout(new BorderLayout());
	tabbedOutputPane = new JTabbedPane(
		JTabbedPane.BOTTOM,
		JTabbedPane.WRAP_TAB_LAYOUT
		);
	tabbedOutputPane.addChangeListener(
		new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
			tabbedOutputPane.setForegroundAt(
			    tabbedOutputPane.getSelectedIndex(),
			    Color.black
			    );
		    }
		}
		);
	getContentPane().add(tabbedOutputPane,BorderLayout.CENTER);

	inputField = new JTextField();
	inputField.setFont(new Font("monospaced",Font.PLAIN,14));

	ActionMap actionMap = new ActionMap();

	actionMap.put("sendCommand",new AbstractAction() {
	    public void actionPerformed(ActionEvent e) {
		client.command(
		    getCurrentViewName(), 
		    inputField.getText()
		    );
		if(
		    client.getConfiguration().
		    getOutputConfiguration(
			getCurrentViewName()
			).keepInput()
		  )
		{
		    inputField.selectAll();
		} else {
		    inputField.setText("");
		}

	    }
	});
	actionMap.put("scrollUpLine",new AbstractAction() {
	    public void actionPerformed(ActionEvent e) {
		((MudClientUI)getCurrentView()).lineUp();
	    }
	});
	actionMap.put("scrollDownLine",new AbstractAction() {
	    public void actionPerformed(ActionEvent e) {
		((MudClientUI)getCurrentView()).lineDown();
	    }
	});
	actionMap.put("scrollUpPage",new AbstractAction() {
	    public void actionPerformed(ActionEvent e) {
		((MudClientUI)getCurrentView()).pageUp();
	    }
	});
	actionMap.put("scrollDownPage",new AbstractAction() {
	    public void actionPerformed(ActionEvent e) {
		((MudClientUI)getCurrentView()).pageDown();
	    }
	});
	actionMap.put("outputLeft",new AbstractAction() {
	    public void actionPerformed(ActionEvent e) {
		int i = tabbedOutputPane.getSelectedIndex();
		if(i == 0) {
		    i = tabbedOutputPane.getTabCount();
		}
		tabbedOutputPane.setSelectedIndex(i-1);
	    }
	});
	actionMap.put("outputRight",new AbstractAction() {
	    public void actionPerformed(ActionEvent e) {
		int i = tabbedOutputPane.getSelectedIndex();
		i++;
		if(i == tabbedOutputPane.getTabCount()) {
		    i = 0;
		}
		tabbedOutputPane.setSelectedIndex(i);
	    }
	});

	InputMap inputMap = client.getConfiguration().getKeyBindings();
	inputMap.setParent(inputField.getInputMap());

	inputField.setInputMap(
	    JComponent.WHEN_FOCUSED, 
	    inputMap
	    );
	
	getContentPane().add(inputField,BorderLayout.SOUTH);

	java.util.List outputs = client.getConfiguration().getOutputNames();
	Iterator i = outputs.iterator();
	while(i.hasNext()) {
	    final String outputName = (String) i.next();
	    createView(outputName);
	    actionMap.put("output"+outputName,
		    new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
			    int idx = tabbedOutputPane.indexOfTab(outputName);
			    if(idx != -1) {
				tabbedOutputPane.setSelectedIndex(idx);
			    }
			}
		    });
	}
	actionMap.setParent(inputField.getActionMap());
	inputField.setActionMap(actionMap);

	defaultView = getView(
		client.getConfiguration().getDefaultOutputName()
		    );

	((MudClientUI)defaultView).setConnection(client);


	pack();

	enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    }

    public ConsoleWriter createView(final String id) {
	ConsoleWriter ui = getView(id);
	if(ui != null) {
	    return ui;
	} else {
	    OutputConfiguration config = 
	    client.getConfiguration().getOutputConfiguration(id);

	    if(config != null) {
		ui = new MudClientUI(this,config);
		views.put(id,ui);

		if(config.getVisible()) {
		    makeVisible((MudClientUI)ui);
		}

		return ui;
	    } else {
		return null;
	    }
	}
    }

    public void processWindowEvent(WindowEvent we) {
	super.processWindowEvent(we);
	if(we.getID() == WindowEvent.WINDOW_CLOSING) {
	    exit();
	}
	if(we.getID() == WindowEvent.WINDOW_ACTIVATED) {
	    inputField.requestFocus();
	}
    }

    public ConsoleWriter getView(String id) {
	return (ConsoleWriter) views.get(id);
    }

    public ConsoleWriter getDefaultView() {
	return defaultView;
    }

    public ConsoleWriter getCurrentView() {
	return (ConsoleWriter) tabbedOutputPane.getSelectedComponent();
    }

    public String getCurrentViewName() {
	return tabbedOutputPane.getTitleAt(
		tabbedOutputPane.getSelectedIndex()
		);
    }

    public void viewUpdated(MudClientUI ui) {
	if(ui != tabbedOutputPane.getSelectedComponent()) {
	    int i = tabbedOutputPane.indexOfComponent(ui);
	    if(i>=0) {
		tabbedOutputPane.setForegroundAt(i, Color.red);
	    }
	}
    }

    public void makeVisible(MudClientUI ui) {
	OutputConfiguration config = ui.getConfiguration();
	if(config.getFloating()) {
	    OutputFrame out = new OutputFrame(ui);
	    otherFrames.add(out);
	    out.show();
	} else {
	    tabbedOutputPane.addTab(config.getName(), (Component) ui);
	}
    }

    public void connectionStatusChanged(int newStatus) {
	switch(newStatus) {
	    case MudConnection.CONNECTED:
		disconnectMenuItem.setEnabled(true);
		connectMenuItem.setEnabled(false);
		break;
	    case MudConnection.CONNECTING:
	    case MudConnection.DISCONNECTING:
		disconnectMenuItem.setEnabled(false);
		connectMenuItem.setEnabled(false);
		break;
	    case MudConnection.DISCONNECTED:
		disconnectMenuItem.setEnabled(false);
		connectMenuItem.setEnabled(true);
		break;
	}
    }

    protected void exit() {
	client.disconnect();
	Iterator i = otherFrames.iterator();
	while(i.hasNext()) {
	    JFrame other = (JFrame) i.next();
	    other.hide();
	    other.dispose();
	}
	hide();
	dispose();
    }
}
