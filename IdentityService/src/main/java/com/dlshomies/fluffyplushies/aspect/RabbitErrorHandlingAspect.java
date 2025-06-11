package com.dlshomies.fluffyplushies.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.stereotype.Component;

/**
 * Aspect that provides error handling for RabbitMQ listeners.
 * It intercepts methods annotated with @RabbitErrorHandler, executes them,
 * and handles any exceptions by logging them and throwing AmqpRejectAndDontRequeueException
 * to ensure the message is sent to the dead letter queue.
 */
@Aspect
@Component
@Slf4j
public class RabbitErrorHandlingAspect {

    public static final String RABBIT_ERROR_HANDLER = "@annotation(com.dlshomies.fluffyplushies.annotation.RabbitErrorHandler)";

    /**
     * Around advice that wraps the execution of methods annotated with @RabbitErrorHandler.
     * If an exception occurs, it logs the error and throws AmqpRejectAndDontRequeueException.
     *
     * @param joinPoint the join point representing the intercepted method
     * @return the result of the method execution if successful
     * @throws Throwable if the method execution throws an exception
     */
    @Around(RABBIT_ERROR_HANDLER)
    public Object handleRabbitErrors(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            String methodName = joinPoint.getSignature().getName();
            Object[] args = joinPoint.getArgs();
            String correlationId = extractCorrelationId(args);

            log.error("Error processing message in method {} with correlationId {}: {}",
                    methodName, correlationId, e.getMessage(), e);

            throw new AmqpRejectAndDontRequeueException("Failed to process message with correlationId: " + correlationId, e);
        }
    }

    private String extractCorrelationId(Object[] args) {
        // Assuming correlationId is always the second parameter
        return args.length > 1 ? String.valueOf(args[1]) : "unknown";
    }
}
