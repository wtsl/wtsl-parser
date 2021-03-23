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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Vadim Kolesnikov
 */
public interface WtslObject {

    Object value(int index);

    default Stream<Object> stream(int... indexes) {
        return Arrays.stream(indexes).mapToObj(this::value);
    }

    default Object[] array(int... indexes) {
        return stream(indexes).toArray();
    }

    default List<Object> list(int... indexes) {
        return stream(indexes).collect(Collectors.toCollection(LinkedList::new));
    }

    default Set<Object> set(int... indexes) {
        return stream(indexes).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    default int hash(int... indexes) {
        return Arrays.hashCode(array(indexes));
    }

    default Map<String, ?> getEntries() {
        return Collections.emptyMap();
    }

    default Object forEach(Object object) {
        return object;
    }

    default Object removeIf(Object object) {
        return object;
    }
}
