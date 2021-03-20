package org.wtst.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeLocator;

import java.util.Map;

public class SpelUtils {

    private static final ExpressionParser EXP_PARSER = new SpelExpressionParser
            (new SpelParserConfiguration(SpelCompilerMode.MIXED, null, true, true, Integer.MAX_VALUE));

    public static Expression parse(String exp) {
        return StringUtils.isBlank(exp) ? new LiteralExpression(exp) : EXP_PARSER.parseExpression(exp);
    }

    public static EvaluationContext build(Map<String, ?> metadata) {
        StandardTypeLocator locator = new StandardTypeLocator();
        locator.registerImport("java.util");

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setTypeLocator(locator);

        if (metadata != null) {
            metadata.forEach(context::setVariable);
        }

        return context;
    }

    public static EvaluationContext build() {
        return build(null);
    }
}
