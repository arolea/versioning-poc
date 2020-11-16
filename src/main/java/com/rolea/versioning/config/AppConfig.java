package com.rolea.versioning.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rolea.versioning.config.mapper.CustomComparators;
import com.rolea.versioning.config.mapper.DeterministicObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper(){
        return DeterministicObjectMapper.create(new ObjectMapper(), new CustomComparators());
    }

}
