/*
 * Created on Oct 14, 2005
 */
package org.osjava.orcs.terminal;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;

/**
 * @author hyandell
 */
public class TestClass extends JFrame {

    public static void main(String[] args) {
        new TestClass().show();
    }
    
    public TestClass() {
        super("TestClass");
        JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        jsp.add(new JTable(new Object[][] { new Object[] { "1", "2" }, new Object[] { "3", "4" } }, new Object[] { "A", "B" }));
        jsp.add(new JTable(new Object[][] { new Object[] { "1", "2" }, new Object[] { "3", "4" } }, new Object[] { "A", "B" }));
        jsp.setResizeWeight(0);
        getContentPane().add(jsp);
        pack();
    }
}
