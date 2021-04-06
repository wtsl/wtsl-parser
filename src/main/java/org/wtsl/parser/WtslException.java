package org.wtsl.parser;

import org.springframework.expression.Expression;

/**
 * @author Vadim Kolesnikov
 */
public class WtslException extends RuntimeException {

    private final Expression exp;

    private final WtslContext ctx;

    private final WtslObject obj;

    public WtslException(String message, Throwable cause) {
        this(message, cause, null);
    }

    public WtslException(String message, Throwable cause, Expression exp) {
        this(message, cause, exp, null, null);
    }

    public WtslException(String message, Throwable cause, Expression exp, WtslContext ctx, WtslObject obj) {
        super(message, cause);

        this.exp = exp;
        this.ctx = ctx;
        this.obj = obj;
    }

    public Expression getExp() {
        return exp;
    }

    public WtslContext getCtx() {
        return ctx;
    }

    public WtslObject getObj() {
        return obj;
    }
}
