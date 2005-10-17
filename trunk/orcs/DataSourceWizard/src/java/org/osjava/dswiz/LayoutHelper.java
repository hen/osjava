/*
 * Created on Oct 10, 2005
 */
package org.osjava.dswiz;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import com.generationjava.awt.VerticalFlowLayout;

/**
 * @author hyandell
 */
public class LayoutHelper {

    public static void layout(JComponent[][] grid, JComponent panel) {
        gridBagLayout(grid, panel);
    }
    
    public static void flowLayout(JComponent[][] grid, JComponent panel) {
        panel.setLayout(new VerticalFlowLayout());
        
        for(int i=0; i<grid.length; i++) {
            JPanel tmp = new JPanel();
            for(int j=0; j<grid[i].length; j++) {
                tmp.add(grid[i][j]);
            }
            panel.add(tmp);
        }
    }
    
    public static void springLayout(JComponent[][] grid, JComponent panel) {
        int distance = 5;
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);
        
        for(int i=0; i<grid.length; i++) {
            for(int j=0; j<grid[i].length; j++) {
                JComponent jc    = grid[i][j];
                panel.add(jc);

                JComponent north          = panel;
                String     northDirection = SpringLayout.NORTH; 
                JComponent east           = panel; 
                String     eastDirection  = SpringLayout.EAST; 
                JComponent south          = panel; 
                String     southDirection = SpringLayout.SOUTH; 
                JComponent west           = panel; 
                String     westDirection  = SpringLayout.WEST; 
                
                // handle edge cases
                if(j != 0) {
                    north = grid[i][j-1];
                    northDirection = SpringLayout.SOUTH; 
                }
                if(i < grid.length - 1) {
                    east  = grid[i+1][j];
                    eastDirection = SpringLayout.WEST; 
                }
                if(j < grid[i].length - 1) {
                    south = grid[i][j+1]; 
                    southDirection = SpringLayout.NORTH; 
                }
                if(i != 0) {
                    west  = grid[i-1][j];
                    westDirection = SpringLayout.EAST; 
                }
                layout.putConstraint(SpringLayout.NORTH, jc, distance, northDirection, north);
                layout.putConstraint(SpringLayout.EAST,  jc, distance, eastDirection,  east);
                layout.putConstraint(SpringLayout.SOUTH, jc, distance, southDirection, south);
                layout.putConstraint(SpringLayout.WEST,  jc, distance, westDirection,  west);
            }
        }
    }
    
    public static void gridBagLayout(JComponent[][] grid, JComponent panel) {
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        
        for(int i=0; i<grid.length; i++) {
            for(int j=0; j<grid[i].length; j++) {
                if(j == grid[i].length - 1) {
                    c.gridwidth = GridBagConstraints.REMAINDER;
                } else {
                    c.gridwidth = GridBagConstraints.RELATIVE;
                }
                gridbag.setConstraints(grid[i][j], c);
                panel.add(grid[i][j]);
            }
        }
        
        panel.setLayout(gridbag);
    }
        
    public static void gridLayout(JComponent[][] grid, JComponent panel) {
        panel.setLayout(new GridLayout(grid.length, grid[0].length));
        for(int i=0; i<grid.length; i++) {
            for(int j=0; j<grid[i].length; j++) {
                panel.add(grid[i][j]);
            }
        }
    }

}
