package org.wtst.parser.rule;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.apache.commons.lang3.Validate.notEmpty;

@Getter
public class WtstRule {

    private final Map<String, Object> entries;

    private final List<WtstParserRule> parsers;

    @JsonCreator
    public WtstRule(@JsonProperty("entries") List<WtstEntryRule> entries,
                    @JsonProperty("parsers") List<WtstParserRule> parsers) {

        this.entries = emptyIfNull(entries).stream().collect(toMap(WtstEntryRule::getName, WtstEntryRule::getValue));
        this.parsers = notEmpty(parsers, "template must have at least one parser rule");
    }
}
