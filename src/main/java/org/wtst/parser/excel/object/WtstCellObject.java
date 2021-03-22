/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wtst.parser.excel.object;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;

/**
 * @author Vadim Kolesnikov
 */
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
