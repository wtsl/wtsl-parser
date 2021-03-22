package org.wtst.parser.excel.object;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class WtstSheetObject {

    private final Workbook workbook;

    private final Sheet sheet;

    public WtstSheetObject(Workbook workbook, Sheet sheet) {
        this.workbook = workbook;
        this.sheet = sheet;
    }

    public int number() {
        return workbook == null ? -1 : workbook.getSheetIndex(sheet) + 1;
    }

    public String name() {
        return sheet == null ? null : sheet.getSheetName();
    }
}
