package org.wtst.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;

public class ApoiUtils {

    public static Object getValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case BLANK:
            case STRING:
            case FORMULA:
                return StringUtils.strip(cell.getStringCellValue());
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue();
                }
                return cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            default:
                return StringUtils.EMPTY;
        }
    }

    public static Font getFont(Workbook workbook, Cell cell) {
        if (cell instanceof XSSFCell) {
            return ((XSSFCell) cell).getCellStyle().getFont();
        }

        if (cell instanceof HSSFCell && workbook instanceof HSSFWorkbook) {
            return ((HSSFCell) cell).getCellStyle().getFont(workbook);
        }

        return null;
    }
}
