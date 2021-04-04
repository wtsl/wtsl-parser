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

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetVisibility;
import org.wtsl.parser.WtslUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Vadim Kolesnikov
 */
public class WtslSheetObject extends WtslBookObject {

    private final Sheet sheet;

    public WtslSheetObject(Map<String, ?> entries, Sheet sheet) {
        super(entries, sheet.getWorkbook());
        this.sheet = sheet;
    }

    public Sheet getSheet() {
        return sheet;
    }

    @Override
    public int size() {
        return getSheet().getPhysicalNumberOfRows();
    }

    @Override
    public WtslRowObject get(int index) {
        return new WtslRowObject(getEntries(), getSheet().getRow(index));
    }

    @Override
    public WtslRowObject get(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<? extends WtslRowObject> all(int limit) {
        return WtslUtils.iterator(limit, getSheet(), row -> new WtslRowObject(getEntries(), row));
    }

    @Override
    public boolean isVisible() {
        return getBook().getSheetVisibility(getBook().getSheetIndex(getSheet())) == SheetVisibility.VISIBLE;
    }

    public boolean isProtected() {
        return getSheet().getProtect();
    }

    public String getSheetName() {
        return getSheet().getSheetName();
    }
}
