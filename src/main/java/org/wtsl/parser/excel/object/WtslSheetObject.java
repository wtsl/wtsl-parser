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

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetVisibility;
import org.apache.poi.ss.util.CellRangeAddress;
import org.wtsl.parser.WtslUtils;
import org.wtsl.parser.excel.WtslExcelValues;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Vadim Kolesnikov
 */
public class WtslSheetObject extends WtslBookObject {

    private final Sheet sheet;

    private final List<CellRangeAddress> ranges;

    public WtslSheetObject(WtslBookObject parent, Sheet sheet) {
        super(parent);
        this.sheet = sheet;
        this.ranges = sheet.getMergedRegions();
    }

    WtslSheetObject(WtslSheetObject parent) {
        super(parent);
        this.sheet = parent.sheet;
        this.ranges = parent.ranges;
    }

    // refined properties

    public final Sheet getSheet() {
        return sheet;
    }

    public List<CellRangeAddress> getRanges() {
        return ranges;
    }

    public final int getSheetNum() {
        return getBook().getSheetIndex(getSheet());
    }

    public final int getSheetSize() {
        return getSheet().getPhysicalNumberOfRows();
    }

    public final String getSheetName() {
        return getSheet().getSheetName();
    }

    public final boolean isSheetVisible() {
        return getBook().getSheetVisibility(getBook().getSheetIndex(getSheet())) == SheetVisibility.VISIBLE;
    }

    public final boolean isSheetProtected() {
        return getSheet().getProtect();
    }

    // common properties

    @Override
    public String getName() {
        return getSheetName();
    }

    @Override
    public boolean isVisible() {
        return isSheetVisible();
    }

    public int getNumber() {
        return getSheetNum();
    }

    public boolean isProtected() {
        return isSheetProtected();
    }

    // interface properties

    public List<WtslExcelValues> all(int from, int to) {
        List<WtslExcelValues> objects = new ArrayList<>(to - from + 1);
        for (int i = from; i <= to; i++) {
            Row row = getSheet().getRow(i);
            if (row != null) {
                objects.add(new WtslRowObject(this, row));
            }
        }
        return objects;
    }

    public List<WtslExcelValues> all(CellRangeAddress range) {
        return all(range.getFirstRow(), range.getLastRow());
    }

    @Override
    public WtslRowObject get(int index) {
        return new WtslRowObject(this, getSheet().getRow(index));
    }

    @Override
    public WtslRowObject get(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<WtslExcelValues> iterator() {
        return WtslUtils.iterator(getSheet(), row -> new WtslRowObject(this, row));
    }

    @Override
    public int size() {
        return getSheetSize();
    }

    @Override
    public String toString() {
        return super.toString() + "; sheet = " + getSheetName().replace("\n", "\\n");
    }
}
