/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wtsl.parser;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Vadim Kolesnikov
 */
public class WtslUtils {

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
