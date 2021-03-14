package org.wtst.parser.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.wtst.parser.WtstObject;

public class WtstRowObject implements WtstObject {

    private final Workbook workbook;

    private final Sheet sheet;

    private final Row row;

    public WtstRowObject(Workbook workbook, Sheet sheet, Row row) {
        this.workbook = workbook;
        this.sheet = sheet;
        this.row = row;
    }
}
