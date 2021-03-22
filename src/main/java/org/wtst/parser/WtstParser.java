package org.wtst.parser;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface WtstParser {

    List<Map<String, Object>> parse(Map<String, Object> metadata, WtstSchema schema, InputStream stream);

    default List<Map<String, Object>> parse(WtstSchema schema, InputStream stream) {
        return parse(Collections.emptyMap(), schema, stream);
    }
}
