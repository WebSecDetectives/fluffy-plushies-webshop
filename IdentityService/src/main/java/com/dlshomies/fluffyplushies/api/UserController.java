package com.dlshomies.fluffyplushies.api;

import com.dlshomies.fluffyplushies.dto.UserRequest;
import com.dlshomies.fluffyplushies.dto.UserResponse;
import com.dlshomies.fluffyplushies.entity.User;
import com.dlshomies.fluffyplushies.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.dlshomies.fluffyplushies.config.ModelMapperConfig.LIST_TYPE_USER_DTO;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/identity/users")
public class UserController {

    UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping("")
    List<UserResponse> getUsers() {
        return modelMapper.map(userService.getUsers(), LIST_TYPE_USER_DTO);
    }

    @PostMapping("")
    public UserResponse registerUser(@Valid @RequestBody UserRequest userRequest) {
        User user = userService.registerUser(modelMapper.map(userRequest, User.class));
        return modelMapper.map(user, UserResponse.class);
    }
}
