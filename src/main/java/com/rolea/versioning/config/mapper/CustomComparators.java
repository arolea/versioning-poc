package com.rolea.versioning.config.mapper;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomComparators {

    private final Map<Class<?>, Comparator<? extends Object>> customComparators;

    public CustomComparators() {
        customComparators = new LinkedHashMap<>();
    }

    public <T> void addConverter(Class<T> clazz, Comparator<?> comparator) {
        customComparators.put(clazz, comparator);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public int compare(Object first, Object second) {
        if (first instanceof Comparable) {
            return ((Comparable) first).compareTo(second);
        }

        for (Map.Entry<Class<?>, Comparator<?>> entry : customComparators.entrySet()) {
            Class<?> clazz = entry.getKey();
            if (first.getClass().isAssignableFrom(clazz)) {
                Comparator<Object> comparator = (Comparator<Object>) entry.getValue();
                return comparator.compare(first, second);
            }
        }

        String message = String.format("Cannot compare object of type %s without a custom comparator", first.getClass().getName());
        throw new UnsupportedOperationException(message);
    }

}
