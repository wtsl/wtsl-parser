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

import org.apache.poi.ss.usermodel.*;
import org.springframework.expression.Expression;
import org.wtsl.parser.*;
import org.wtsl.parser.excel.object.WtslCellObject;
import org.wtsl.parser.excel.object.WtslRowObject;
import org.wtsl.parser.excel.object.WtslSheetObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Boolean.TRUE;
import static org.wtsl.parser.WtslUtils.stream;
import static org.wtsl.parser.WtslUtils.value;

/**
 * @author Vadim Kolesnikov
 */
public class WtslExcelParser implements WtslParser {

    @Override
    public List<Map<String, Object>> parse(Map<String, Object> metadata, WtslSchema schema, InputStream stream) {
        Map<String, Object> entries = new LinkedHashMap<>(schema.getEntries());
        try (Workbook workbook = WorkbookFactory.create(stream)) {
            if (!schema.getReaders().isEmpty()) {
                read(metadata, entries, schema.getReaders(), workbook, 1);
            }
            return write(metadata, entries, schema.getWriters());
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private void read(Map<String, Object> metadata, Map<String, Object> entries, List<WtslReader> readers, Iterable<?> element, int lvl) {
        WtslContext ctx = new WtslContext();
        WtslReader reader = null;

        for (Object node : element) {
            WtslObject obj = build(entries, node, lvl);

            if (reader != null && reader.isTill(ctx, obj)) {
                reader.doThen(ctx, obj, name -> name.charAt(0) == '$');
                reader = null;
            }

            if (reader == null) {
                ctx.setVariables(metadata);

                for (WtslReader temp : readers) {
                    if (temp.isWhen(ctx, obj)) {
                        reader = temp;
                        reader.doExec(ctx, obj);
                        reader.doThen(ctx, obj, name -> name.charAt(name.length() - 1) == '$');
                        break;
                    }
                }
            }

            if (reader != null && !reader.isSkip(ctx, obj)) {
                reader.doThen(ctx, obj, name -> !(name.charAt(0) == '$' || name.charAt(name.length() - 1) == '$'));

                if (!reader.getTake().isEmpty()) {
                    read(metadata, entries, reader.getTake(), (Iterable<?>) node, lvl + 1);
                }
            }
        }
    }

    private List<Map<String, Object>> write(Map<String, Object> metadata, Map<String, Object> entries, Map<String, Expression> writers) {
        WtslObject obj = new WtslExcelStream(entries);

        Stream<WtslContext> stream = Stream.of(new WtslContext(metadata));

        for (Map.Entry<String, Expression> writer : writers.entrySet()) {
            final String name = writer.getKey();
            final Expression exp = writer.getValue();

            if (exp.getExpressionString().matches("^\\s?forEach\\([\\s\\S]+\\)\\s?$")) {
                stream = stream.flatMap(ctx -> stream(value(name, exp, ctx, obj)).map(val -> ctx.next(name, val)));
            } else if (exp.getExpressionString().matches("^\\s?removeIf\\([\\s\\S]+\\)\\s?$")) {
                stream = stream.filter(ctx -> !TRUE.equals(value(name, exp, ctx, obj)));
            } else {
                stream = stream.map(ctx -> ctx.same(name, value(name, exp, ctx, obj)));
            }
        }

        return stream.map(WtslContext::getVariables).collect(Collectors.toCollection(LinkedList::new));
    }

    private WtslObject build(Map<String, Object> entries, Object node, int lvl) {
        switch (lvl) {
            case 1:
                return new WtslSheetObject(entries, (Sheet) node);
            case 2:
                return new WtslRowObject(entries, (Row) node);
            case 3:
                return new WtslCellObject(entries, (Cell) node);
            default:
                throw new UnsupportedOperationException();
        }
    }
}
