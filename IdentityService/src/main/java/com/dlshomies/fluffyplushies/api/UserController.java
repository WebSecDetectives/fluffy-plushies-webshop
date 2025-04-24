package com.dlshomies.fluffyplushies.api;

import com.dlshomies.fluffyplushies.dto.UserRequest;
import com.dlshomies.fluffyplushies.dto.UserResponse;
import com.dlshomies.fluffyplushies.entity.User;
import com.dlshomies.fluffyplushies.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.dlshomies.fluffyplushies.config.ModelMapperConfig.LIST_TYPE_USER_DTO;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/users")
public class UserController {

    private UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    List<UserResponse> getUsers() {
        return modelMapper.map(userService.getUsers(), LIST_TYPE_USER_DTO);
    }

    @PostMapping("")
    public UserResponse registerUser(@Valid @RequestBody UserRequest userRequest) {
        User user = userService.registerUser(modelMapper.map(userRequest, User.class), userRequest.getPassword());
        return modelMapper.map(user, UserResponse.class);
    }

    @PostMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserResponse registerAdmin(@Valid @RequestBody UserRequest userRequest) {
        User user = userService.registerAdminUser(modelMapper.map(userRequest, User.class), userRequest.getPassword());
        return modelMapper.map(user, UserResponse.class);
    }

    @PreAuthorize("hasAuthority('ADMIN') or @userSecurity.isSelf(#id)")
    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable UUID id) {
        return modelMapper.map(userService.getUser(id), UserResponse.class);
    }
}
