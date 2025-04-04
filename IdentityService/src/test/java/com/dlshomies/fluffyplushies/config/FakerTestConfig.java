package com.dlshomies.fluffyplushies.config;

import net.datafaker.Faker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class FakerTestConfig {
    @Bean
    public Faker faker() {
        return new Faker();
    }
}
