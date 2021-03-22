package org.wtst.parser;

import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

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
