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
import org.apache.poi.ss.util.CellReference;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Vadim Kolesnikov
 */
public class WtslCellObject extends WtslRowObject {

    private static final Map<Integer, String> NAME_CACHE = IntStream.range(0, 100).boxed().collect
            (Collectors.toMap(Function.identity(), CellReference::convertNumToColString));

    private static final Object UNKNOWN_TYPE = new Object();

    private final Cell cell;

    public WtslCellObject(Map<String, ?> entries, Cell cell) {
        super(entries, cell.getRow());
        this.cell = cell;
    }

    public final Cell getCell() {
        return cell;
    }

    public final int getColNum() {
        return getCell().getColumnIndex();
    }

    public final String getColName() {
        return NAME_CACHE.computeIfAbsent(getColNum(), CellReference::convertNumToColString);
    }

    public final boolean isColVisible() {
        return !getSheet().isColumnHidden(getCell().getColumnIndex());
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
    public Iterable<WtslCellObject> all(int limit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return getColName();
    }

    @Override
    public int getNumber() {
        return getColNum();
    }

    @Override
    public boolean isVisible() {
        return isColVisible();
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

    @Override
    public String toString() {
        return super.toString() + ", cell: [ type: " + getType()
                + ", name: " + getColName()
                + ", number: " + getColNum()
                + ", visible: " + isColVisible()
                + " ]";
    }
}
