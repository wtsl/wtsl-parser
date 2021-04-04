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

package org.wtsl.parser.excel.object;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Vadim Kolesnikov
 */
public class WtslCellObject extends WtslRowObject {

    private static final Object UNKNOWN_TYPE = new Object();

    private final Cell cell;

    public WtslCellObject(Map<String, ?> entries, Cell cell) {
        super(entries, cell.getRow());
        this.cell = cell;
    }

    public Cell getCell() {
        return cell;
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public WtslCellObject get(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public WtslCellObject get(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<WtslCellObject> all(int limit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isVisible() {
        return !getSheet().isColumnHidden(getCell().getColumnIndex());
    }

    public String getType() {
        return getCell().getCellType().name();
    }

    public Font getFont() {
        return getBook().getFontAt(getStyle().getFontIndex());
    }

    public CellStyle getStyle() {
        return getCell().getCellStyle();
    }

    public Object getValue() {
        switch (getCell().getCellType()) {
            case ERROR:
                return getCell().getErrorCellValue();
            case BLANK:
            case STRING:
            case FORMULA:
                return getCell().getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(getCell())) {
                    return getCell().getLocalDateTimeCellValue();
                }
                return getCell().getNumericCellValue();
            case BOOLEAN:
                return getCell().getBooleanCellValue();
            default:
                return UNKNOWN_TYPE;
        }
    }
}
