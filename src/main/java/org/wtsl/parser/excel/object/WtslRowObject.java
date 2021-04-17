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

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.wtsl.parser.WtslUtils;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Integer.MAX_VALUE;
import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;

/**
 * @author Vadim Kolesnikov
 */
public class WtslRowObject extends WtslSheetObject {

    private static final Map<String, Integer> NAME_CACHE = IntStream.range(0, 100).boxed().collect
            (Collectors.toMap(CellReference::convertNumToColString, Function.identity()));

    private final Row row;

    public WtslRowObject(Map<String, ?> entries, Row row) {
        super(entries, row.getSheet());
        this.row = row;
    }

    public Row getRow() {
        return row;
    }

    public int getRowNum() {
        return getRow().getRowNum();
    }

    @Override
    public int size() {
        return getRow().getPhysicalNumberOfCells();
    }

    @Override
    public WtslCellObject get(int index) {
        return new WtslCellObject(getEntries(), getRow().getCell(index, CREATE_NULL_AS_BLANK));
    }

    @Override
    public WtslCellObject get(String key) {
        return get(NAME_CACHE.computeIfAbsent(key, CellReference::convertColStringToIndex));
    }

    @Override
    public Iterable<? extends WtslCellObject> all(int limit) {
        return WtslUtils.iterator(limit, getRow(), cell -> new WtslCellObject(getEntries(), cell));
    }

    @Override
    public boolean isVisible() {
        return !getRow().getZeroHeight();
    }

    // short links

    public Object type(int index) {
        return get(index).getType();
    }

    public Object type(String key) {
        return get(key).getType();
    }

    public Object font(int index) {
        return get(index).getFont();
    }

    public Object font(String key) {
        return get(key).getFont();
    }

    public Object style(int index) {
        return get(index).getStyle();
    }

    public Object style(String key) {
        return get(key).getStyle();
    }

    public Object value(int index) {
        return get(index).getValue();
    }

    public Object value(String key) {
        return get(key).getValue();
    }

    public Object visible(int index) {
        return get(index).isVisible();
    }

    public Object visible(String key) {
        return get(key).isVisible();
    }

    @Override
    public String toString() {
        DataFormatter formatter = new DataFormatter();

        return super.toString() + ", row: [ size: " + size()
                + ", visible: " + isVisible()
                + ", index: " + getRowNum()
                + ", values: " + String.join(" | ", WtslUtils.iterator(MAX_VALUE, getRow(), formatter::formatCellValue))
                + " ]";
    }
}
