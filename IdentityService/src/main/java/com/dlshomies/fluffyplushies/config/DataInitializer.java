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

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserService userService;

    /**
     * Fixed ids for the seeded user and merchant so other services (e.g. InventoryService
     * seed data) can attribute records to these real, loginable accounts. Must match
     * InventoryService DataInitializer.SEED_USER_ID / SEED_MERCHANT_ID.
     */
    public static final UUID SEED_USER_ID = UUID.fromString("00000000-0000-0000-0000-0000000000a1");
    public static final UUID SEED_MERCHANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Value("${identity.admin.password}")
    private String adminPassword;

    @Value("${identity.merchant.password}")
    private String merchantPassword;

    @Value("${identity.user.password}")
    private String userPassword;

    @PostConstruct
    public void init() {
        log.info("Starting data initialization...");

        seedUsers();

        log.info("Finished data initialization...");
    }

    /** Seeds the three demo accounts (admin, merchant, user) sharing one address. */
    private void seedUsers() {
        var address = buildSharedAddress();

        userService.registerAdminUser(buildAdminUser(address), adminPassword);
        log.info("Admin user created successfully");

        userService.registerMerchantUser(buildMerchantUser(address), merchantPassword);
        log.info("Merchant user created successfully");

        userService.registerUser(buildUser(address), userPassword);
        log.info("User user created successfully");
    }

    private Address buildSharedAddress() {
        return Address.builder()
                .street("Main Street")
                .city("Capital City")
                .country("Canada")
                .postalCode("12345")
                .build();
    }

    private User buildAdminUser(Address address) {
        return User.builder()
                .username("admin")
                .email("admin@admin.com")
                .phone("1234567890")
                .address(address)
                .role(Role.ADMIN)
                .build();
    }

    private User buildMerchantUser(Address address) {
        return User.builder()
                .id(SEED_MERCHANT_ID)
                .username("merchant")
                .email("merchant@merchant.com")
                .phone("+35834343434")
                .address(address)
                .role(Role.MERCHANT)
                .build();
    }

    private User buildUser(Address address) {
        return User.builder()
                .id(SEED_USER_ID)
                .username("user")
                .email("user@user.com")
                .phone("1234567890")
                .address(address)
                .role(Role.USER)
                .build();
    }
}
