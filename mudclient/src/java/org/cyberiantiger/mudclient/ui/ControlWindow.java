package org.cyberiantiger.mudclient.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import org.cyberiantiger.mudclient.MudClient;
import org.cyberiantiger.mudclient.parser.*;
import org.cyberiantiger.mudclient.net.MudConnection;
import org.cyberiantiger.console.*;

public class ControlWindow extends JFrame {

    public static final String DEFAULT_OUTPUT_NAME = "Main";

    private MudClient client;

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu emulationMenu;
    private JMenu helpMenu;
    private JMenuItem connectMenuItem;
    private JMenuItem disconnectMenuItem;
    private JMenuItem exitMenuItem;
    private JMenuItem ansiMenuItem;
    private JMenuItem externalMenuItem;
    private JMenuItem helpMenuItem;
    private JMenuItem aboutMenuItem;
    private JTabbedPane tabbedOutputPane;
    private JTextField inputField;

    private MudClientUI defaultOutput;
    private Map customOutput = new HashMap();
    private Map views = new HashMap();

    public ControlWindow(MudClient client) {
	super("Elephant Mud Client");
	this.client = client;
	initComponents();
    }

    private void initComponents() {
	setDefaultCloseOperation(EXIT_ON_CLOSE);

	menuBar = new JMenuBar();
	fileMenu = new JMenu("File");
	emulationMenu = new JMenu("Emulation");
	helpMenu = new JMenu("Help");
	connectMenuItem = new JMenuItem("Connect");
	disconnectMenuItem= new JMenuItem("Disconnect");
	exitMenuItem = new JMenuItem("Exit");
	ansiMenuItem = new JMenuItem("Ansi");
	externalMenuItem = new JMenuItem("External Client");
	helpMenuItem = new JMenuItem("Help");
	aboutMenuItem = new JMenuItem("About");

	menuBar.add(fileMenu);
	menuBar.add(emulationMenu);
	menuBar.add(helpMenu);

	fileMenu.add(connectMenuItem);
	fileMenu.add(disconnectMenuItem);
	fileMenu.add(exitMenuItem);

	emulationMenu.add(ansiMenuItem);
	emulationMenu.add(externalMenuItem);

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
			client.exit();
		    }
		});
	ansiMenuItem.addActionListener(
		new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			client.setParser(new ANSIParser());
		    }
		});
	externalMenuItem.addActionListener(
		new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			client.setParser(new ElephantMUDParser());
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
	inputField.addKeyListener(
		new KeyListener() {
		    public void keyPressed(KeyEvent e) {}
		    public void keyReleased(KeyEvent e) {}
		    public void keyTyped(KeyEvent e) {
			// Hack, should use getKeyCode(), but for some
			// reason java swallows it.
			if(e.getKeyChar() == '\n') {
			    client.command(
				getSelectedView().getViewID(), 
				inputField.getText()
				);
			    inputField.selectAll();
			}
		    }
		});
	
	getContentPane().add(inputField,BorderLayout.SOUTH);

	defaultOutput = createView(DEFAULT_OUTPUT_NAME);
	defaultOutput.sendSizeChangeEvents(client);

	pack();

	enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    }

    public MudClientUI createView(String id) {
	MudClientUI ui = getView(id);
	if(ui != null) {
	    return ui;
	} else {
	    ui = new MudClientUI(id);
	    tabbedOutputPane.addTab(id, ui);
	    views.put(id,ui);
	    return ui;
	}
    }

    public void deleteView(String id) {
    }

    public MudClientUI getView(String viewId) {
	return (MudClientUI) views.get(viewId);
    }

    public MudClientUI getSelectedView() {
	return (MudClientUI)tabbedOutputPane.getSelectedComponent();
    }

    public void processWindowEvent(WindowEvent we) {
	super.processWindowEvent(we);
	if(we.getID() == WindowEvent.WINDOW_CLOSING) {
	    client.exit();
	}
    }

    public void consoleAction(ConsoleAction action) {
	if(action instanceof ElephantMUDConsoleAction) {
	    ElephantMUDConsoleAction eAction = 
	    (ElephantMUDConsoleAction) action;

	    String tmp = eAction.getPrimaryClass();

	    // First check if there's an existing window to grab this message.
	    String outputName;
	    MudClientUI destination;

	    if((destination = (MudClientUI)customOutput.get(tmp) ) == null) {
		if((outputName = client.getCustomOutputName(tmp)) == null) {
		    outputName = DEFAULT_OUTPUT_NAME;
		    destination = defaultOutput;
		} else {
		    destination = createView(outputName);
		    java.util.List types = client.getCustomOutputTypes(outputName);
		    Iterator i = types.iterator();
		    while(i.hasNext()) {
			customOutput.put(i.next(), destination);
		    }
		}
	    } else {
		outputName = client.getCustomOutputName(tmp);
	    }
	    if(!outputName.equals(
			tabbedOutputPane.getTitleAt(
			    tabbedOutputPane.getSelectedIndex()
			    )
			)
	      ) 
	    {
		tabbedOutputPane.setForegroundAt(
			tabbedOutputPane.indexOfTab(outputName), 
			Color.red
			);
	    }
	    destination.consoleAction(action);
	} else {
	    defaultOutput.consoleAction(action);
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
}
