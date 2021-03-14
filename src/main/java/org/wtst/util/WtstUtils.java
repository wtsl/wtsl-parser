package org.wtst.util;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.Callable;

public class WtstUtils {

    public static <T> T notError(Callable<T> callable, String message, Object... values) {
        try {
            return callable.call();
        } catch (Exception ex) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static Object merge(Object object1, Object object2) {
        if (object1 == null || object2 == null) {
            return object2;
        }

        if (object1 instanceof Map && object2 instanceof Map) {
            Map<Object, Object> result = new LinkedHashMap<>((Map<?, ?>) object1);
            ((Map<?, ?>) object2).forEach((key, value) -> result.put(key, merge(result.get(key), value)));

            return result;
        }

        if (object1 instanceof Set && object2 instanceof Set) {
            Set<Object> result = new LinkedHashSet<>((Set<?>) object1);
            result.addAll((Set<?>) object2);

            return result;
        }

        if (object1 instanceof List && object2 instanceof List) {
            List<Object> result = new LinkedList<>((List<?>) object1);
            result.addAll((List<?>) object2);

            return result;
        }

        if (object1.getClass().isArray() && object2.getClass().isArray()) {
            Class<?> type = object1.getClass().getComponentType();
            if (type == object2.getClass().getComponentType()) {
                int length1 = Array.getLength(object1);
                int length2 = Array.getLength(object2);

                Object result = Array.newInstance(type, length1 + length2);

                System.arraycopy(object1, 0, result, 0, length1);
                System.arraycopy(object2, 0, result, length1, length2);

                return result;
            }
        }

        return object2;
    }
}
