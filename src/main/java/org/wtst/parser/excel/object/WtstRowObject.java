package org.wtst.parser.excel.object;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class WtstRowObject {

    private final Workbook workbook;

    private final Sheet sheet;

    private final Row row;

    public WtstRowObject(Workbook workbook, Sheet sheet, Row row) {
        this.workbook = workbook;
        this.sheet = sheet;
        this.row = row;
    }

    public int number() {
        return row == null ? -1 : row.getRowNum() + 1;
    }
}
