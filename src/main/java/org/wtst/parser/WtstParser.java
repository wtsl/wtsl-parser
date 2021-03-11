package org.wtst.parser;

import org.springframework.core.io.Resource;

import java.util.Collections;
import java.util.Map;

public interface WtstParser {

    <T> T parse(Resource resource, WtstSchema schema, Map<String, ?> metadata, Class<T> type);

    default <T> T parse(Resource resource, WtstSchema schema, Class<T> type) {
        return parse(resource, schema, Collections.emptyMap(), type);
    }
}
