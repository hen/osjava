package org.cyberiantiger.mudclient.ui;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import org.cyberiantiger.mudclient.config.ClientConfiguration;

public class AboutDialog extends JDialog {

    private ImageIcon icon;
    private JLabel logoLabel;

    public AboutDialog(ControlWindow ctrl) {
        super(ctrl, "About", false);
        initComponents();
    }

    private void initComponents() {
        icon = new ImageIcon(
                AboutDialog.class.getClassLoader().getResource(
                    "org/cyberiantiger/mudclient/about.gif"
                    ));
        logoLabel = new JLabel(icon);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(logoLabel, BorderLayout.CENTER);

        pack();
    }
}
