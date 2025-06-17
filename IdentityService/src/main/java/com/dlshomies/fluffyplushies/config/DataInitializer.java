package com.dlshomies.fluffyplushies.config;

import com.dlshomies.fluffyplushies.entity.Address;
import com.dlshomies.fluffyplushies.entity.Role;
import com.dlshomies.fluffyplushies.entity.User;
import com.dlshomies.fluffyplushies.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserService userService;

    @Value("${identity.admin.password}")
    private String adminPassword;

    private final URI IMG_URL = URI.create("https://dk.pinterest.com/pin/736549714079317500/");

    @PostConstruct
    public void init() throws MalformedURLException {
        log.info("Starting data initialization...");

            var address = Address.builder()
                    .street("Main Street")
                    .city("Capital City")
                    .country("Canada")
                    .postalCode("12345")
                    .build();

            var adminUser = User.builder()
                    .username("admin")
                    .email("admin@admin.com")
                    .phone("1234567890")
                    .address(address)
                    .role(Role.ADMIN)
                    .imgUrl(IMG_URL)
                    .build();

            userService.registerAdminUser(adminUser, adminPassword);
            log.info("Admin user created successfully");
        }
}
