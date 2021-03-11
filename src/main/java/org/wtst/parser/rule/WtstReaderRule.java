package org.wtst.parser.rule;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.wtst.parser.WtstContext;
import org.wtst.util.SpelUtils;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.notNull;

public class WtstReaderRule {

    private final Expression when;

    private final Expression till;

    private final Expression skip;

    private final WtstEntryRule take;

    @JsonCreator
    public WtstReaderRule(@JsonProperty("when") String when,
                          @JsonProperty("till") String till,
                          @JsonProperty("skip") String skip,
                          @JsonProperty("take") WtstEntryRule take) {

        this.when = SpelUtils.parse(notNull(when, "reader rule must have 'when' rule"));
        this.till = SpelUtils.parse(till);
        this.skip = SpelUtils.parse(skip);
        this.take = notNull(take, "reader rule must have 'take' rule");
    }

    public boolean getWhen(EvaluationContext ctx, WtstContext obj) {
        return Objects.equals(when.getValue(ctx, obj), Boolean.TRUE);
    }

    public boolean getTill(EvaluationContext ctx, WtstContext obj) {
        return !Objects.equals(till.getValue(ctx, obj), Boolean.FALSE);
    }

    public boolean getSkip(EvaluationContext ctx, WtstContext obj) {
        return Objects.equals(skip.getValue(ctx, obj), Boolean.TRUE);
    }

    public WtstEntryRule getTake() {
        return take;
    }
}
