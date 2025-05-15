package com.dlshomies.fluffyplushies.messaging;

import com.dlshomies.fluffyplushies.domain.IdentityChannel;
import com.dlshomies.fluffyplushies.dto.messaging.UserAuthorizationRequest;
import com.dlshomies.fluffyplushies.dto.messaging.UserAuthorizationResponse;
import com.dlshomies.fluffyplushies.dto.messaging.UserInformationRequest;
import com.dlshomies.fluffyplushies.dto.messaging.UserInformationResponse;
import com.dlshomies.fluffyplushies.entity.User;
import com.dlshomies.fluffyplushies.security.JwtUtil;
import com.dlshomies.fluffyplushies.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class IdentityMessageListener {

    private static final String IDENTITY_EXCHANGE = "identity.exchange";
    private final RabbitTemplate rabbitTemplate;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;

    @RabbitListener(queues = "#{T(com.dlshomies.fluffyplushies.domain.IdentityChannel).USER_INFO_REQUEST.getQueueName()}")
    public void handleUserInformationRequest(
            @Payload UserInformationRequest request,
            @Header("correlationId") String correlationId) {

        log.info("Received user information request with correlationId: {}", correlationId);

        var user = getUserFromToken(request.getUserToken());
        var response = modelMapper.map(user, UserInformationResponse.class);

        rabbitTemplate.convertAndSend(
                IDENTITY_EXCHANGE,
                IdentityChannel.USER_INFO_RESPONSE.getRoutingKey(),
                response,
                message -> {
                    message.getMessageProperties().setCorrelationId(correlationId);
                    return message;
                }
        );
    }

    private User getUserFromToken(String token) {
        var parsedToken = jwtUtil.parseToken(token);
        return userService.getUser(parsedToken.getSubject());
    }

    @RabbitListener(queues = "#{T(com.dlshomies.fluffyplushies.domain.IdentityChannel).AUTH_REQUEST.getQueueName()}")
    public void handleAuthorizationRequest(
            @Payload UserAuthorizationRequest request,
            @Header("correlationId") String correlationId) {

        log.info("Received authorization request with correlationId: {}", correlationId);

        var user = getUserFromToken(request.getUserToken());
        var response = modelMapper.map(user, UserAuthorizationResponse.class);

        rabbitTemplate.convertAndSend(
                IDENTITY_EXCHANGE,
                IdentityChannel.AUTH_RESPONSE.getRoutingKey(),
                response,
                message -> {
                    message.getMessageProperties().setCorrelationId(correlationId);
                    return message;
                }
        );
    }
}
