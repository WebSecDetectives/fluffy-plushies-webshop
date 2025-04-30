package com.dlshomies.fluffyplushies.util;

import com.dlshomies.fluffyplushies.dto.AddressRequest;
import com.dlshomies.fluffyplushies.dto.CreateUserRequest;
import com.dlshomies.fluffyplushies.entity.Address;
import com.dlshomies.fluffyplushies.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.datafaker.Faker;

import java.time.Instant;

public class TestDataUtil {
    private final Faker faker = new Faker();

    private static final String STRONG_PASSWORD = "Str0ngP@ssw0rd";

    public CreateUserRequest userRequestWithDefaults() {
        return CreateUserRequest.builder()
                .username(username())
                .email(emailAddress())
                .phone(phoneNumber())
                .password(STRONG_PASSWORD)
                .address(addressRequest())
                .build();
    }

    public CreateUserRequest userRequestWithUsername(String username) {
        return CreateUserRequest.builder()
                .username(username)
                .email(emailAddress())
                .phone(phoneNumber())
                .password(STRONG_PASSWORD)
                .address(addressRequest())
                .build();
    }

    public CreateUserRequest userRequestWithEmail(String email) {
        return CreateUserRequest.builder()
                .username(username())
                .email(email)
                .phone(phoneNumber())
                .password(STRONG_PASSWORD)
                .address(addressRequest())
                .build();
    }

    public User userWithDefaults() {
        return User.builder()
                .username(username())
                .email(emailAddress())
                .phone(phoneNumber())
                .address(addressWithDefaults())
                .build();
    }

    public User userWithUsername(String username) {
        return User.builder()
                .username(username)
                .email(emailAddress())
                .phone(phoneNumber())
                .address(addressWithDefaults())
                .build();
    }

    public @NotNull @NotBlank String username() {
        var slug = faker.internet().slug().replace('.', '_');
        var stamp = stamp();
        var maxSlugLength = 30 - stamp.length();

        if (slug.length() > maxSlugLength) {
            slug = slug.substring(0, maxSlugLength);
        }

        return slug + stamp;
    }

    public @NotNull @NotBlank @Email String emailAddress() {
        var email = faker.internet().emailAddress();
        var at = email.indexOf('@');

        return email.substring(0, at) + stamp() + email.substring(at);
    }

    private static String stamp() {
        return String.valueOf(Instant.now().toEpochMilli());
    }

    public @NotNull @NotBlank String phoneNumber() {
        return faker.phoneNumber().phoneNumber();
    }

    public AddressRequest addressRequest() {
        return AddressRequest.builder()
                .street(streetAddress())
                .postalCode(postcode())
                .city(city())
                .country(country())
                .build();
    }

    private Address addressWithDefaults() {
        return Address.builder()
                .street(streetAddress())
                .postalCode(postcode())
                .city(city())
                .country(country())
                .build();
    }
    private @NotNull String streetAddress() {
        return faker.address().streetAddress();
    }

    private @NotNull String postcode() {
        return faker.address().postcode();
    }

    private @NotNull String city() {
        return faker.address().city();
    }

    private @NotNull String country() {
        return faker.address().country();
    }

}