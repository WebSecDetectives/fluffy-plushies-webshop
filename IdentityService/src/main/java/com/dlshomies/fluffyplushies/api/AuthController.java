package com.dlshomies.fluffyplushies.api;

import com.dlshomies.fluffyplushies.dto.UserDto;
import com.dlshomies.fluffyplushies.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

import static com.dlshomies.fluffyplushies.config.ModelMapperConfig.LIST_TYPE_USER_DTO;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/identity/auth")
public class AuthController {

    private ModelMapper modelMapper;

    private UserService userService;

    @GetMapping("")
    ArrayList<UserDto> getUsers() {
        var users = userService.getUsers();
        return modelMapper.map(users, LIST_TYPE_USER_DTO);
    }
}
