package org.osjava.reportrunner.util;

import com.generationjava.io.CsvWriter;

import org.apache.poi.hssf.usermodel.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class ExcelWriter {

    private HSSFRow row;
    private HSSFWorkbook wb;
    private HSSFSheet s;
    private OutputStream out;
    private short col;
    private short rowNum;

    public ExcelWriter(OutputStream out) {
        this.out = out;
        wb = new HSSFWorkbook();
        s = wb.createSheet();
        wb.setSheetName(0, "Report");
    }

    public void writeField(Object field) throws IOException {
        if(row == null) {
            row = s.createRow(rowNum);
        }
        HSSFCell cell = row.createCell(col);
        if(field instanceof Number) {
            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            cell.setCellValue( ((Number)field).doubleValue() );
        } else
        if(field instanceof Boolean) {
            cell.setCellType(HSSFCell.CELL_TYPE_BOOLEAN);
            cell.setCellValue( ((Boolean)field).booleanValue() );
        } else
        if(field instanceof java.util.Date) {
            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            cell.setCellValue( (java.util.Date) field );
        } else
        if(field instanceof java.util.Calendar) {
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
