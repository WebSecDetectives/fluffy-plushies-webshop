package com.dlshomies.fluffyplushies.config;

import com.dlshomies.fluffyplushies.dto.messaging.UserInformationResponse;
import com.dlshomies.fluffyplushies.dto.rest.UserResponse;
import com.dlshomies.fluffyplushies.entity.User;
import com.dlshomies.fluffyplushies.entity.UserHistory;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.TypeToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Type;
import java.util.ArrayList;

@Configuration
public class ModelMapperConfig {
    public static final Type LIST_TYPE_USER_DTO = new TypeToken<ArrayList<UserResponse>>() {}.getType();

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration()
                .setPropertyCondition(Conditions.isNotNull());

        TypeMap<User, UserHistory> userToHistory = mapper.createTypeMap(User.class, UserHistory.class);
        userToHistory.addMappings(m -> {
            m.map(User::getId, UserHistory::setUserId);
            m.skip(UserHistory::setId);
            m.skip(UserHistory::setCreatedAt);
            m.map(src -> src.getAddress().getId(), UserHistory::setAddressId);
        });

        TypeMap<User, UserInformationResponse> userToInfoResponse = mapper.createTypeMap(User.class, UserInformationResponse.class);
        userToInfoResponse.addMappings(m -> {
            m.map(User::getUsername, UserInformationResponse::setCustomerName);
            m.using(ctx -> Integer.valueOf((String)ctx.getSource())).map(User::getPhone, UserInformationResponse::setPhone);
            m.using(ctx -> Integer.valueOf((String)ctx.getSource())).map(src -> src.getAddress().getPostalCode(), UserInformationResponse::setPostalCode);
        });

        return mapper;
    }
}
