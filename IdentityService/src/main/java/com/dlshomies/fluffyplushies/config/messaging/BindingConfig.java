package com.dlshomies.fluffyplushies.config.messaging;

import com.dlshomies.fluffyplushies.domain.IdentityChannel;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.dlshomies.fluffyplushies.config.messaging.QueueConfig.*;

@Configuration
public class BindingConfig {

    @Bean
    Binding userInfoRequestBinding() {
        return BindingBuilder
                .bind(userInfoRequestQueue())
                .to(identityExchange())
                .with(IdentityChannel.USER_INFO_REQUEST.getRoutingKey());
    }

    @Bean
    public Binding userInfoResponseBinding() {
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
    Binding deadLetterBinding() {
        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(IdentityChannel.DEAD_LETTER.getRoutingKey());
    }
}
