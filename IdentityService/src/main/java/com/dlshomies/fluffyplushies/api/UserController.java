package com.dlshomies.fluffyplushies.api;

import com.dlshomies.fluffyplushies.dto.CreateUserRequest;
import com.dlshomies.fluffyplushies.dto.UpdatePasswordRequest;
import com.dlshomies.fluffyplushies.dto.UpdateUserRequest;
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

    @GetMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    List<UserResponse> getUsers() {
        return modelMapper.map(userService.getUsers(), LIST_TYPE_USER_DTO);
    }

    @PostMapping("")
    public UserResponse registerUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        User user = userService.registerUser(modelMapper.map(createUserRequest, User.class), createUserRequest.getPassword());
        return modelMapper.map(user, UserResponse.class);
    }

    @PostMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserResponse registerAdmin(@Valid @RequestBody CreateUserRequest createUserRequest) {
        User user = userService.registerAdminUser(modelMapper.map(createUserRequest, User.class), createUserRequest.getPassword());
        return modelMapper.map(user, UserResponse.class);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or @userSecurity.isSelf(#id)")
    public UserResponse getUser(@PathVariable UUID id) {
        return modelMapper.map(userService.getUser(id), UserResponse.class);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or @userSecurity.isSelf(#id)")
    public UserResponse updateUser(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        var patch = new User();
        modelMapper.map(updateUserRequest, patch);
        return modelMapper.map(userService.updateUser(id, patch), UserResponse.class);
    }

    @PatchMapping("/{id}/password")
    @PreAuthorize("hasAuthority('ADMIN') or @userSecurity.isSelf(#id)")
    public UserResponse updatePassword(@PathVariable UUID id, @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {
        return modelMapper.map(userService.updatePassword(id, updatePasswordRequest.getPassword()), UserResponse.class);
    }
}
