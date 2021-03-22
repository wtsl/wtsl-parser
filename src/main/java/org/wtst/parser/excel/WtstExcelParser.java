package org.wtst.parser.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.expression.Expression;
import org.wtst.parser.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Boolean.TRUE;
import static org.wtst.parser.WtstUtils.stream;

public class WtstExcelParser implements WtstParser {

    @Override
    public List<Map<String, Object>> parse(Map<String, Object> metadata, WtstSchema schema, InputStream stream) {
        Map<String, Object> entries = new LinkedHashMap<>(schema.getEntries());

        try (Workbook workbook = WorkbookFactory.create(stream)) {
            read(metadata, entries, schema.getReaders(), workbook);
            return write(metadata, entries, schema.getWriters());
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private void read(Map<String, Object> metadata, Map<String, Object> entries, List<WtstReader> readers, Workbook workbook) {
        if (readers == null || readers.isEmpty()) {
            return;
        }

        for (Sheet sheet : workbook) {
            WtstContext ctx = new WtstContext(metadata);
            WtstObject obj = new WtstExcelObject(entries, workbook, sheet);

            for (WtstReader reader : readers) {
                if (reader.isWhen(ctx, obj)) {
                    reader.doThen(ctx, obj, name -> true);
                    read(metadata, entries, reader.getTake(), workbook, sheet);
                }
            }
        }
    }

    private void read(Map<String, Object> metadata, Map<String, Object> entries, List<WtstReader> readers, Workbook workbook, Sheet sheet) {
        if (readers == null || readers.isEmpty()) {
            return;
        }

        WtstReader reader = null;
        WtstContext ctx = null;

        for (Row row : sheet) {
            WtstObject obj = new WtstExcelObject(entries, workbook, sheet, row);

            if (reader != null && reader.isTill(ctx, obj)) {
                reader.doThen(ctx, obj, name -> name.charAt(0) == '$');
                reader = null;
            }

            if (reader == null) {
                ctx = new WtstContext(metadata);

                for (WtstReader temp : readers) {
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
        WtstObject obj = new WtstExcelObject(entries);

        Stream<WtstContext> stream = Stream.of(new WtstContext(metadata));

        for (Map.Entry<String, Expression> writer : writers.entrySet()) {
            final String name = writer.getKey();
            final Expression exp = writer.getValue();

            if (exp.getExpressionString().matches("^forEach\\([\\s\\S]+\\)$")) {
                stream = stream.flatMap(ctx -> stream(exp.getValue(ctx, obj)).map(value -> ctx.next(name, value)));
            } else if (exp.getExpressionString().matches("^removeIf\\([\\s\\S]+\\)$")) {
                stream = stream.filter(ctx -> !TRUE.equals(exp.getValue(ctx, obj))).map(ctx -> ctx.next(name, false));
            } else {
                stream = stream.map(ctx -> ctx.next(name, exp.getValue(ctx, obj)));
            }
        }

        return stream.map(WtstContext::getVariables).collect(Collectors.toList());
    }
}
