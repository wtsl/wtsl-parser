package org.wtt.util;

import java.util.concurrent.Callable;

public class WtstUtils {

    public static <T> T notError(Callable<T> callable, String message, Object... values) {
        try {
            return callable.call();
        } catch (Exception ex) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }
}
