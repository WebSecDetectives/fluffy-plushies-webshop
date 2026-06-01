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
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserService userService;

    /**
     * Fixed id for the seeded normal user so other services (e.g. InventoryService seed data)
     * can attribute records to this real, loginable account. Must match
     * InventoryService DataInitializer.SEED_USER_ID.
     */
    public static final UUID SEED_USER_ID = UUID.fromString("00000000-0000-0000-0000-0000000000a1");

    @Value("${identity.admin.password}")
    private String adminPassword;

    @Value("${identity.merchant.password}")
    private String merchantPassword;

    @Value("${identity.user.password}")
    private String userPassword;

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

        var merchantUser = User.builder()
                .username("merchant")
                .email("merchant@merchant.com")
                .phone("+35834343434")
                .address(address)
                .role(Role.MERCHANT)
                .imgUrl(IMG_URL)
                .build();

        userService.registerMerchantUser(merchantUser, merchantPassword);
        log.info("Merchant user created successfully");

        var user = User.builder()
                .id(SEED_USER_ID)
                .username("user")
                .email("user@user.com")
                .phone("1234567890")
                .address(address)
                .role(Role.USER)
                .imgUrl(IMG_URL)
                .build();

        userService.registerUser(user, userPassword);
        log.info("User user created successfully");
    }
}
