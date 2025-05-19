package com.dlshomies.fluffyplushies.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark RabbitMQ listener methods for automatic error handling.
 * When a method annotated with this is executed, any exceptions will be caught
 * and handled by the RabbitErrorHandlingAspect.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RabbitErrorHandler {
}
