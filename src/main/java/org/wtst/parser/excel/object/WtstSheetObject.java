package org.wtst.parser.excel.object;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.wtst.parser.WtstObject;

public class WtstSheetObject implements WtstObject {

    private final Workbook workbook;

    private final Sheet sheet;

    public WtstSheetObject(Workbook workbook, Sheet sheet) {
        this.workbook = workbook;
        this.sheet = sheet;
    }

    public String name() {
        return sheet.getSheetName();
    }
}
