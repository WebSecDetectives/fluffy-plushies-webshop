package com.dlshomies.fluffyplushies.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonDeserialize(builder = AddressResponse.AddressResponseBuilderImpl.class)
public class AddressResponse {
    private UUID id;
    private String street;
    private int postalCode;
    private String city;
    private String country;

    @JsonPOJOBuilder(withPrefix = "")
    public static class AddressResponseBuilderImpl extends AddressResponse.AddressResponseBuilder<AddressResponse, AddressResponseBuilderImpl> {
    }
}
