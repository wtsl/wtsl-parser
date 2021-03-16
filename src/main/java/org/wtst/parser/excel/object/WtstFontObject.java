package org.wtst.parser.excel.object;

import org.apache.poi.ss.usermodel.*;

public class WtstFontObject {

    private final Workbook workbook;

    private final Sheet sheet;

    private final Row row;

    private final Cell cell;

    private final Font font;

    public WtstFontObject(Workbook workbook, Sheet sheet, Row row, Cell cell, Font font) {
        this.workbook = workbook;
        this.sheet = sheet;
        this.row = row;
        this.cell = cell;
        this.font = font;
    }

    public boolean isStrikeout() {
        return font != null && font.getStrikeout();
    }
}
