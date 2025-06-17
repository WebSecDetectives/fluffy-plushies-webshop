package com.dlshomies.fluffyplushies.dto.rest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.net.URI;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonDeserialize(builder = UpdateUserRequest.UpdateUserRequestBuilderImpl.class)
public class UpdateUserRequest {
    private String phone;

    private AddressRequest address;

    private URI imgUrl;

    @JsonPOJOBuilder(withPrefix = "")
    public static class UpdateUserRequestBuilderImpl extends UpdateUserRequest.UpdateUserRequestBuilder<UpdateUserRequest, UpdateUserRequest.UpdateUserRequestBuilderImpl> {
    }
}
