package com.dlshomies.fluffyplushies.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonDeserialize(builder = AddressRequest.AddressRequestBuilderImpl.class)
public class AddressRequest {

    @NotNull
    private String street;

    @NotNull
    private String postalCode;

    @NotNull
    private String city;

    @NotNull
    private String country;

    @JsonPOJOBuilder(withPrefix = "")
    public static class AddressRequestBuilderImpl extends AddressRequest.AddressRequestBuilder<AddressRequest, AddressRequestBuilderImpl> {
    }
}
