/*
 * Created on Oct 10, 2005
 */
package org.osjava.dswiz.oracle;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.osjava.dswiz.DatabaseChoicePanel;

import com.generationjava.awt.VerticalFlowLayout;

/**
 * TODO: Switch the Type choice to a Radio Button
 * 
 * @author hyandell
 */
public class OraclePanel extends DatabaseChoicePanel {
    
    private static final String THIN = "thin";
    private static final String OCI = "oci";
    
    public OraclePanel() {
        super();
        
        TnsName[] tnsNames = null;
        try {
            tnsNames = TnsNamesParser.parseTnsNames(TnsNamesParser.getDefaultFile());
        } catch(IOException ioe) {
            throw new RuntimeException("Unable to parse default TNS names file");
        }
        
        final JTextField serviceInput      = new JTextField(15);
        final JTextField hostInput      = new JTextField(15);
        final JTextField sidInput      = new JTextField(15);
        final JTextField portInput      = new JTextField(15);
        final JCheckBox byHandCheckBox = new JCheckBox("manually");       
        final JComboBox typeDropDown      = new JComboBox(new Object[] { THIN, OCI } );
        final JComboBox serviceDropDown = new JComboBox(tnsNames);
        JButton okayButton       = new JButton("Okay");
        JButton cancelButton     = new JButton("Cancel");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okayButton);
        buttonPanel.add(cancelButton);
        
        byHandCheckBox.setSelected(true);
        byHandCheckBox.setEnabled(false);
        serviceInput.setEditable(false);
        
        // by hand layout; as opposed to the commented out auto-grid below
        setLayout(new BorderLayout());
        
        JPanel tmp = new JPanel();
        tmp.setLayout(new BorderLayout());
        
        JPanel tmpL = new JPanel();
        tmpL.setLayout(new BorderLayout());
        tmpL.add(byHandCheckBox, BorderLayout.NORTH);
        JPanel tmpLL = new JPanel();
        tmpLL.setLayout(new VerticalFlowLayout());
        tmpLL.add(new JLabel("service:", JLabel.RIGHT));
        tmpLL.add(new JLabel("host:", JLabel.RIGHT));
        tmpLL.add(new JLabel("sid:", JLabel.RIGHT));
        tmpLL.add(new JLabel("port:", JLabel.RIGHT));
        tmpL.add(tmpLL, BorderLayout.WEST);

        JPanel tmpLR= new JPanel();
        tmpLR.setLayout(new VerticalFlowLayout());
        tmpLR.add(serviceInput);
        tmpLR.add(hostInput);
        tmpLR.add(sidInput);
        tmpLR.add(portInput);
        tmpL.add(tmpLR, BorderLayout.EAST);

        
        tmp.add(tmpL, BorderLayout.WEST);
        
        JPanel tmpR = new JPanel();
        tmpR.setLayout(new BorderLayout());
        JPanel tmpTR = new JPanel();
        tmpTR.setLayout(new BorderLayout());
        JPanel tmpNE = new JPanel();
        tmpNE.setLayout(new BorderLayout());
        tmpNE.add(new JLabel("type:", JLabel.RIGHT), BorderLayout.WEST);
        tmpNE.add(typeDropDown, BorderLayout.EAST);
        tmpTR.add(tmpNE, BorderLayout.NORTH);
        JPanel tmpCE = new JPanel();
        tmpCE.setLayout(new BorderLayout());
        tmpCE.add(new JLabel("TNS:", JLabel.RIGHT), BorderLayout.WEST);
        tmpCE.add(serviceDropDown, BorderLayout.EAST);
        tmpTR.add(tmpCE, BorderLayout.SOUTH);
        tmpR.add(tmpTR, BorderLayout.NORTH);
        
        tmp.add(tmpR, BorderLayout.EAST);
        
        add(tmp, BorderLayout.CENTER);

        JPanel tmp3 = new JPanel();
        tmp3.setLayout(new BorderLayout());
        tmp3.add(buttonPanel, BorderLayout.EAST);
        add(tmp3, BorderLayout.SOUTH);

//        LayoutHelper.layout(new JComponent[][] {
//                new JComponent[] { byHandCheckBox, new JBlank(), typeLabel, typeDropDown, },
//                new JComponent[] { serviceLabel, serviceInput, , serviceDropDown },
//                new JComponent[] { hostLabel, hostInput, new JBlank(), new JBlank() },
//                new JComponent[] { sidLabel, sidInput, new JBlank(), new JBlank() },
//                new JComponent[] { portLabel, portInput, new JBlank(), new JBlank() },
//                new JComponent[] { new JBlank(), new JBlank(), new JBlank(), buttonPanel },
//        }, this);
        
        typeDropDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JComboBox box = (JComboBox) event.getSource();
                String type = (String) box.getSelectedItem();
                if(byHandCheckBox.isSelected()) {
                    if(type.equals(THIN)) {
                        hostInput.setEditable(true);
                        sidInput.setEditable(true);
                        portInput.setEditable(true);
                        serviceInput.setEditable(false);
                    } else {
                        hostInput.setEditable(false);
                        sidInput.setEditable(false);
                        portInput.setEditable(false);
                        serviceInput.setEditable(true);
                    }
                }
            } 
        });      
        
        serviceDropDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JComboBox box = (JComboBox) event.getSource();
                TnsName tnsName = (TnsName) box.getSelectedItem();
                serviceInput.setText(tnsName.getName());
                serviceInput.setEditable(false);
                hostInput.setText(tnsName.getHost());
                hostInput.setEditable(false);
                sidInput.setText(tnsName.getSid());
                sidInput.setEditable(false);
                portInput.setText(tnsName.getPort());
                portInput.setEditable(false);
                byHandCheckBox.setSelected(false);
                byHandCheckBox.setEnabled(true);
            }
        });   
        
        byHandCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JCheckBox box = (JCheckBox) event.getSource();
                box.setEnabled(false);
                if(byHandCheckBox.isSelected()) {
                    if(typeDropDown.getSelectedItem().equals(THIN)) {
                        hostInput.setEditable(true);
                        sidInput.setEditable(true);
                        portInput.setEditable(true);
                        serviceInput.setEditable(false);
                    } else {
                        hostInput.setEditable(false);
                        sidInput.setEditable(false);
                        portInput.setEditable(false);
                        serviceInput.setEditable(true);
                    }
                }
            }
        });
        
        okayButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String type = (String) typeDropDown.getSelectedItem();
                String uri = "jdbc:oracle:" + type + ":@"; 
                if(type.equals(THIN)) {
                    uri = uri + hostInput.getText()+":"+portInput.getText()+":"+sidInput.getText();
                } else {
                    uri = uri + serviceInput.getText();
                }
                
                notifyDatabaseChosen("oracle.jdbc.driver.OracleDriver", uri);
            }
        });

        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                notifyDatabaseChoiceCancelled();
            }
        });

    }

}
