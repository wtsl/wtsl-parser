package org.wtst.util;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeLocator;

public class SpelUtils {

    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser
            (new SpelParserConfiguration(SpelCompilerMode.MIXED, null, true, true, Integer.MAX_VALUE));

    public static Expression parse(String expression) {
        return EXPRESSION_PARSER.parseExpression(expression);
    }

    public static EvaluationContext build() {
        StandardTypeLocator locator = new StandardTypeLocator();
        locator.registerImport("java.util");

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setTypeLocator(locator);

        return context;
    }
}
