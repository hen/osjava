package org.cyberiantiger.mudclient.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Color;
import java.awt.Font;
import java.awt.AWTEvent;
import java.awt.event.*;
import java.awt.EventQueue;
import java.awt.Toolkit;
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
    private JMenuItem optionsMenuItem;
    private JMenuItem exitMenuItem;
    private JMenuItem aboutMenuItem;
    private JTabbedPane tabbedOutputPane;
    private JTextField inputField;
    private OptionsDialog options;
    private AboutDialog about;
    private Map views = new HashMap();
    private ConsoleWriter defaultView;
    private List otherFrames = new ArrayList();
    private EQ eq = new EQ();


    public ControlWindow(Connection client) {
	super("Elephant Mud Client");
	this.client = client;
        installEventQueue();
	initComponents();
        options = new OptionsDialog(this);
        about = new AboutDialog(this);
    }

    private void installEventQueue() {
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(eq);
    }

    private void initComponents() {
	menuBar = new JMenuBar();
	fileMenu = new JMenu("File");
	helpMenu = new JMenu("Help");
	connectMenuItem = new JMenuItem("Connect");
	disconnectMenuItem= new JMenuItem("Disconnect");
        optionsMenuItem = new JMenuItem("Options");
	exitMenuItem = new JMenuItem("Exit");
	aboutMenuItem = new JMenuItem("About");

	menuBar.add(fileMenu);
	menuBar.add(helpMenu);

	fileMenu.add(connectMenuItem);
	fileMenu.add(disconnectMenuItem);
        fileMenu.add(optionsMenuItem);
	fileMenu.add(exitMenuItem);

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
        optionsMenuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        options.show();
                    }
                });
	exitMenuItem.addActionListener(
		new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			exit();
		    }
		});
	aboutMenuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        about.show();
                    }
                });
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
	inputField.setFont(new Font("Monospaced",Font.PLAIN,14));

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

        List macros = client.getConfiguration().getMacros();
        {
            for (int i = 0; i < macros.size(); i++) {
                actionMap.put("macro" + i, 
                        new MacroAction((String)macros.get(i)));
            }
        }

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
        options.hide();
        options.dispose();
	hide();
	dispose();
        client.exit();
    }

    public ClientConfiguration getConfiguration() {
        return client.getConfiguration();
    }

    private class MacroAction extends AbstractAction {

        private String cmd;

        public MacroAction(String cmd) {
            this.cmd = cmd;
        }

        public void actionPerformed(ActionEvent e) {
            eq.dropSubsequentKeyTyped();
            client.command(getCurrentViewName(), cmd);
        }

    }

    private static class EQ extends EventQueue {
        /* XXX: Evil hack to stop macros from generating a key code 
         * Check event queue and if the next event in the queue is 
         * a key typed event, drop it, This may not work on some
         * platforms, it assumes that the key typed event immediately
         * follows the key pressed event. Some platforms may send
         * key typed events before key pressed events, or a bunch
         * of other circumstances which might break this. */
        private volatile boolean flag = false;
        public void dropSubsequentKeyTyped() {
            flag = true;
        }
        protected void dispatchEvent(AWTEvent e) {
            if (flag) {
                switch (e.getID()) {
                    case KeyEvent.KEY_TYPED:
                        flag = false;
                        return;
                    case KeyEvent.KEY_PRESSED:
                        flag = false;
                }
            }
            super.dispatchEvent(e);
        }
    }
}
