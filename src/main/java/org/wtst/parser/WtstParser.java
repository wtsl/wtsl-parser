package org.wtst.parser;

import org.springframework.core.io.Resource;

import java.util.Collections;
import java.util.Map;

public interface WtstParser {

    <T> T parse(Map<String, ?> metadata, WtstSchema schema, Resource resource, Class<T> type);

    default <T> T parse(WtstSchema schema, Resource resource, Class<T> type) {
        return parse(Collections.emptyMap(), schema, resource, type);
    }
}
