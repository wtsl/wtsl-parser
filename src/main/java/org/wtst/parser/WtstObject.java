package org.wtst.parser;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface WtstObject {

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
