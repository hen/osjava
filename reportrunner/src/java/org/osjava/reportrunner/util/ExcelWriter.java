package org.osjava.reportrunner.util;

import com.generationjava.io.CsvWriter;

import org.apache.poi.hssf.usermodel.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class ExcelWriter extends CsvWriter {

    private HSSFRow row;
    private HSSFWorkbook wb;
    private HSSFSheet s;
    private OutputStream out;
    private short col;
    private short rowNum;

    // This needs to become an OutputStream
    public ExcelWriter(OutputStream out) {
        super(new OutputStreamWriter(out));
        this.out = out;
        wb = new HSSFWorkbook();
        s = wb.createSheet();
        wb.setSheetName(0, "Report");
    }

    public void writeField(String field) throws IOException {
        if(row == null) {
            row = s.createRow(rowNum);
        }
        HSSFCell cell = row.createCell(col);
        cell.setCellValue(field);
        col++;
    }

    public void endBlock() throws IOException {
        this.row = null;
        this.col = 0;
        this.rowNum++;
    }

    public void close() throws IOException {
        wb.write(out); 
        super.close();
    }

    public void flush() throws IOException {
        wb.write(out);
    }

}
