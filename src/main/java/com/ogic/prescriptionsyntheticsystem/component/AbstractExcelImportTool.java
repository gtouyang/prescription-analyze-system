package com.ogic.prescriptionsyntheticsystem.component;

import com.ogic.prescriptionsyntheticsystem.exception.UnitUnfixedException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Excel读取工具
 * @author ogic
 */
public abstract class AbstractExcelImportTool {
    FileInputStream in;
    HSSFWorkbook workbook;
    public AbstractExcelImportTool(String fileName) throws IOException {
        in = new FileInputStream(fileName);
        workbook = new HSSFWorkbook(in);
    }

    public void close() throws IOException {
        in.close();
    }

    /**
     * 读取文件流中指定sheet的表格
     * @param sheetId 表页
     */
    public abstract void readExcel(int sheetId) throws IOException, ParseException, UnitUnfixedException;
}
