package com.ogic.prescriptionsyntheticsystem.component;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * @author ogic
 */

public abstract class ExcelImportTool {
    FileInputStream in;
    HSSFWorkbook workbook;
    public ExcelImportTool(String fileName) throws IOException {
        in = new FileInputStream(fileName);
        workbook = new HSSFWorkbook(in);
    }

    public void close() throws IOException {
        in.close();
    }

    /**
     * 读取文件流中指定sheet的表格
     * @param sheetId
     * @return
     */
    public abstract List readExcel(int sheetId) throws IOException, ParseException;
}
