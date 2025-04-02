package com.dlshomies.fluffyplushies.dto;

import com.dlshomies.fluffyplushies.util.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequest {
    @NotNull
    private String username;

    @NotNull
    @Email
    private String email;

    @NotNull
    private int phone;

    @StrongPassword
    private String password;

    @NotNull
    private AddressRequest address;
}
