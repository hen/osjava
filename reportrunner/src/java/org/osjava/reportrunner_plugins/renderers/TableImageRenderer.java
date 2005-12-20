package org.osjava.reportrunner_plugins.renderers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.osjava.reportrunner.AbstractRenderer;
import org.osjava.reportrunner.Column;
import org.osjava.reportrunner.Report;
import org.osjava.reportrunner.Result;
import org.osjava.reportrunner.util.tablelayout.TableLayoutHelper;


public class TableImageRenderer extends AbstractRenderer {

    private double[] columnWidths;    
    private String[] columnAlignments;
    private int rowHeight;  
    private double[] footerWidths = null;
    private Color footerColor = Color.WHITE;
    
    private Calendar cal = Calendar.getInstance();
    
    public void setColWidths(String colWidths) {
        String[] widthStrings = colWidths.split(",");
        this.columnWidths = new double[widthStrings.length];
        
        for (int i = 0; i < this.columnWidths.length; i++) {
            this.columnWidths[i] = Double.parseDouble(widthStrings[i]);
        }
    }
    
    public void setFooterWidths(String footerWidths) {
        String[] widthStrings = footerWidths.split(",");
        this.footerWidths = new double[widthStrings.length];
        
        for (int i = 0; i < this.footerWidths.length; i++) {
            this.footerWidths[i] = Double.parseDouble(widthStrings[i]);
        }
    }
    
    public void setFooterColor(String footer) {                
        this.footerColor = Color.decode(footer);
    }
    
    public void setRowHeight(String rowHeight) {
        this.rowHeight = Integer.parseInt(rowHeight);
    }
    
    public void setColAlignments(String colAlignments) {
        this.columnAlignments = colAlignments.split(",");
    }
    
    public void display(Result result, Report report, Writer out) throws IOException {
        throw new RuntimeException("This should not be used with a Writer. ");
    }
    
    public void display(Result result, Report report, OutputStream out) throws IOException {                       
        Column[] columns = result.getHeader();
                
        String[] headers = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {     
            headers[i] = columns[i].getLabel();            
        }        
       
        BufferedImage img = this.create(headers, result);         
        ImageIO.write(img, "png", out);                        
    }
    
    private BufferedImage create(String[] headers, Result result) {
        JPanel targetPanel = new JPanel();
        
        TableLayoutHelper tlh = new TableLayoutHelper(targetPanel);        
        tlh.setRowHeight(this.rowHeight);
        tlh.setColWidths(this.columnWidths);
        tlh.setBorderWidth(0);
        
        // Header       
        JPanel[] headerPanels = new JPanel[headers.length];
        for (int i = 0; i < headerPanels.length; i++) {
            headerPanels[i] = new JPanel(new BorderLayout());             
            headerPanels[i].setBackground(Color.BLACK);            
            
            JLabel label = new JLabel(headers[i]);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Verdana", Font.BOLD, 12));
            
            String alignment = BorderLayout.CENTER;
            
            if (this.columnAlignments != null) {                    
                if (this.columnAlignments[i].equalsIgnoreCase("Left")) {
                    alignment = BorderLayout.WEST;
                } else if (this.columnAlignments[i].equalsIgnoreCase("Right")) {
                    alignment = BorderLayout.EAST;
                } 
            }
        
            headerPanels[i].add(label, alignment);                     
        }
        
        tlh.addRow(headerPanels);
        
        // Data        
        while (result.hasNextRow()) {                    
            Object[] data = result.nextRow();
            boolean isLastRow = (result.hasNextRow() == false);
            
            if (isLastRow && this.footerWidths != null) {                
                JPanel outerPanel = new JPanel();                
                
                TableLayoutHelper helper = new TableLayoutHelper(outerPanel);        
                helper.setRowHeight(this.rowHeight);
                helper.setColWidths(this.footerWidths);
                helper.setBorderWidth(0);
                
                Font font = new Font("Verdana", Font.BOLD, 12);
                JPanel[] panels = this.getInnerPanels(data, this.columnAlignments, this.footerColor, null, font);
               
                helper.addRow(panels);
                helper.addRowsToContainer();
                
                tlh.addRow(new Object[] {new Object[] {outerPanel, new Integer(data.length)}});                 
            } else {            
                Font font = new Font("Verdana", Font.BOLD, 10);
                JPanel[] panels = this.getInnerPanels(data, this.columnAlignments, null, Color.LIGHT_GRAY, font);
                tlh.addRow(panels);
            }
        }
        
        tlh.addRowsToContainer();
                
        Frame frame = new Frame();
        frame.add(targetPanel);
        frame.pack();
       
        try {
            BufferedImage image = 
                new BufferedImage(targetPanel.getPreferredSize().width, targetPanel.getPreferredSize().height, BufferedImage.TYPE_INT_RGB);                      
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Paint the panel in this Graphics object
            targetPanel.paint(g);
            frame.dispose();
            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }                           
    }
    
    private JPanel[] getInnerPanels(Object[] data, String[] columnAlignments, Color bgColor, Color borderColor, Font font) {
        JPanel[] panels = new JPanel[data.length];              
        
        for (int i = 0; i < panels.length; i++) {
            panels[i] = new JPanel(new BorderLayout());
                                                                    
            if (bgColor == null) {
                panels[i].setBackground(Color.WHITE);
            } else {
                panels[i].setBackground(bgColor);
            }
            
            if (borderColor != null) {
                panels[i].setBorder(BorderFactory.createLineBorder(borderColor));
            }
            
            String dataString = null;
            if (data[i] != null) {
                dataString = data[i].toString();
            }
            
            JLabel label = new JLabel(dataString);
            label.setFont(font);
            
            String alignment = BorderLayout.CENTER;
            
            if (columnAlignments != null) {                    
                if (columnAlignments[i].equalsIgnoreCase("Left")) {
                    alignment = BorderLayout.WEST;
                } else if (columnAlignments[i].equalsIgnoreCase("Right")) {
                    alignment = BorderLayout.EAST;
                } 
            }
            
            panels[i].add(label, alignment);                
        }
        
        return panels;
    }   
}
