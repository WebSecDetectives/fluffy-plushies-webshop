package com.dlshomies.fluffyplushies.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuration class to enable Aspect-Oriented Programming in the application.
 * This allows aspects like RabbitErrorHandlingAspect to intercept method calls.
 */
@Configuration
@EnableAspectJAutoProxy
public class AopConfig {
}
