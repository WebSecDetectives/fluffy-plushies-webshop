package com.dlshomies.fluffyplushies.security;

import com.dlshomies.fluffyplushies.entity.User;
import com.dlshomies.fluffyplushies.exception.UnexpectedUserTypeException;
import com.dlshomies.fluffyplushies.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component("userSecurity")
public class UserSecurity {
    private final UserRepository userRepository;

    public boolean isSelf(UUID targetUserId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = authentication.getPrincipal();

        if (user instanceof User) {
            return targetUserId.equals(((User) user).getId());
        }
        if (user != null) {
            throw new UnexpectedUserTypeException(user.getClass().getSimpleName());
        }
        throw new UnexpectedUserTypeException("null");
    }
}