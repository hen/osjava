package org.cyberiantiger.mudclient.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import org.cyberiantiger.mudclient.net.MudConnection;
import org.cyberiantiger.mudclient.parser.*;
import org.cyberiantiger.mudclient.config.*;
import org.cyberiantiger.console.*;

public class OutputFrame extends JFrame {

    private MudClientUI ui;

    public OutputFrame(MudClientUI ui) {
	super(ui.getConfiguration().getName());
	this.ui = ui;
	initComponents();
    }

    private void initComponents() {
	setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

	getContentPane().setLayout(new BorderLayout());

	getContentPane().add(ui, BorderLayout.CENTER);

	pack();

    }
}
