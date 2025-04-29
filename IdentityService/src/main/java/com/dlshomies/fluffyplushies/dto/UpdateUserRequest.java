package com.dlshomies.fluffyplushies.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonDeserialize(builder = UpdateUserRequest.UpdateUserRequestBuilderImpl.class)
public class UpdateUserRequest {
    private String phone;
    private String password;
    private AddressRequest address;

    @JsonPOJOBuilder(withPrefix = "")
    public static class UpdateUserRequestBuilderImpl extends UpdateUserRequest.UpdateUserRequestBuilder<UpdateUserRequest, UpdateUserRequest.UpdateUserRequestBuilderImpl> {
    }
}
