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

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.expression.Expression;
import org.wtsl.parser.*;
import org.wtsl.parser.excel.object.WtslBookObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Boolean.TRUE;
import static org.wtsl.parser.WtslUtils.stream;
import static org.wtsl.parser.WtslUtils.value;

/**
 * @author Vadim Kolesnikov
 */
public class WtslExcelParser implements WtslParser {

    private static final Predicate<String> THEN_FILTER_PRE = key -> key.charAt(key.length() - 1) == '$';

    private static final Predicate<String> THEN_FILTER = key -> key.charAt(key.length() - 1) != '$' && key.charAt(0) != '$';

    private static final Predicate<String> THEN_FILTER_POST = key -> key.charAt(0) == '$';

    private static final Pattern FOR_EACH_FILTER = Pattern.compile("^\\s?forEach\\([\\s\\S]+\\)\\s?$");

    private static final Pattern REMOVE_IF_FILTER = Pattern.compile("^\\s?removeIf\\([\\s\\S]+\\)\\s?$");

    @Override
    public List<Map<String, Object>> parse(Map<String, Object> metadata, WtslSchema schema, InputStream stream) {
        Map<String, Object> entries = new LinkedHashMap<>(schema.getEntries());
        try (Workbook workbook = WorkbookFactory.create(stream)) {
            if (!schema.getReaders().isEmpty()) {
                read(metadata, schema.getReaders(), new WtslBookObject(entries, workbook));
            }
            return write(metadata, entries, schema.getWriters());
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private void read(Map<String, Object> metadata, List<WtslReader> readers, WtslExcelValues object) {
        WtslContext ctx = new WtslContext();
        WtslReader reader = null;

        Iterator<WtslExcelValues> iterator = object.iterator();
        while (iterator.hasNext()) {
            WtslExcelValues obj = iterator.next();

            if (reader != null && reader.isTill(ctx, obj)) {
                reader.doThen(ctx, obj, THEN_FILTER_POST);
                reader = null;
            }

            if (reader == null) {
                ctx.setVariables(metadata);

                for (WtslReader temp : readers) {
                    if (temp.isWhen(ctx, obj)) {
                        reader = temp;
                        reader.doExec(ctx, obj);
                        reader.doThen(ctx, obj, THEN_FILTER_PRE);
                        break;
                    }
                }
            }

            if (reader != null && !reader.isSkip(ctx, obj)) {
                reader.doThen(ctx, obj, THEN_FILTER);

                if (!reader.getTake().isEmpty()) {
                    read(metadata, reader.getTake(), obj);
                }

                if (!iterator.hasNext()) {
                    reader.doThen(ctx, obj, THEN_FILTER_POST);
                    reader = null;
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

            if (FOR_EACH_FILTER.matcher(exp.getExpressionString()).matches()) {
                stream = stream.flatMap(ctx -> stream(value(name, exp, ctx, obj)).map(val -> ctx.next(name, val)));
            } else if (REMOVE_IF_FILTER.matcher(exp.getExpressionString()).matches()) {
                stream = stream.filter(ctx -> !TRUE.equals(value(name, exp, ctx, obj)));
            } else {
                stream = stream.map(ctx -> ctx.same(name, value(name, exp, ctx, obj)));
            }
        }

        return stream.map(WtslContext::getVariables).collect(Collectors.toCollection(LinkedList::new));
    }
}
