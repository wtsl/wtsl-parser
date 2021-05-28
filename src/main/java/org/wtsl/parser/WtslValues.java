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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vadim Kolesnikov
 */
public interface WtslValues<T extends WtslValues<T>> extends Iterable<T> {

    T get(int index);

    T get(String key);

    int size();

    default List<T> all(int limit) {
        List<T> objects = new ArrayList<>(limit);
        for (T object : this) {
            if (limit <= objects.size()) {
                break;
            }
            objects.add(object);
        }
        return objects;
    }

    default List<T> all() {
        List<T> objects = new ArrayList<>(size());
        for (T object : this) {
            objects.add(object);
        }
        return objects;
    }

    default boolean isEmpty() {
        return size() == 0;
    }
}
