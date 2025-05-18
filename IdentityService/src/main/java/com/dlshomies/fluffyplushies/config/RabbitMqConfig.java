package com.dlshomies.fluffyplushies.config;

import com.dlshomies.fluffyplushies.domain.IdentityChannel;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {

    private static final String IDENTITY_EXCHANGE = "identity.exchange";
    private static final String DEAD_LETTER_EXCHANGE = "identity.dlx";
    private static final String DEAD_LETTER_QUEUE = "identity.dlq";

    @Bean
    Queue deadLetterQueue() {
        return new Queue(DEAD_LETTER_QUEUE, true);
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    @Bean
    Binding deadLetterBinding() {
        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(DEAD_LETTER_QUEUE);
    }

    @Bean
    Queue userInfoRequestQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        args.put("x-dead-letter-routing-key", DEAD_LETTER_QUEUE);
        return new Queue(IdentityChannel.USER_INFO_REQUEST.getQueueName(), true, false, false, args);
    }

    @Bean
    Queue userInfoResponseQueue() {
        return new Queue(IdentityChannel.USER_INFO_RESPONSE.getQueueName(), true);
    }

    @Bean
    Queue authRequestQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        args.put("x-dead-letter-routing-key", DEAD_LETTER_QUEUE);
        return new Queue(IdentityChannel.AUTH_REQUEST.getQueueName(), true, false, false, args);
    }

    @Bean
    Queue authResponseQueue() {
        return new Queue(IdentityChannel.AUTH_RESPONSE.getQueueName(), true);
    }

    @Bean
    DirectExchange identityExchange() {
        return new DirectExchange(IDENTITY_EXCHANGE);
    }

    @Bean
    Binding userInfoRequestBinding() {
        return BindingBuilder
                .bind(userInfoRequestQueue())
                .to(identityExchange())
                .with(IdentityChannel.USER_INFO_REQUEST.getRoutingKey());
    }

    @Bean
    Binding userInfoResponseBinding() {
        return BindingBuilder
                .bind(userInfoResponseQueue())
                .to(identityExchange())
                .with(IdentityChannel.USER_INFO_RESPONSE.getRoutingKey());
    }

    @Bean
    Binding authRequestBinding() {
        return BindingBuilder
                .bind(authRequestQueue())
                .to(identityExchange())
                .with(IdentityChannel.AUTH_REQUEST.getRoutingKey());
    }

    @Bean
    Binding authResponseBinding() {
        return BindingBuilder
                .bind(authResponseQueue())
                .to(identityExchange())
                .with(IdentityChannel.AUTH_RESPONSE.getRoutingKey());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}