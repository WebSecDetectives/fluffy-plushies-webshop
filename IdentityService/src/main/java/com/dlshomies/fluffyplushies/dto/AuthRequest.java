package com.dlshomies.fluffyplushies.dto;

import com.dlshomies.fluffyplushies.util.StrongPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AuthRequest {

    @NotNull
    @NotBlank
    @Size(min = 3, max = 30)
    private String username;

    @StrongPassword
    private String password;
}
