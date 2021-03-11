package org.wtst.parser.rule;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.wtst.parser.WtstContext;
import org.wtst.util.SpelUtils;

import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.Validate.notEmpty;

public class WtstParserRule {

    private final Expression chapter;

    private final List<WtstReaderRule> readers;

    private final List<WtstMapperRule> mappers;

    @JsonCreator
    public WtstParserRule(@JsonProperty("chapter") String chapter,
                          @JsonProperty("readers") List<WtstReaderRule> readers,
                          @JsonProperty("mappers") List<WtstMapperRule> mappers) {

        this.chapter = SpelUtils.parse(defaultString(chapter, "true"));
        this.readers = notEmpty(readers, "parser rule must have at least one reader rule");
        this.mappers = notEmpty(mappers, "parser rule must have at least one mapper rule");
    }

    public boolean isChapter(EvaluationContext ctx, WtstContext obj) {
        return Objects.equals(chapter.getValue(ctx, obj), Boolean.TRUE);
    }

    public List<WtstReaderRule> getReaders() {
        return readers;
    }

    public List<WtstMapperRule> getMappers() {
        return mappers;
    }
}
