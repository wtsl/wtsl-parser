package org.wtst.parser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.expression.Expression;
import org.wtst.util.SpelUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WtstSchema {

    private final List<WtstReader> readers;

    private final Map<String, Object> entries;

    private final Map<String, Expression> writers;

    @JsonCreator
    public WtstSchema(@JsonProperty("readers") List<WtstReader> readers,
                      @JsonProperty("entries") Map<String, String> entries,
                      @JsonProperty("writers") Map<String, String> writers) {

        this.readers = readers;

        this.entries = new HashMap<>();
        if (entries != null) {
            entries.forEach((name, exp) -> this.entries.put(name, SpelUtils.parse(exp).getValue(SpelUtils.build())));
        }

        this.writers = new LinkedHashMap<>();
        if (writers != null) {
            writers.forEach((name, exp) -> this.writers.put(name, SpelUtils.parse(exp)));
        }
    }

    public List<WtstReader> getReaders() {
        return readers;
    }

    public Map<String, Object> getEntries() {
        return entries;
    }

    public Map<String, Expression> getWriters() {
        return writers;
    }
}
