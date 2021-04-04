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

/**
 * @author Vadim Kolesnikov
 */
public class WtslSchema {

    private final List<WtslReader> readers;

    private final Map<String, Object> entries;

    private final Map<String, Expression> writers;

    @JsonCreator
    public WtslSchema(@JsonProperty("readers") List<WtslReader> readers,
                      @JsonProperty("entries") Map<String, String> entries,
                      @JsonProperty("writers") Map<String, String> writers) {

        this.readers = ListUtils.emptyIfNull(readers);

        this.entries = new LinkedHashMap<>();
        if (entries != null) {
            entries.forEach((name, exp) -> this.entries.put(name, WtslUtils.parse(exp).getValue()));
        }

        this.writers = new LinkedHashMap<>();
        if (writers != null) {
            writers.forEach((name, exp) -> this.writers.put(name, WtslUtils.parse(exp)));
        }
    }

    public List<WtslReader> getReaders() {
        return readers;
    }

    public Map<String, Object> getEntries() {
        return entries;
    }

    public Map<String, Expression> getWriters() {
        return writers;
    }
}
