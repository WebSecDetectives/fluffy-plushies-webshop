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


@Configuration
public class RabbitMqConfig {

    private static final String IDENTITY_EXCHANGE = "identity.exchange";

    @Bean
    Queue userInfoRequestQueue() {
        return new Queue(IdentityChannel.USER_INFO_REQUEST.getQueueName(), true);
    }

    @Bean
    Queue userInfoResponseQueue() {
        return new Queue(IdentityChannel.USER_INFO_RESPONSE.getQueueName(), true);
    }

    @Bean
    Queue authRequestQueue() {
        return new Queue(IdentityChannel.AUTH_REQUEST.getQueueName(), true);
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