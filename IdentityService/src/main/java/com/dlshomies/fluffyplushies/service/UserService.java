package com.dlshomies.fluffyplushies.service;

import com.dlshomies.fluffyplushies.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@AllArgsConstructor
@Service
public class UserService {

    public ArrayList<User> getUsers() {
        return new ArrayList<User>();
    }
}
