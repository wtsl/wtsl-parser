package org.wtst.parser.rule;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.wtst.parser.WtstContext;
import org.wtst.util.SpelUtils;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class WtstEntryRule {

    private final String name;

    private final Expression value;

    private final Expression alter;

    @JsonCreator
    public WtstEntryRule(@JsonProperty("name") String name,
                         @JsonProperty("value") String value,
                         @JsonProperty("alter") String alter) {

        this.name = notBlank(name, "entry rule must have non-blank name");
        this.value = SpelUtils.parse(notNull(value, "entry rule must have 'value' rule"));
        this.alter = SpelUtils.parse(alter);
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return getValue(SpelUtils.build(), null);
    }

    public Object getValue(EvaluationContext ctx, WtstContext obj) {
        Object result = value.getValue(ctx, obj);

        if (alter != SpelUtils.EMPTY_EXP) {
            ctx.setVariable("value", result);
            result = alter.getValue(ctx, obj);
            ctx.setVariable("value", null);
        }

        return result;
    }
}
