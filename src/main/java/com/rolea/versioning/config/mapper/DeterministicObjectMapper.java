package com.rolea.versioning.config.mapper;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.StdConverter;

import java.util.*;
import java.util.stream.Collectors;

public class DeterministicObjectMapper {

    private DeterministicObjectMapper() {
    }

    public static ObjectMapper create(ObjectMapper original, CustomComparators customComparators) {
        ObjectMapper mapper = original.copy()
                .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
                .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);

        SimpleModule module = new SimpleModule();

        var serializers = mapper.getSerializerProviderInstance();
        var customSerializer = new CollectionToSortedListConverter(customComparators);
        var delegatingSerializer = new CustomDelegatingSerializerProvider(serializers, customSerializer);

        module.addSerializer(Collection.class, delegatingSerializer);
        mapper.registerModule(module);

        return mapper;
    }

    private static class CustomDelegatingSerializerProvider extends StdDelegatingSerializer {

        private final SerializerProvider serializerProvider;

        private CustomDelegatingSerializerProvider(SerializerProvider serializerProvider, Converter<?, ?> converter) {
            super(converter);
            this.serializerProvider = serializerProvider;
        }

        @Override
        protected StdDelegatingSerializer withDelegate(Converter<Object, ?> converter, JavaType delegateType, JsonSerializer<?> delegateSerializer) {
            return new StdDelegatingSerializer(converter, delegateType, delegateSerializer);
        }

        @Override
        public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
            return super.createContextual(serializerProvider, property);
        }
    }

    private static class CollectionToSortedListConverter extends StdConverter<Collection<?>, Collection<?>> {

        private final CustomComparators customComparators;

        public CollectionToSortedListConverter(CustomComparators customComparators) {
            this.customComparators = customComparators;
        }

        @Override
        public Collection<? extends Object> convert(Collection<?> value) {
            if (value == null || value.isEmpty()) {
                return Collections.emptyList();
            }

            Comparator<Object> comparator = Comparator.comparing(x -> x.getClass().getName())
                    .thenComparing(customComparators::compare);

            Collection<? extends Object> filtered = value.stream()
                    .filter(Objects::nonNull)
                    .sorted(comparator)
                    .collect(Collectors.toList());

            if (filtered.isEmpty()) {
                return Collections.emptyList();
            }

            return filtered;
        }
    }

}
