package com.dlshomies.fluffyplushies.service;

import com.dlshomies.fluffyplushies.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service implementation of {@link UserDetailsService} for loading user-specific data.
 * This class is used by Spring Security to retrieve user authentication and authorization
 * details based on a provided username.
 *
 * It interacts with the {@link UserRepository} to fetch user information stored in the database.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Loads a user's details by their username.
     * This method is used by the Spring Security framework to retrieve user authentication
     * and authorization information based on the provided username.
     *
     * @param username the username of the user whose details are to be loaded
     * @return a UserDetails object containing the user's information
     * @throws UsernameNotFoundException if no user is found with the given username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

    }
}
