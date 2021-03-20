package org.wtst.parser.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.Resource;
import org.springframework.expression.EvaluationContext;
import org.wtst.parser.WtstObject;
import org.wtst.parser.WtstParser;
import org.wtst.parser.WtstReader;
import org.wtst.parser.WtstSchema;
import org.wtst.util.SpelUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WtstExcelParser implements WtstParser {

    @Override
    public <T> T parse(Map<String, ?> metadata, WtstSchema schema, Resource resource, Class<T> type) {
        Map<String, Object> entries = new HashMap<>(schema.getEntries());

        try (Workbook workbook = WorkbookFactory.create(resource.getInputStream())) {
            read(metadata, entries, schema.getReaders(), workbook);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        return null; // TODO
    }

    private void read(Map<String, ?> metadata, Map<String, Object> entries, List<WtstReader> readers, Workbook workbook) {
        if (readers == null || readers.isEmpty()) {
            return;
        }

        for (Sheet sheet : workbook) {
            EvaluationContext ctx = SpelUtils.build(metadata);
            WtstObject obj = new WtstExcelObject(entries, workbook, sheet);

            for (WtstReader reader : readers) {
                if (reader.isWhen(ctx, obj)) {
                    reader.doThen(ctx, obj, name -> true);
                    read(metadata, entries, reader.getTake(), workbook, sheet);
                }
            }
        }
    }

    private void read(Map<String, ?> metadata, Map<String, Object> entries, List<WtstReader> readers, Workbook workbook, Sheet sheet) {
        if (readers == null || readers.isEmpty()) {
            return;
        }

        WtstReader reader = null;
        EvaluationContext ctx = null;

        for (Row row : sheet) {
            WtstObject obj = new WtstExcelObject(entries, workbook, sheet, row);

            if (reader != null && reader.isTill(ctx, obj)) {
                reader.doThen(ctx, obj, name -> name.charAt(0) == '$');
                reader = null;
            }

            if (reader == null) {
                ctx = SpelUtils.build(metadata);

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
}
