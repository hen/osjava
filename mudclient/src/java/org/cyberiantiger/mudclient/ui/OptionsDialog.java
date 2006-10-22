package org.cyberiantiger.mudclient.ui;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import org.cyberiantiger.mudclient.config.ClientConfiguration;

public class OptionsDialog extends JDialog {

    private ControlWindow ctrl;
    private JLabel hostLabel;
    private JLabel portLabel;
    private JLabel proxyHostLabel;
    private JLabel proxyPortLabel;
    private JLabel useProxyLabel;
    private JLabel useJvmProxyLabel;
    private JTextField host;
    private JTextField port;
    private JTextField proxyHost;
    private JTextField proxyPort;
    private JCheckBox useProxy;
    private JCheckBox useJvmProxy;
    private JButton save;
    private JPanel formPanel;
    private JPanel buttonPanel;

    public OptionsDialog(ControlWindow ctrl) {
        super(ctrl, "Options", true);
        this.ctrl = ctrl;
        initComponents();
    }

    private void initComponents() {
        hostLabel = new JLabel("Address");
        portLabel = new JLabel("Port");
        proxyHostLabel = new JLabel("Proxy address");
        proxyPortLabel = new JLabel("Proxy port");
        useProxyLabel = new JLabel("Use proxy");
        useJvmProxyLabel = new JLabel("Use default proxy");
        host = new JTextField(20);
        port = new JTextField(20);
        proxyHost = new JTextField(20);
        proxyPort = new JTextField(20);
        useProxy = new JCheckBox();
        useJvmProxy = new JCheckBox();
        save = new JButton("Save");
        formPanel = new JPanel();
        buttonPanel = new JPanel();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(formPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        formPanel.setLayout(new SpringLayout());
        buttonPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));

        buttonPanel.add(save);

        formPanel.add(hostLabel);
        formPanel.add(host);
        formPanel.add(portLabel);
        formPanel.add(port);
        formPanel.add(useProxyLabel);
        formPanel.add(useProxy);
        formPanel.add(useJvmProxyLabel);
        formPanel.add(useJvmProxy);
        formPanel.add(proxyHostLabel);
        formPanel.add(proxyHost);
        formPanel.add(proxyPortLabel);
        formPanel.add(proxyPort);

        SpringUtilities.makeCompactGrid(formPanel, 6, 2, 5, 5, 5, 5);

        pack();

        save.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        hide();
                        saveConfig();
                    }
                });
        useProxy.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        updateEnabled();
                    }
                });
        useJvmProxy.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ClientConfiguration config = ctrl.getConfiguration();
                        if (useJvmProxy.isSelected()) {
                            proxyHost.setText(config.getJvmProxyHost());
                            proxyPort.setText("" + config.getJvmProxyPort());
                        } else {
                            proxyHost.setText(config.getProxyHost());
                            proxyPort.setText("" + config.getProxyPort());
                        }
                        updateEnabled();
                    }
                });
        DocumentListener updateListener = 
            new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    updateEnabled();
                }
                public void removeUpdate(DocumentEvent e) {
                    updateEnabled();
                }
                public void insertUpdate(DocumentEvent e) {
                    updateEnabled();
                }
            };
        host.getDocument().addDocumentListener(updateListener);
        port.getDocument().addDocumentListener(updateListener);
        proxyHost.getDocument().addDocumentListener(updateListener);
        proxyPort.getDocument().addDocumentListener(updateListener);

        loadConfig();
    }

    private void updateEnabled() {
        if (useProxy.isSelected()) {
            useJvmProxy.setEnabled(true);
            boolean tmp = !useJvmProxy.isSelected();
            proxyHost.setEnabled(tmp);
            proxyPort.setEnabled(tmp);
        } else {
            useJvmProxy.setEnabled(false);
            proxyHost.setEnabled(false);
            proxyPort.setEnabled(false);
        }
        save.setEnabled(formValid());
    }

    private boolean formValid() {
        if (host.getText().length() == 0) {
            return false;
        }
        if (port.getText().length() == 0) {
            return false;
        }
        try {
            int p = Integer.parseInt(port.getText());
            if (p < 1 || p > 65535) {
                return false;
            }
        } catch (NumberFormatException nfe) {
            return false;
        }
        if (useProxy.isSelected()) {
            if (proxyHost.getText().length() == 0) {
                return false;
            }
            if (proxyPort.getText().length() == 0) {
                return false;
            }
            try {
                int p = Integer.parseInt(proxyPort.getText());
                if (p < 1 || p > 65535) {
                    return false;
                }
            } catch (NumberFormatException nfe) {
                return false;
            }
        }
        return true;
    }
    
    protected void loadConfig() {
        ClientConfiguration config = ctrl.getConfiguration();
        host.setText(config.getHost());
        port.setText("" + config.getPort());
        useProxy.setSelected(config.getUseProxy());
        useJvmProxy.setSelected(config.getUseJvmProxy());
        proxyHost.setText(config.getUseJvmProxy() ? config.getJvmProxyHost() : config.getProxyHost());
        proxyPort.setText("" + (config.getUseJvmProxy() ? config.getJvmProxyPort() : config.getProxyPort()));
        updateEnabled();
    }

    protected void saveConfig() {
        ClientConfiguration config = ctrl.getConfiguration();
        config.setHost(host.getText());
        config.setPort(Integer.parseInt(port.getText()));
        config.setUseProxy(useProxy.isSelected());
        config.setUseJvmProxy(useJvmProxy.isSelected());
        if (!useJvmProxy.isSelected()) {
            config.setProxyHost(proxyHost.getText());
            config.setProxyPort(Integer.parseInt(proxyPort.getText()));
        }
    }
}
