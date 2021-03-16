package org.wtst.parser.excel.object;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.wtst.util.ApoiUtils;

public class WtstCellObject {

    private final Workbook workbook;

    private final Sheet sheet;

    private final Row row;

    private final Cell cell;

    public WtstCellObject(Workbook workbook, Sheet sheet, Row row, Cell cell) {
        this.workbook = workbook;
        this.sheet = sheet;
        this.row = row;
        this.cell = cell;
    }

    public WtstFontObject font() {
        return new WtstFontObject(workbook, sheet, row, cell, ApoiUtils.getFont(workbook, cell));
    }

    public Object value() {
        return ApoiUtils.getValue(cell);
    }
}
