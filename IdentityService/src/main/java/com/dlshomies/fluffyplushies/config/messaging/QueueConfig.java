package com.dlshomies.fluffyplushies.config.messaging;

import com.dlshomies.fluffyplushies.domain.IdentityChannel;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class QueueConfig {
    static final String DEAD_LETTER_EXCHANGE = "identity.deadletter.exchange";
    static final String IDENTITY_EXCHANGE = "identity.exchange";

    @Bean
    public static DirectExchange identityExchange() {
        return new DirectExchange(IDENTITY_EXCHANGE);
    }

    @Bean
    public static DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    @Bean
    public static Queue userInfoRequestQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        args.put("x-dead-letter-routing-key", IdentityChannel.DEAD_LETTER.getRoutingKey());
        return new Queue(IdentityChannel.USER_INFO_REQUEST.getQueueName(), true, false, false, args);
    }

    @Bean
    public static Queue userInfoResponseQueue() {
        return new Queue(IdentityChannel.USER_INFO_RESPONSE.getQueueName(), true);
    }

    @Bean
    public static Queue authRequestQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        args.put("x-dead-letter-routing-key", IdentityChannel.DEAD_LETTER.getRoutingKey());
        return new Queue(IdentityChannel.AUTH_REQUEST.getQueueName(), true, false, false, args);
    }

    @Bean
    public static  Queue authResponseQueue() {
        return new Queue(IdentityChannel.AUTH_RESPONSE.getQueueName(), true);
    }


    @Bean
    public static Queue deadLetterQueue() {
        return new Queue(IdentityChannel.DEAD_LETTER.getQueueName(), true);
    }




}
