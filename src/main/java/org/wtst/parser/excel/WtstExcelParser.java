package org.wtst.parser.excel;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.Resource;
import org.wtst.parser.WtstParser;
import org.wtst.parser.WtstSchema;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

public class WtstExcelParser implements WtstParser {

    @Override
    public <T> T parse(Resource resource, WtstSchema schema, Map<String, ?> metadata, Class<T> type) {
        read(resource, schema, metadata);
        return null;
    }

    public Map<String, Object> read(Resource resource, WtstSchema schema, Map<String, ?> metadata) {
        try (Workbook workbook = WorkbookFactory.create(resource.getInputStream())) {
            return read(workbook, schema, metadata);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public Map<String, Object> read(Workbook workbook, WtstSchema schema, Map<String, ?> metadata) {
        return null;
    }
}
