package com.dlshomies.fluffyplushies.api;

import com.dlshomies.fluffyplushies.dto.UserRequest;
import com.dlshomies.fluffyplushies.dto.UserResponse;
import com.dlshomies.fluffyplushies.entity.User;
import com.dlshomies.fluffyplushies.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/identity/users")
public class UserController {

    UserService userService;
    private final ModelMapper modelMapper;

    @PostMapping("")
    public UserResponse registerUser(@Valid @RequestBody UserRequest userRequest) {
        User user = userService.registerUser(modelMapper.map(userRequest, User.class));
        return modelMapper.map(user, UserResponse.class);
    }
}
