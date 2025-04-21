package com.dlshomies.fluffyplushies.api;

import com.dlshomies.fluffyplushies.dto.AuthRequest;
import com.dlshomies.fluffyplushies.dto.AuthResponse;
import com.dlshomies.fluffyplushies.service.AuthService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/auth")
public class AuthController {

    private ModelMapper modelMapper;

    private AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        var encodedToken = authService.login(authRequest.getUsername(), authRequest.getPassword());
        return modelMapper.map(encodedToken, AuthResponse.class);
    }
}
