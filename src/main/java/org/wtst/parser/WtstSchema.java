package org.wtst.parser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.wtst.parser.rule.WtstEntryRule;
import org.wtst.parser.rule.WtstParserRule;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.apache.commons.lang3.Validate.notEmpty;

public class WtstSchema {

    private final Map<String, Object> entries;

    private final List<WtstParserRule> parsers;

    @JsonCreator
    public WtstSchema(@JsonProperty("entries") List<WtstEntryRule> entries,
                      @JsonProperty("parsers") List<WtstParserRule> parsers) {

        this.entries = emptyIfNull(entries).stream().collect(toMap(WtstEntryRule::getName, WtstEntryRule::getValue));
        this.parsers = notEmpty(parsers, "schema must have at least one parser rule");
    }

    public Map<String, Object> getEntries() {
        return entries;
    }

    public List<WtstParserRule> getParsers() {
        return parsers;
    }
}
