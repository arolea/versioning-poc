package com.rolea.versioning.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rolea.versioning.config.mapper.CustomComparators;
import com.rolea.versioning.config.mapper.DeterministicObjectMapper;
import com.rolea.versioning.entities.Course;
import com.rolea.versioning.entities.Student;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;

@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        CustomComparators comparators = new CustomComparators();

        comparators.addConverter(Course.class, Comparator.comparing(Course::getId));
        comparators.addConverter(Student.class, Comparator.comparing(Student::getId));

        return DeterministicObjectMapper.create(objectMapper, comparators);
    }

}
