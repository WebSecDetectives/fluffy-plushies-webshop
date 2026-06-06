package com.sirmeows.fluffyinventoryservice.api;

import com.sirmeows.fluffyinventoryservice.security.AuthUser;
import com.sirmeows.fluffyinventoryservice.service.ItemImageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/items/{itemId}/image")
public class ItemImageController {

    private ItemImageService itemImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('MERCHANT','ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void uploadItemImage(@PathVariable UUID itemId,
                                @AuthenticationPrincipal AuthUser user,
                                @RequestParam("file") MultipartFile file) {
        log.info("{} uploading image for item {}", user.id(), itemId);
        itemImageService.storeImage(itemId, user, file);
    }

    @GetMapping
    public ResponseEntity<byte[]> getItemImage(@PathVariable UUID itemId, @AuthenticationPrincipal AuthUser user) {
        var image = itemImageService.getImageForVisibleItem(itemId, user);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .body(image.getData());
    }
}