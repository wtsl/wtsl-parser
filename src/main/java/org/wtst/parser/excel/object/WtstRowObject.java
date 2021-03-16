package org.wtst.parser.excel.object;

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

    public WtstCellObject cell(int index) {
        return new WtstCellObject(workbook, sheet, row, row.getCell(index));
    }

    public WtstFontObject font(int index) {
        return cell(index).font();
    }

    public Object value(int index) {
        return cell(index).value();
    }
}
