package org.wtst.parser.excel.object;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;

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
        if (cell instanceof XSSFCell) {
            return new WtstFontObject(workbook, sheet, row, cell, ((XSSFCell) cell).getCellStyle().getFont());
        }

        if (cell instanceof HSSFCell && workbook instanceof HSSFWorkbook) {
            return new WtstFontObject(workbook, sheet, row, cell, ((HSSFCell) cell).getCellStyle().getFont(workbook));
        }

        return new WtstFontObject(workbook, sheet, row, cell, null);
    }

    public Object value() {
        if (cell != null) {
            switch (cell.getCellType()) {
                case BLANK:
                case STRING:
                case FORMULA:
                    return cell.getStringCellValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getLocalDateTimeCellValue();
                    }
                    return cell.getNumericCellValue();
                case BOOLEAN:
                    return cell.getBooleanCellValue();
            }
        }

        return "";
    }
}
