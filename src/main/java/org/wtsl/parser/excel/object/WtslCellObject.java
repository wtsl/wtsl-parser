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

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.util.ArrayList;
import java.util.List;
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

    private final List<WtslPartObject> parts;

    public WtslCellObject(Map<String, ?> entries, Cell cell) {
        super(entries, cell.getRow());
        this.cell = cell;
        this.parts = new ArrayList<>();
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
        return all().size();
    }

    @Override
    public WtslPartObject get(int index) {
        return all().get(index);
    }

    @Override
    public WtslPartObject get(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<? extends WtslPartObject> all(int limit) {
        return all().subList(0, limit);
    }

    @Override
    public List<? extends WtslPartObject> all() {
        if (parts.isEmpty()) {
            if (getCell().getCellType() == CellType.STRING) {
                RichTextString rts = getCell().getRichStringCellValue();
                String value = rts.getString();

                boolean hssf = rts instanceof HSSFRichTextString;

                for (int i = 0; i < rts.numFormattingRuns(); i++) {
                    int start = rts.getIndexOfFormattingRun(i);
                    if (parts.isEmpty() && start > 0) {
                        parts.add(new WtslPartObject(getEntries(), getCell(), getFont(), value.substring(0, start)));
                    }

                    Font font;
                    if (hssf) {
                        font = getBook().getFontAt(((HSSFRichTextString) rts).getFontOfFormattingRun(i));
                    } else {
                        font = ((XSSFRichTextString) rts).getFontOfFormattingRun(i);
                    }

                    if (font == null) {
                        font = getFont();
                    }

                    parts.add(new WtslPartObject(getEntries(), getCell(), font, i + 1 < rts.numFormattingRuns()
                            ? value.substring(start, rts.getIndexOfFormattingRun(i + 1)) : value.substring(start)));
                }
            }

            if (parts.isEmpty()) {
                parts.add(new WtslPartObject(getEntries(), getCell(), getFont(), getValue()));
            }
        }

        return parts;
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

    public CellRangeAddress getRange() {
        int rowNum = getRowNum();
        int colNum = getColNum();

        for (CellRangeAddress range : getRanges()) {
            if (range.isInRange(rowNum, colNum)) {
                return range;
            }
        }

        return new CellRangeAddress(rowNum, rowNum, colNum, colNum);
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
        return super.toString() + "; col = " + getColName();
    }
}
