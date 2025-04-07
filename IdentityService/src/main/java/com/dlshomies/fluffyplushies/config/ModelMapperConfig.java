package com.dlshomies.fluffyplushies.config;

import com.dlshomies.fluffyplushies.dto.UserResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Type;
import java.util.ArrayList;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public static final Type LIST_TYPE_USER_DTO = new TypeToken<ArrayList<UserResponse>>() {}.getType();
}
