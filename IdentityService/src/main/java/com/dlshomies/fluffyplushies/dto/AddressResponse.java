package com.dlshomies.fluffyplushies.dto;

import jakarta.validation.constraints.NotNull;

public class AddressResponse {
    @NotNull
    private String street;

    @NotNull
    private int postalCode;

    @NotNull
    private String city;

    @NotNull
    private String country;
}
