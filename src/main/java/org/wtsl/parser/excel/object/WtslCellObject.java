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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.wtsl.parser.WtslUtils;
import org.wtsl.parser.excel.WtslExcelValues;

import java.util.ArrayList;
import java.util.Iterator;
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

    private final Cell cell;

    private final List<WtslExcelValues> parts;

    public WtslCellObject(WtslRowObject parent, Cell cell) {
        super(parent);
        this.cell = cell;
        this.parts = new ArrayList<>();
    }

    WtslCellObject(WtslCellObject parent) {
        super(parent);
        this.cell = parent.cell;
        this.parts = parent.parts;
    }

    // refined properties

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

    // common properties

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
        return WtslUtils.value(getCell(), getEval());
    }

    // interface properties

    @Override
    public List<WtslExcelValues> all() {
        if (parts.isEmpty()) {
            if (getCell().getCellType() == CellType.STRING) {
                RichTextString rts = getCell().getRichStringCellValue();
                String value = rts.getString();

                boolean hssf = rts instanceof HSSFRichTextString;

                for (int i = 0; i < rts.numFormattingRuns(); i++) {
                    int start = rts.getIndexOfFormattingRun(i);
                    if (parts.isEmpty() && start > 0) {
                        parts.add(new WtslPartObject(this, getFont(), value.substring(0, start)));
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

                    parts.add(new WtslPartObject(this, font, i + 1 < rts.numFormattingRuns()
                            ? value.substring(start, rts.getIndexOfFormattingRun(i + 1)) : value.substring(start)));
                }
            }

            if (parts.isEmpty()) {
                parts.add(new WtslPartObject(this, getFont(), getValue()));
            }
        }

        return parts;
    }

    @Override
    public List<WtslExcelValues> all(int limit) {
        return all().subList(0, limit + 1);
    }

    @Override
    public List<WtslExcelValues> all(int from, int to) {
        return all().subList(from, to + 1);
    }

    @Override
    public List<WtslExcelValues> all(CellRangeAddress range) {
        throw new UnsupportedOperationException();
    }

    @Override
    public WtslPartObject get(int index) {
        return (WtslPartObject) all().get(index);
    }

    @Override
    public WtslPartObject get(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<WtslExcelValues> iterator() {
        return all().iterator();
    }

    @Override
    public int size() {
        return all().size();
    }

    @Override
    public String toString() {
        return super.toString() + "; col = " + getColName();
    }
}
