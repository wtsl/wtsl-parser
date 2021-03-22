package org.wtst.parser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

        this.when = WtstUtils.parse(when);
        this.till = WtstUtils.parse(till);
        this.skip = WtstUtils.parse(skip);

        this.then = new LinkedHashMap<>();
        if (then != null) {
            then.forEach((name, exp) -> this.then.put(name, WtstUtils.parse(exp)));
        }
    }

    public List<WtstReader> getTake() {
        return take;
    }

    public boolean isWhen(EvaluationContext context, WtstObject object) {
        return Boolean.TRUE.equals(when.getValue(context, object));
    }

    public boolean isTill(EvaluationContext context, WtstObject object) {
        return !Boolean.FALSE.equals(till.getValue(context, object));
    }

    public boolean isSkip(EvaluationContext context, WtstObject object) {
        return Boolean.TRUE.equals(skip.getValue(context, object));
    }

    public void doThen(EvaluationContext ctx, Object obj, Predicate<String> filter) {
        then.forEach((name, exp) -> {
            if (filter.test(name)) {
                ctx.setVariable(name, exp.getValue(ctx, obj));
            }
        });
    }
}
