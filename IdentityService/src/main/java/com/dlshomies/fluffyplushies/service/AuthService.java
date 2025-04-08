package com.dlshomies.fluffyplushies.service;

import com.dlshomies.fluffyplushies.domain.EncodedJwtToken;
import com.dlshomies.fluffyplushies.repository.UserRepository;
import com.dlshomies.fluffyplushies.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private JwtUtil jwtUtil;

    public EncodedJwtToken login(String username, String password) {

        var optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new BadCredentialsException("Invalid credentials");
        }

        var user = optionalUser.get();

        if(!isValidPassword(password, user.getEncodedPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return jwtUtil.generateToken(username, user.getRole());
    }

    private boolean isValidPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
