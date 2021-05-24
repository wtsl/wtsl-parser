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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.wtsl.parser.WtslUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;
import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.RETURN_NULL_AND_BLANK;

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

    public final Row getRow() {
        return row;
    }

    public final int getRowNum() {
        return getRow().getRowNum();
    }

    public final int getRowSize() {
        return getRow().getPhysicalNumberOfCells();
    }

    public final String getRowName() {
        return Integer.toString(getRowNum() + 1);
    }

    public final boolean isRowVisible() {
        return !getRow().getZeroHeight();
    }

    @Override
    public int size() {
        return getRowSize();
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

    public Iterable<? extends WtslCellObject> all(int from, int to) {
        return all(from, to, Function.identity());
    }

    public Iterable<? extends WtslCellObject> all(CellRangeAddress range) {
        return all(range, Function.identity());
    }

    private <T> Iterable<T> all(int from, int to, Function<WtslCellObject, T> function) {
        List<T> objects = new ArrayList<>();
        for (int i = from; i <= to; i++) {
            Cell cell = getRow().getCell(i, RETURN_NULL_AND_BLANK);
            if (cell != null) {
                objects.add(function.apply(new WtslCellObject(getEntries(), cell)));
            }
        }
        return objects;
    }

    private <T> Iterable<T> all(CellRangeAddress range, Function<WtslCellObject, T> function) {
        return all(range.getFirstColumn(), range.getLastColumn(), function);
    }

    @Override
    public String getName() {
        return getRowName();
    }

    @Override
    public int getNumber() {
        return getRowNum();
    }

    @Override
    public boolean isVisible() {
        return isRowVisible();
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

    public Object range(int index) {
        return get(index).getRange();
    }

    public Object range(String key) {
        return get(key).getRange();
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

    public Object values(int from, int to) {
        return all(from, to, WtslCellObject::getValue);
    }

    public Object values(CellRangeAddress range) {
        return all(range, WtslCellObject::getValue);
    }

    public Object visible(int index) {
        return get(index).isVisible();
    }

    public Object visible(String key) {
        return get(key).isVisible();
    }

    @Override
    public String toString() {
        return super.toString() + "; row = " + getRowName();
    }
}
