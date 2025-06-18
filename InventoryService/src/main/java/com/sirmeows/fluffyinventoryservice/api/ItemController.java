package com.sirmeows.fluffyinventoryservice.api;

import com.sirmeows.fluffyinventoryservice.dto.ItemRequestDto;
import com.sirmeows.fluffyinventoryservice.dto.ItemResponseDto;
import com.sirmeows.fluffyinventoryservice.entity.Item;
import com.sirmeows.fluffyinventoryservice.service.ItemService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("")
    public ItemResponseDto createItem(@Valid @RequestBody ItemRequestDto itemRequestDto) {
        var item = itemService.createItem(modelMapper.map(itemRequestDto, Item.class));
        return modelMapper.map(item, ItemResponseDto.class);
    }
}
