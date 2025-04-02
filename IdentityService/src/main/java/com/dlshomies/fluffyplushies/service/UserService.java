package com.dlshomies.fluffyplushies.service;

import com.dlshomies.fluffyplushies.entity.User;
import com.dlshomies.fluffyplushies.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public User registerUser(User user, String password) {
        user.setEncodedPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
