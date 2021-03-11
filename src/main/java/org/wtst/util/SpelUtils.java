package org.wtst.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeLocator;

public class SpelUtils {

    public static final ExpressionParser EXP_PARSER = new SpelExpressionParser
            (new SpelParserConfiguration(SpelCompilerMode.MIXED, null, true, true, Integer.MAX_VALUE));

    public static final Expression EMPTY_EXP = EXP_PARSER.parseExpression("''");

    public static Expression parse(String exp) {
        return StringUtils.isBlank(exp) ? EMPTY_EXP : EXP_PARSER.parseExpression(exp);
    }

    public static EvaluationContext build() {
        StandardTypeLocator locator = new StandardTypeLocator();
        locator.registerImport("java.util");

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setTypeLocator(locator);

        return context;
    }
}
