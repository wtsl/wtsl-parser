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

package org.wtsl.parser.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.expression.Expression;
import org.wtsl.parser.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Boolean.TRUE;

/**
 * @author Vadim Kolesnikov
 */
public class WtslExcelParser implements WtslParser {

    @Override
    public List<Map<String, Object>> parse(Map<String, Object> metadata, WtslSchema schema, InputStream stream) {
        Map<String, Object> entries = new LinkedHashMap<>(schema.getEntries());

        try (Workbook workbook = WorkbookFactory.create(stream)) {
            read(metadata, entries, schema.getReaders(), workbook);
            return write(metadata, entries, schema.getWriters());
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private void read(Map<String, Object> metadata, Map<String, Object> entries, List<WtslReader> readers, Workbook workbook) {
        if (readers == null || readers.isEmpty()) {
            return;
        }

        for (Sheet sheet : workbook) {
            WtslContext ctx = new WtslContext(metadata);
            WtslObject obj = new WtslExcelObject(entries, workbook, sheet);

            for (WtslReader reader : readers) {
                if (reader.isWhen(ctx, obj)) {
                    reader.doThen(ctx, obj, name -> true);
                    read(metadata, entries, reader.getTake(), workbook, sheet);
                }
            }
        }
    }

    private void read(Map<String, Object> metadata, Map<String, Object> entries, List<WtslReader> readers, Workbook workbook, Sheet sheet) {
        if (readers == null || readers.isEmpty()) {
            return;
        }

        WtslReader reader = null;
        WtslContext ctx = null;

        for (Row row : sheet) {
            WtslObject obj = new WtslExcelObject(entries, workbook, sheet, row);

            if (reader != null && reader.isTill(ctx, obj)) {
                reader.doThen(ctx, obj, name -> name.charAt(0) == '$');
                reader = null;
            }

            if (reader == null) {
                ctx = new WtslContext(metadata);

                for (WtslReader temp : readers) {
                    if (temp.isWhen(ctx, obj)) {
                        reader = temp;
                        reader.doThen(ctx, obj, name -> name.charAt(name.length() - 1) == '$');
                        break;
                    }
                }
            }

            if (reader != null && !reader.isSkip(ctx, obj)) {
                reader.doThen(ctx, obj, name -> !(name.charAt(0) == '$' || name.charAt(name.length() - 1) == '$'));
            }
        }
    }

    private List<Map<String, Object>> write(Map<String, Object> metadata, Map<String, Object> entries, Map<String, Expression> writers) {
        WtslObject obj = new WtslExcelObject(entries);

        Stream<WtslContext> stream = Stream.of(new WtslContext(metadata));

        for (Map.Entry<String, Expression> writer : writers.entrySet()) {
            final String name = writer.getKey();
            final Expression exp = writer.getValue();

            if (exp.getExpressionString().matches("^forEach\\([\\s\\S]+\\)$")) {
                stream = stream.flatMap(ctx -> WtslUtils.stream(exp.getValue(ctx, obj)).map(value -> ctx.next(name, value)));
            } else if (exp.getExpressionString().matches("^removeIf\\([\\s\\S]+\\)$")) {
                stream = stream.filter(ctx -> !TRUE.equals(exp.getValue(ctx, obj))).map(ctx -> ctx.next(name, false));
            } else {
                stream = stream.map(ctx -> ctx.next(name, exp.getValue(ctx, obj)));
            }
        }

        return stream.map(WtslContext::getVariables).collect(Collectors.toList());
    }
}
