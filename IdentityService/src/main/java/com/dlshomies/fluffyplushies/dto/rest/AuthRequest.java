package com.dlshomies.fluffyplushies.dto.rest;

import com.dlshomies.fluffyplushies.util.StrongPassword;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonDeserialize(builder = AuthRequest.AuthRequestBuilderImpl.class)
public class AuthRequest {

    @NotNull
    @NotBlank
    @Size(min = 3, max = 30)
    private String username;

    @StrongPassword
    private String password;

    @JsonPOJOBuilder(withPrefix = "")
    public static class AuthRequestBuilderImpl extends AuthRequest.AuthRequestBuilder<AuthRequest, AuthRequestBuilderImpl> {}
}
