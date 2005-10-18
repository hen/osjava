/*
 * Created on Oct 10, 2005
 */
package org.osjava.dswiz;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.generationjava.awt.VerticalFlowLayout;

/**
 * @author hyandell
 */
public class LoginPanel extends JPanel {
    
    public LoginPanel(final DataSourceWizard dsw) {
        super();
        
        JLabel loginLabel       = new JLabel("login:");
        JLabel passwordLabel          = new JLabel("password:");
        final JTextField loginInput   = new JTextField(15);
        final JPasswordField passwordInput      = new JPasswordField(15);
        JButton okayButton       = new JButton("Okay");
        JButton cancelButton     = new JButton("Cancel");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okayButton);
        buttonPanel.add(cancelButton);
        
        // by hand layout; as opposed to the commented out auto-grid below
        setLayout(new BorderLayout());
        
        JPanel tmp = new JPanel();
        JPanel tmp1 = new JPanel();
        tmp1.setLayout(new VerticalFlowLayout());
        tmp1.add(loginLabel);
        tmp1.add(passwordLabel);
        tmp.add(tmp1);
        
        JPanel tmp2 = new JPanel();
        tmp2.setLayout(new VerticalFlowLayout());
        tmp2.add(loginInput);
        tmp2.add(passwordInput);
        tmp.add(tmp2);
        
        add(tmp, BorderLayout.CENTER);
        JPanel tmp3 = new JPanel();
        tmp3.setLayout(new BorderLayout());
        tmp3.add(buttonPanel, BorderLayout.EAST);
        add(tmp3, BorderLayout.SOUTH);

//        LayoutHelper.layout(new JComponent[][] {
//                new JComponent[] { loginLabel, loginInput },
//                new JComponent[] { passwordLabel, passwordInput },
//                new JComponent[] { new JBlank(), buttonPanel },
//        }, this);
        
        okayButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dsw.credentialsChosen(loginInput.getText(), passwordInput.getText());
            }
        });

        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dsw.credentialsChoiceCancelled();
            }
        });

    }

}
