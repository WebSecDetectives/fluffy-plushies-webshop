package com.dlshomies.fluffyplushies.dto.rest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.net.URI;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonDeserialize(builder = UserResponse.UserResponseBuilderImpl.class)
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private String phone;
    private URI imgUrl;
    private AddressResponse address;

    @JsonPOJOBuilder(withPrefix = "")
    public static class UserResponseBuilderImpl extends UserResponse.UserResponseBuilder<UserResponse, UserResponseBuilderImpl> {
    }
}
