package org.osjava.reportrunner_plugins.renderers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.osjava.reportrunner.AbstractRenderer;
import org.osjava.reportrunner.Column;
import org.osjava.reportrunner.Report;
import org.osjava.reportrunner.Result;

public class AdvancedExcelRenderer extends AbstractRenderer {
    
    private static final int HEADER_ROW_TYPE = 1;
    private static final int DATA_ROW_TYPE = 2;
    private static final int FOOTER_ROW_TYPE = 3;
    private static final int STRIPED_ROW_TYPE = 4;
    
    private int rowHeight = 0;            // heights in points
    private int fontHeight = 0; 
    
    private int[] columnWidths = null;    // widths in 1/256th of a character
    private int[] footerWidths = null;
    
    private int[] columnAlignments = null;
    
    private boolean stripedRows = false;
    private boolean useHeader = true;
           
    public void setRowHeight(String rowHeightStr) {
        this.rowHeight = Integer.parseInt(rowHeightStr);
    }
    
    public void setFontHeight(String fontHeightStr) {
        this.fontHeight = Integer.parseInt(fontHeightStr);
    }
    
    public void setStripedRows(String stripedRows) {
        this.stripedRows = Boolean.valueOf(stripedRows).booleanValue();
    }
    
    public void setUseHeader(String useHeader) {
        this.useHeader = Boolean.valueOf(useHeader).booleanValue();
    }
    
    public void setColWidths(String colWidths) {
        String[] widthStrings = colWidths.split(",");
        this.columnWidths = new int[widthStrings.length];
        
        for (int i = 0; i < this.columnWidths.length; i++) {
            this.columnWidths[i] = Integer.parseInt(widthStrings[i]);
        }
    }
    
    public void setColAlignments(String colAlignments) {
        String[] alignStrings = colAlignments.split(",");
        this.columnAlignments = new int[alignStrings.length];
        
        for (int i = 0; i < this.columnAlignments.length; i++) {
            if (alignStrings[i].equalsIgnoreCase("Left")) {
                this.columnAlignments[i] = HSSFCellStyle.ALIGN_LEFT;
            } else if (alignStrings[i].equalsIgnoreCase("Right")) {
                this.columnAlignments[i] = HSSFCellStyle.ALIGN_RIGHT;
            } else if (alignStrings[i].equalsIgnoreCase("Center")) {
                this.columnAlignments[i] = HSSFCellStyle.ALIGN_CENTER;
            } else if (alignStrings[i].equalsIgnoreCase("Justify")) {
                this.columnAlignments[i] = HSSFCellStyle.ALIGN_JUSTIFY;
            }            
        }
    }
    
    public void setFooterWidths(String footerWidths) {
        String[] widthStrings = footerWidths.split(",");
        this.footerWidths = new int[widthStrings.length];
        
        for (int i = 0; i < this.footerWidths.length; i++) {                        
            this.footerWidths[i] = Integer.parseInt(widthStrings[i]);
        }
    }
    
    public void display(Result result, Report report, Writer out) throws IOException {
        throw new RuntimeException("This should not be used with a Writer. ");
    }

    public void display(Result result, Report report, OutputStream out) throws IOException {
        ExcelWriter xls = new ExcelWriter(out);

        // Header
        Column[] columns = result.getHeader();
        if(columns != null) {
            int rowType = HEADER_ROW_TYPE;
            
            if (!this.useHeader) {
                rowType = DATA_ROW_TYPE;
            }
            
            for(int i=0; i<columns.length; i++) {
                Integer colAlignment = null;
                if (this.columnAlignments != null) {
                    colAlignment = new Integer(this.columnAlignments[i]);
                }
                
                xls.writeField(columns[i].getLabel(), rowType, colAlignment);
            }
            xls.writeBlankField();
            xls.endBlock();
        }

        Object[] row = null;
        
        // Main Data        
        int count = 0;
        while(result.hasNextRow()) {            
            count++;
            int rowtype = DATA_ROW_TYPE;
            if (this.stripedRows && count % 2 > 0) {                                                         
                rowtype = STRIPED_ROW_TYPE; 
            }
                     
            row = result.nextRow();
        
            if (!result.hasNextRow() && this.footerWidths != null) {
                break;
            }
                                    
            for(int j=0; j<row.length; j++) {
                Integer colAlignment = null;
                if (this.columnAlignments != null) {
                    colAlignment = new Integer(this.columnAlignments[j]);
                }
                
                xls.writeField(row[j], rowtype, colAlignment);                
            }
            xls.writeBlankField();
            xls.endBlock();
        }
        
        // Footer
        if (this.footerWidths != null) {            
            for(int j=0; j<row.length; j++) {    
                Integer colAlignment = null;
                if (this.columnAlignments != null) {
                    colAlignment = new Integer(this.columnAlignments[j]);
                }
                
                xls.writeField(row[j], FOOTER_ROW_TYPE, colAlignment);
            }
            xls.writeBlankField();
            xls.endBlock();
        }

        xls.flush();
    }
    
