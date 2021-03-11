package org.wtst.parser.excel;

import org.springframework.core.io.Resource;
import org.wtst.parser.WtstParser;
import org.wtst.parser.WtstSchema;

import java.util.Map;

public class WtstExcelParser implements WtstParser {

    @Override
    public <T> T parse(Resource resource, WtstSchema schema, Map<String, ?> metadata, Class<T> type) {
        return null;
    }
}
