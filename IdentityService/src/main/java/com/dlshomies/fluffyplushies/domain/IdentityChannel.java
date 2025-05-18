package com.dlshomies.fluffyplushies.domain;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * Defines the routing keys used for message routing in RabbitMQ.
 * In the current application design, routing keys match exactly with queue names
 * when using direct exchange binding.
 */
@ToString
@AllArgsConstructor
public enum IdentityChannel {
    USER_INFO_REQUEST("identity.user_information_requests"),
    USER_INFO_RESPONSE("identity.user_information_responses"),
    AUTH_REQUEST("identity.user_authentication_authorization_requests"),
    AUTH_RESPONSE("identity.user_authentication_authorization_responses"),
    DEAD_LETTER("identity.dead_letters");

    private final String key;

    /**
     * Gets the queue name, which is identical to the routing key.
     * This method is provided for semantic clarity when using this enum
     * to refer to a queue.
     *
     * @return The queue name string
     */
    public String getQueueName() {
        return key;
    }

    /**
     * Gets the routing key, which is identical to the queue name.
     * This method provides semantic clarity when using this enum
     * to refer to a routing key.
     *
     * @return The routing key string
     */
    public String getRoutingKey() {
        return key;
    }
}
