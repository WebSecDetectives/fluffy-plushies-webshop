package com.sirmeows.fluffyinventoryservice.api;

import com.sirmeows.fluffyinventoryservice.dto.ItemRequestDto;
import com.sirmeows.fluffyinventoryservice.dto.ItemResponseDto;
import com.sirmeows.fluffyinventoryservice.dto.ItemUpdateDto;
import com.sirmeows.fluffyinventoryservice.entity.Item;
import com.sirmeows.fluffyinventoryservice.security.AuthUser;
import com.sirmeows.fluffyinventoryservice.service.ItemService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
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
    public List<ItemResponseDto> getItems(@AuthenticationPrincipal AuthUser user) {
        return modelMapper.map(itemService.getVisibleItems(user), LIST_TYPE_ITEM_RESPONSE_DTO);
    }

    @GetMapping("/{id}")
    public ItemResponseDto getItem(@PathVariable UUID id, @AuthenticationPrincipal AuthUser user) {
        return modelMapper.map(itemService.getVisibleItem(id, user), ItemResponseDto.class);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasAuthority('MERCHANT')")
    public List<ItemResponseDto> getMyItems(@AuthenticationPrincipal AuthUser user) {
        return modelMapper.map(itemService.getItemsByMerchant(user.id()), LIST_TYPE_ITEM_RESPONSE_DTO);
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('MERCHANT')")
    public ItemResponseDto createItem(@AuthenticationPrincipal AuthUser user,
                                      @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Merchant {} creating new item {}", user.id(), itemRequestDto);
        var item = itemService.createItem(modelMapper.map(itemRequestDto, Item.class), user.id());
        return modelMapper.map(item, ItemResponseDto.class);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MERCHANT','ADMIN')")
    public ItemResponseDto updateItem(@PathVariable UUID id,
                                      @AuthenticationPrincipal AuthUser user,
                                      @Valid @RequestBody ItemUpdateDto itemUpdateDto) {
        log.info("{} updating item {}", user.id(), id);
        var item = itemService.updateItem(id, itemUpdateDto, user);
        return modelMapper.map(item, ItemResponseDto.class);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MERCHANT','ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable UUID id, @AuthenticationPrincipal AuthUser user) {
        log.info("{} deleting item {}", user.id(), id);
        itemService.deleteItem(id, user);
    }
}
