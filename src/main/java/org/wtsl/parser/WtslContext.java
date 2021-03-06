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

import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Vadim Kolesnikov
 */
public class WtslContext extends StandardEvaluationContext {

    private Map<String, Object> variables;

    public WtslContext() {
        setVariables(Collections.emptyMap());
    }

    public WtslContext(Map<String, Object> variables) {
        setVariables(variables);
    }

    public WtslContext same(String name, Object value) {
        setVariable(name, value);

        return this;
    }

    public WtslContext next(String name, Object value) {
        WtslContext context = new WtslContext(variables);
        context.setVariable(name, value);

        return context;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    @Override
    public void setVariables(Map<String, Object> variables) {
        this.variables = new TreeMap<>(variables);
    }

    @Override
    public Object lookupVariable(String name) {
        return variables.get(name);
    }

    @Override
    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }

    @Override
    public void registerFunction(String name, Method method) {
        throw new UnsupportedOperationException();
    }
}
