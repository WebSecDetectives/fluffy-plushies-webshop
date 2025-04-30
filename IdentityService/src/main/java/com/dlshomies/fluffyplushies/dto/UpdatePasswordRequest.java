package com.dlshomies.fluffyplushies.dto;

import com.dlshomies.fluffyplushies.util.StrongPassword;
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
@JsonDeserialize(builder = UpdatePasswordRequest.UpdatePasswordRequestBuilderImpl.class)
public class UpdatePasswordRequest {

    @StrongPassword
    private String password;

    @JsonPOJOBuilder(withPrefix = "")
    public static class UpdatePasswordRequestBuilderImpl extends UpdatePasswordRequest.UpdatePasswordRequestBuilder<UpdatePasswordRequest, UpdatePasswordRequest.UpdatePasswordRequestBuilderImpl> {
    }
}
