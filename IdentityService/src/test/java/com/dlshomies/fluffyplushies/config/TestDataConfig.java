package com.dlshomies.fluffyplushies.config;

import com.dlshomies.fluffyplushies.util.TestDataUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestDataConfig {
    @Bean
    public TestDataUtil testDataUtil() {
        return new TestDataUtil();
    }
}

