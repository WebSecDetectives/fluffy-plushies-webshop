package com.dlshomies.fluffyplushies.util;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.datafaker.Faker;

public class TestDataUtil {
    private static final Faker faker = new Faker();

    public static @NotNull @NotBlank String username() {
        var slug = faker.internet().slug();
        slug = slug.replace('.', '_');
        if (slug.length() < 3) {
            slug += "xxx".substring(0, 3 - slug.length());
        }
        if (slug.length() > 30) {
            slug = slug.substring(0, 30);
        }
        return slug;
    }

    public static @NotNull @NotBlank @Email String emailAddress() {
        return faker.internet().emailAddress();
    }

    public static @NotNull @NotBlank String phoneNumber() {
        return faker.phoneNumber().phoneNumber();
    }

    public static @NotNull String streetAddress() {
        return faker.address().streetAddress();
    }

    public static @NotNull String postcode() {
        return faker.address().postcode();
    }

    public static @NotNull String city() {
        return faker.address().city();
    }

    public static @NotNull String country() {
        return faker.address().country();
    }
}