package com.sirmeows.fluffyinventoryservice.api;

import com.sirmeows.fluffyinventoryservice.dto.ItemResponseDto;
import com.sirmeows.fluffyinventoryservice.service.ItemService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.sirmeows.fluffyinventoryservice.config.ModelMapperConfig.LIST_TYPE_ITEM_RESPONSE_DTO;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/inventory")
public class ItemController {

    private ItemService itemService;
    private final ModelMapper modelMapper;

    @GetMapping("")
    public List<ItemResponseDto> getItems() {
        return modelMapper.map(itemService.getItems(), LIST_TYPE_ITEM_RESPONSE_DTO);
    }
}
