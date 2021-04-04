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

import org.apache.poi.ss.usermodel.Workbook;
import org.wtsl.parser.WtslUtils;
import org.wtsl.parser.WtslValues;
import org.wtsl.parser.excel.WtslExcelObject;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Vadim Kolesnikov
 */
public class WtslBookObject extends WtslExcelObject implements WtslValues {

    private final Workbook book;

    public WtslBookObject(Map<String, ?> entries, Workbook book) {
        super(entries);
        this.book = book;
    }

    public Workbook getBook() {
        return book;
    }

    @Override
    public int size() {
        return getBook().getNumberOfSheets();
    }

    @Override
    public WtslSheetObject get(int index) {
        return new WtslSheetObject(getEntries(), getBook().getSheetAt(index));
    }

    @Override
    public WtslSheetObject get(String key) {
        return new WtslSheetObject(getEntries(), getBook().getSheet(key));
    }

    @Override
    public Iterable<? extends WtslSheetObject> all(int limit) {
        return WtslUtils.iterator(limit, getBook(), sheet -> new WtslSheetObject(getEntries(), sheet));
    }

    public boolean isVisible() {
        return !getBook().isHidden();
    }

    public String getVersion() {
        return getBook().getSpreadsheetVersion().name();
    }
}
