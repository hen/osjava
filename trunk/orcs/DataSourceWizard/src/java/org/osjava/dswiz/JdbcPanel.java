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
import javax.swing.JTextField;

import com.generationjava.awt.VerticalFlowLayout;

/**
 * @author hyandell
 */
public class JdbcPanel extends DatabaseChoicePanel {
    
    public JdbcPanel() {
        super();
        
        JLabel driverLabel       = new JLabel("driver:", JLabel.RIGHT);
        JLabel uriLabel          = new JLabel("uri:", JLabel.RIGHT);
        final JTextField driverInput   = new JTextField(15);
        final JTextField uriInput      = new JTextField(15);
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
        tmp1.add(driverLabel);
        tmp1.add(uriLabel);
        tmp.add(tmp1);
        
        JPanel tmp2 = new JPanel();
        tmp2.setLayout(new VerticalFlowLayout());
        tmp2.add(driverInput);
        tmp2.add(uriInput);
        tmp.add(tmp2);
        
        add(tmp, BorderLayout.CENTER);
        JPanel tmp3 = new JPanel();
        tmp3.setLayout(new BorderLayout());
        tmp3.add(buttonPanel, BorderLayout.EAST);
        add(tmp3, BorderLayout.SOUTH);
        
//      LayoutHelper.layout(new JComponent[][] {
//                new JComponent[] { driverLabel, driverInput },
//                new JComponent[] { uriLabel, uriInput },
//                new JComponent[] { new JBlank(), buttonPanel },
//        }, this);
        
        okayButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                notifyDatabaseChosen(driverInput.getText(), uriInput.getText());
            }
        });
        
        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                notifyDatabaseChoiceCancelled();
            }
        });
    }

}
