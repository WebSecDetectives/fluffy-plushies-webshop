package com.dlshomies.fluffyplushies.dto;

import com.dlshomies.fluffyplushies.util.StrongPassword;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonDeserialize(builder = UserRequest.UserRequestBuilderImpl.class)
public class UserRequest {

    @NotNull
    @NotBlank
    @Size(min = 3, max = 30)
    private String username;

    @NotNull
    @NotBlank
    @Email
    private String email;

    @NotNull
    @NotBlank
    private String phone;

    @StrongPassword
    private String password;

    @NotNull
    private AddressRequest address;

    @JsonPOJOBuilder(withPrefix = "")
    public static class UserRequestBuilderImpl extends UserRequest.UserRequestBuilder<UserRequest, UserRequestBuilderImpl> {
    }
}