    private class ExcelWriter {
        private HSSFRow row;
               
        private HSSFWorkbook wb;
        private HSSFSheet s;
        private HSSFSheet s2;
        
        private OutputStream out;
        
        private short col;
        private short rowNum;
        
        private HSSFFont headerFont;
        private HSSFFont plainFont;

        public ExcelWriter(OutputStream out) {
            this.out = out;
            wb = new HSSFWorkbook();
            
            s = wb.createSheet();         
                                    
            if (AdvancedExcelRenderer.this.rowHeight > 0) {
                s.setDefaultRowHeightInPoints((short) AdvancedExcelRenderer.this.rowHeight);
            }
                                    
            if (AdvancedExcelRenderer.this.columnWidths != null) {
                for (int i = 0; i < AdvancedExcelRenderer.this.columnWidths.length; i++) {
                    s.setColumnWidth((short) i, (short) AdvancedExcelRenderer.this.columnWidths[i]);
                }
            }
            
            if (AdvancedExcelRenderer.this.footerWidths != null) {
                s2 = wb.createSheet("Footer");                       
                
                for (int i = 0; i < AdvancedExcelRenderer.this.footerWidths.length; i++) {
                    s2.setColumnWidth((short) i, (short) AdvancedExcelRenderer.this.footerWidths[i]);
                }                
            }
            
            wb.setSheetName(0, "Report");
            
            this.headerFont = wb.createFont();
            headerFont.setFontName("Verdana");
            headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            headerFont.setColor(HSSFColor.WHITE.index);
            if (AdvancedExcelRenderer.this.fontHeight > 0) {       
                headerFont.setFontHeightInPoints((short) (AdvancedExcelRenderer.this.fontHeight));
            }
            
            this.plainFont = wb.createFont();
            plainFont.setFontName("Verdana");
            if (AdvancedExcelRenderer.this.fontHeight > 0) {       
                plainFont.setFontHeightInPoints((short) (AdvancedExcelRenderer.this.fontHeight));
            }
        }
        
        public void writeBlankField() throws IOException {
            this.writeField(null, 0, null);
        }

        public void writeField(Object field, int rowType, Integer align) throws IOException {                                       
            if(row == null) {
                if (rowType == HEADER_ROW_TYPE || rowType == DATA_ROW_TYPE || rowType == STRIPED_ROW_TYPE) {
                    row = s.createRow(rowNum);
                } else if (rowType == FOOTER_ROW_TYPE) {
                    row = s2.createRow(0);
                }
            }            
          
            HSSFCell cell = row.createCell(col);
            
            if (field != null && align != null) {
                HSSFCellStyle style = wb.createCellStyle();   
                
                if (align != null) {
                    style.setAlignment((short) align.intValue());
                }
                
                short color = HSSFColor.GREY_25_PERCENT.index;               
            
                if (rowType == HEADER_ROW_TYPE) {
                    cell.setCellValue("Default Palette");                                                            
                    style.setFillForegroundColor(HSSFColor.BLACK.index);                
                    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);                        
                    style.setFont(this.headerFont);
                 
                    color = HSSFColor.BLACK.index;
                } else if (rowType == FOOTER_ROW_TYPE) {
                    cell.setCellValue("Default Palette");                                
                    style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);                
                    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);      
                    style.setFont(this.plainFont);
                } else {
                    if (rowType == STRIPED_ROW_TYPE) {
                        style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);                
                        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);      
                    }
                    style.setFont(this.plainFont);
                }
                
                style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                style.setBottomBorderColor(color);
                
                style.setBorderTop(HSSFCellStyle.BORDER_THIN);
                style.setTopBorderColor(color);
                
                style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                style.setLeftBorderColor(color);
                
                style.setBorderRight(HSSFCellStyle.BORDER_THIN);
                style.setRightBorderColor(color);
                                                             
                cell.setCellStyle(style);
            } else {
                // Hack: InDesign wasn't seeing the last column of excel data when imported, so
                // we're adding on an extra column of blank data.
                field = "";
            }
            
            if (field instanceof Number) {
                cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                cell.setCellValue( ((Number)field).doubleValue() );
            } else if (field instanceof Boolean) {
                cell.setCellType(HSSFCell.CELL_TYPE_BOOLEAN);
                cell.setCellValue( ((Boolean)field).booleanValue() );
            } else if (field instanceof java.util.Date) {
                cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                cell.setCellValue( (java.util.Date) field );
            } else if (field instanceof java.util.Calendar) {
                cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                cell.setCellValue( (java.util.Calendar) field );
            } else {
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(""+field);
            }
            col++;
        }

        public void endBlock() throws IOException {
            this.row = null;
            this.col = 0;
            this.rowNum++;
        }

        public void close() throws IOException {
            wb.write(out); 
            out.flush();
            out.close();
        }

        public void flush() throws IOException {
            wb.write(out);
            out.flush();
        }
    }
}
