package com.sirmeows.fluffyinventoryservice.api;

import com.sirmeows.fluffyinventoryservice.dto.ItemRequestDto;
import com.sirmeows.fluffyinventoryservice.dto.ItemResponseDto;
import com.sirmeows.fluffyinventoryservice.entity.Item;
import com.sirmeows.fluffyinventoryservice.security.AuthUser;
import com.sirmeows.fluffyinventoryservice.service.ItemService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.sirmeows.fluffyinventoryservice.config.ModelMapperConfig.LIST_TYPE_ITEM_RESPONSE_DTO;

@Slf4j
@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/items")
public class ItemController {

    private ItemService itemService;
    private final ModelMapper modelMapper;

    @GetMapping("")
    public List<ItemResponseDto> getItems() {
        return modelMapper.map(itemService.getItems(), LIST_TYPE_ITEM_RESPONSE_DTO);
    }

    @GetMapping("/{id}")
    public ItemResponseDto getItem(@PathVariable UUID id) {
        return modelMapper.map(itemService.getItem(id), ItemResponseDto.class);
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('MERCHANT')")
    public ItemResponseDto createItem(@AuthenticationPrincipal AuthUser user,
                                      @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Merchant {} creating new item {}", user.id(), itemRequestDto);
        var item = itemService.createItem(modelMapper.map(itemRequestDto, Item.class), user.id());
        return modelMapper.map(item, ItemResponseDto.class);
    }
}
