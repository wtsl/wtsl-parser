package org.wtst.parser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.wtst.util.SpelUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class WtstReader {

    private final List<WtstReader> take;

    private final Expression when;

    private final Expression till;

    private final Expression skip;

    private final Map<String, Expression> then;

    @JsonCreator
    public WtstReader(@JsonProperty("take") List<WtstReader> take,
                      @JsonProperty("when") String when,
                      @JsonProperty("till") String till,
                      @JsonProperty("skip") String skip,
                      @JsonProperty("then") Map<String, String> then) {

        this.take = take;

        this.when = SpelUtils.parse(when);
        this.till = SpelUtils.parse(till);
        this.skip = SpelUtils.parse(skip);

        this.then = new LinkedHashMap<>();
        if (then != null) {
            then.forEach((name, exp) -> this.then.put(name, SpelUtils.parse(exp)));
        }
    }

    public List<WtstReader> getTake() {
        return take;
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

    public void doThen(EvaluationContext ctx, Object obj, Predicate<String> filter) {
        then.forEach((name, exp) -> {
            if (filter.test(name)) {
                ctx.setVariable(name, exp.getValue(ctx, obj));
            }
        });
    }
}
