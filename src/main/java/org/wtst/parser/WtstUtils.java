package org.wtst.parser;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

public class WtstUtils {

    private static final ExpressionParser EXP_PARSER = new SpelExpressionParser
            (new SpelParserConfiguration(SpelCompilerMode.MIXED, null, true, true, Integer.MAX_VALUE));

    public static Expression parse(String exp) {
        return EXP_PARSER.parseExpression(String.valueOf(exp));
    }

    public static Stream<?> stream(Object object) {
        if (object == null) {
            return Stream.empty();
        }

        if (object instanceof Stream) {
            return (Stream<?>) object;
        }

        if (object.getClass().isArray()) {
            return Arrays.stream((Object[]) object);
        }

        if (object instanceof Collection) {
            return ((Collection<?>) object).stream();
        }

        if (object instanceof Map) {
            return ((Map<?, ?>) object).entrySet().stream();
        }

        return Stream.of(object);
    }
}
