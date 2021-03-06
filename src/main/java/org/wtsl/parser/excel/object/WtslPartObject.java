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

import org.apache.poi.ss.usermodel.Font;
import org.wtsl.parser.excel.WtslExcelValues;

import java.util.List;

/**
 * @author Vadim Kolesnikov
 */
public class WtslPartObject extends WtslCellObject {

    private final Font font;

    private final Object value;

    public WtslPartObject(WtslCellObject parent, Font font, Object value) {
        super(parent);
        this.font = font;
        this.value = value;
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public List<WtslExcelValues> all() {
        throw new UnsupportedOperationException();
    }
}
