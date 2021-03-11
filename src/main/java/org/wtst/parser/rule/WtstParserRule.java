package org.wtst.parser.rule;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.expression.Expression;
import org.wtst.util.SpelUtils;

import java.util.List;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.wtst.util.WtstUtils.notError;

@Getter
public class WtstParserRule {

    private final Expression filter;

    private final WtstFormat format;

    private final List<WtstReaderRule> readers;

    private final List<WtstMapperRule> mappers;

    @JsonCreator
    public WtstParserRule(@JsonProperty("filter") String filter,
                          @JsonProperty("format") String format,
                          @JsonProperty("readers") List<WtstReaderRule> readers,
                          @JsonProperty("mappers") List<WtstMapperRule> mappers) {

        this.filter = SpelUtils.parse(filter);
        this.format = notError(() -> WtstFormat.valueOf(format), "parser rule must have defined format");
        this.readers = notEmpty(readers, "parser rule must have at least one reader rule");
        this.mappers = notEmpty(mappers, "parser rule must have at least one mapper rule");
    }
}
