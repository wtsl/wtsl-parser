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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Vadim Kolesnikov
 */
public class WtslUtils {

    private static final String ERROR_FORMAT = "key = %s\n\n%s\n";

    private static final ExpressionParser EXP_PARSER = new SpelExpressionParser
            (new SpelParserConfiguration(SpelCompilerMode.MIXED, null, true, true, Integer.MAX_VALUE));

    public static Expression parse(String name, String exp) {
        try {
            return EXP_PARSER.parseExpression(String.valueOf(exp));
        } catch (Exception ex) {
            throw new WtslException(String.format(ERROR_FORMAT, name, exp), ex);
        }
    }

    public static Object value(String name, Expression exp) {
        try {
            return exp.getValue();
        } catch (Exception ex) {
            throw new WtslException(String.format(ERROR_FORMAT, name, exp.getExpressionString()), ex, exp);
        }
    }

    public static Object value(String name, Expression exp, WtslContext ctx, WtslObject obj) {
        try {
            return exp.getValue(ctx, obj);
        } catch (Exception ex) {
            throw new WtslException(obj + "; " + String.format(ERROR_FORMAT, name, exp.getExpressionString()), ex, exp, ctx, obj);
        }
    }

    public static Object value(String name, String exp) {
        return value(name, parse(name, exp));
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

    public static <T, R> Iterable<R> iterator(int limit, Iterable<T> iterable, Function<T, R> function) {
        return () -> new Iterator<R>() {

            final Iterator<T> iterator = iterable.iterator();

            int count = 0;

            @Override
            public boolean hasNext() {
                return count < limit && iterator.hasNext();
            }

            @Override
            public R next() {
                if (hasNext()) {
                    count++;
                    return function.apply(iterator.next());
                }
                throw new NoSuchElementException();
            }
        };
    }
}
