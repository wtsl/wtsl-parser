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

package org.wtst.parser;

import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Vadim Kolesnikov
 */
public class WtstContext extends StandardEvaluationContext {

    private final Map<String, Object> variables;

    public WtstContext(Map<String, Object> variables) {
        this.variables = new LinkedHashMap<>(variables);
    }

    public WtstContext next(String name, Object value) {
        WtstContext context = new WtstContext(variables);
        context.setVariable(name, value);

        return context;
    }

    public Map<String, Object> getVariables() {
        return variables;
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
    public void setVariables(Map<String, Object> variables) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerFunction(String name, Method method) {
        throw new UnsupportedOperationException();
    }
}
