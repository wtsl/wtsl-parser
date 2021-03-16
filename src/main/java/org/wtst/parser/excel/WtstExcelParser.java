package org.wtst.parser.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.Resource;
import org.springframework.expression.EvaluationContext;
import org.wtst.parser.WtstObject;
import org.wtst.parser.WtstParser;
import org.wtst.parser.WtstSchema;
import org.wtst.parser.excel.object.WtstRowObject;
import org.wtst.parser.excel.object.WtstSheetObject;
import org.wtst.parser.rule.WtstParserRule;
import org.wtst.parser.rule.WtstReaderRule;
import org.wtst.util.SpelUtils;
import org.wtst.util.WtstUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WtstExcelParser implements WtstParser {

    @Override
    public <T> T parse(Map<String, ?> metadata, WtstSchema schema, Resource resource, Class<T> type) {
        Map<String, Object> result = new HashMap<>();

        EvaluationContext context = SpelUtils.build();
        context.setVariable("result", result);
        context.setVariable("entries", schema.getEntries());
        context.setVariable("metadata", metadata);

        try (Workbook workbook = WorkbookFactory.create(resource.getInputStream())) {
            parse(context, schema.getParsers(), workbook);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        return null; // TODO type
    }

    private void parse(EvaluationContext context, List<WtstParserRule> parsers, Workbook workbook) {
        for (Sheet sheet : workbook) {
            WtstObject object = new WtstSheetObject(workbook, sheet);

            for (WtstParserRule parser : parsers) {
                if (parser.isChapter(context, object)) {
                    read(context, parser.getReaders(), workbook, sheet);
                    // TODO parser.getMappers()
                }
            }
        }
    }

    private void read(EvaluationContext context, List<WtstReaderRule> readers, Workbook workbook, Sheet sheet) {
        WtstReaderRule reader = null;
        Object value = null;

        for (Row row : sheet) {
            WtstObject object = new WtstRowObject(workbook, sheet, row);

            if (reader != null && reader.isTill(context, object)) {
                final Object next = reader.getTake().getAlter(context, object, value);
                values(context).compute(reader.getTake().getName(), (key, prev) -> WtstUtils.merge(prev, next));

                reader = null;
                value = null;
            }

            if (reader == null) {
                reader = readers.stream().filter(rule -> rule.isWhen(context, object)).findFirst().orElse(null);
            }

            if (reader != null && !reader.isSkip(context, object)) {
                value = WtstUtils.merge(value, reader.getTake().getValue(context, object));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> values(EvaluationContext context) {
        return (Map<String, Object>) context.lookupVariable("values");
    }
}
