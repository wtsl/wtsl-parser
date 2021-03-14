package org.wtst.parser.rule;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.wtst.parser.WtstObject;
import org.wtst.util.SpelUtils;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.Validate.notBlank;

public class WtstEntryRule {

    private final String name;

    private final Expression value;

    private final Expression alter;

    @JsonCreator
    public WtstEntryRule(@JsonProperty("name") String name,
                         @JsonProperty("value") String value,
                         @JsonProperty("alter") String alter) {

        this.name = notBlank(name, "entry rule must have non-blank name");
        this.value = SpelUtils.parse(notBlank(value, "entry rule must have 'value' rule"));
        this.alter = ofNullable(alter).map(SpelUtils::parse).orElse(null);
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return getValue(SpelUtils.build(), null);
    }

    public Object getValue(EvaluationContext context, WtstObject object) {
        return value.getValue(context, object);
    }

    public Object getAlter(EvaluationContext context, WtstObject object, Object value) {
        if (alter == null) {
            return value;
        }

        context.setVariable("value", value);
        value = alter.getValue(context, object);
        context.setVariable("value", null);

        return value;
    }
}
