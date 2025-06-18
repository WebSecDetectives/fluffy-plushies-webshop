package com.sirmeows.fluffyinventoryservice.config;

import com.sirmeows.fluffyinventoryservice.dto.ItemRequestDto;
import com.sirmeows.fluffyinventoryservice.dto.ItemResponseDto;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Type;
import java.util.ArrayList;

@Configuration
public class ModelMapperConfig {
    public static final Type LIST_TYPE_ITEM_REQUEST_DTO = new TypeToken<ArrayList<ItemRequestDto>>(){}.getType();
    public static final Type LIST_TYPE_ITEM_RESPONSE_DTO = new TypeToken<ArrayList<ItemResponseDto>>(){}.getType();

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration()
                .setPropertyCondition(Conditions.isNotNull());

        return mapper;
    }
}
