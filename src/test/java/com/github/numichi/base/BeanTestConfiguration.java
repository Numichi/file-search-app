package com.github.numichi.base;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class BeanTestConfiguration {

    @Bean
    public String currentLinuxUser() {
        return "testuser";
    }
}
