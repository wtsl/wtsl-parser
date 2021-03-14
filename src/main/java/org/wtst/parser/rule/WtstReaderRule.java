package org.wtst.parser.rule;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.wtst.parser.WtstObject;
import org.wtst.util.SpelUtils;

import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.Validate.notBlank;
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

        this.when = SpelUtils.parse(notBlank(when, "reader rule must have 'when' rule"));
        this.till = SpelUtils.parse(defaultIfBlank(till, "true"));
        this.skip = SpelUtils.parse(defaultIfBlank(skip, "false"));
        this.take = notNull(take, "reader rule must have 'take' rule");
    }

    public boolean isWhen(EvaluationContext context, WtstObject object) {
        return Objects.equals(when.getValue(context, object), Boolean.TRUE);
    }

    public boolean isTill(EvaluationContext context, WtstObject object) {
        return !Objects.equals(till.getValue(context, object), Boolean.FALSE);
    }

    public boolean isSkip(EvaluationContext context, WtstObject object) {
        return Objects.equals(skip.getValue(context, object), Boolean.TRUE);
    }

    public WtstEntryRule getTake() {
        return take;
    }
}
