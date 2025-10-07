package com.github.numichi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class LinuxUser {

    @Bean
    @ConditionalOnMissingBean(name = "currentLinuxUser")
    public String currentLinuxUser() {
        try {
            return System.getProperty("user.name");
        } catch (Exception e) {
            log.warn("Error retrieving Linux user name", e);
            return "unknown";
        }
    }
}
