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

import org.apache.poi.ss.usermodel.*;

/**
 * @author Vadim Kolesnikov
 */
public class WtslFontObject {

    private final Workbook workbook;

    private final Sheet sheet;

    private final Row row;

    private final Cell cell;

    private final Font font;

    public WtslFontObject(Workbook workbook, Sheet sheet, Row row, Cell cell, Font font) {
        this.workbook = workbook;
        this.sheet = sheet;
        this.row = row;
        this.cell = cell;
        this.font = font;
    }

    public boolean isStrikeout() {
        return font != null && font.getStrikeout();
    }
}
