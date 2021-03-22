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

package org.wtst.parser.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.wtst.parser.WtstObject;
import org.wtst.parser.excel.object.WtstCellObject;
import org.wtst.parser.excel.object.WtstFontObject;
import org.wtst.parser.excel.object.WtstRowObject;
import org.wtst.parser.excel.object.WtstSheetObject;

import java.util.Map;

/**
 * @author Vadim Kolesnikov
 */
public class WtstExcelObject implements WtstObject {

    private final Map<String, ?> entries;

    private final Workbook workbook;

    private final Sheet sheet;

    private final Row row;

    public WtstExcelObject(Map<String, ?> entries) {
        this(entries, null);
    }

    public WtstExcelObject(Map<String, ?> entries, Workbook workbook) {
        this(entries, workbook, null);
    }

    public WtstExcelObject(Map<String, ?> entries, Workbook workbook, Sheet sheet) {
        this(entries, workbook, sheet, null);
    }

    public WtstExcelObject(Map<String, ?> entries, Workbook workbook, Sheet sheet, Row row) {
        this.entries = entries;

        this.workbook = workbook;
        this.sheet = sheet;
        this.row = row;
    }

    public WtstSheetObject sheet() {
        return new WtstSheetObject(workbook, sheet);
    }

    public WtstRowObject row() {
        return new WtstRowObject(workbook, sheet, row);
    }

    public WtstCellObject cell(int number) {
        return new WtstCellObject(workbook, sheet, row, row == null ? null : row.getCell(number));
    }

    public WtstFontObject font(int number) {
        return cell(number).font();
    }

    @Override
    public Object value(int number) {
        return cell(number).value();
    }

    @Override
    public Map<String, ?> getEntries() {
        return entries;
    }
}
