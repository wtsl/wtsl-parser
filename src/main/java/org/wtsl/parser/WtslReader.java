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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.collections4.ListUtils;
import org.springframework.expression.Expression;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author Vadim Kolesnikov
 */
public class WtslReader {

    private static final String WHEN = "when";

    private static final String TILL = "till";

    private static final String SKIP = "skip";

    private final List<WtslReader> take;

    private final Expression when;

    private final Expression till;

    private final Expression skip;

    private final Map<String, Expression> then;

    @JsonCreator
    public WtslReader(@JsonProperty("take") List<WtslReader> take,
                      @JsonProperty(WHEN) String when,
                      @JsonProperty(TILL) String till,
                      @JsonProperty(SKIP) String skip,
                      @JsonProperty("then") Map<String, String> then) {

        this.take = ListUtils.emptyIfNull(take);

        this.when = WtslUtils.parse(WHEN, when);
        this.till = WtslUtils.parse(TILL, till);
        this.skip = WtslUtils.parse(SKIP, skip);

        this.then = new LinkedHashMap<>();
        if (then != null) {
            then.forEach((name, exp) -> this.then.put(name, WtslUtils.parse(name, exp)));
        }
    }

    public List<WtslReader> getTake() {
        return take;
    }

    public boolean isWhen(WtslContext ctx, WtslObject obj) {
        return Boolean.TRUE.equals(WtslUtils.value(WHEN, when, ctx, obj));
    }

    public boolean isTill(WtslContext ctx, WtslObject obj) {
        return !Boolean.FALSE.equals(WtslUtils.value(TILL, till, ctx, obj));
    }

    public boolean isSkip(WtslContext ctx, WtslObject obj) {
        return Boolean.TRUE.equals(WtslUtils.value(SKIP, skip, ctx, obj));
    }

    public void doThen(WtslContext ctx, WtslObject obj, Predicate<String> filter) {
        then.forEach((name, exp) -> {
            if (filter.test(name)) {
                ctx.setVariable(name, WtslUtils.value(name, exp, ctx, obj));
            }
        });
    }
}
