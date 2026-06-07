package com.sirmeows.fluffyinventoryservice.service;

import com.sirmeows.fluffyinventoryservice.entity.ItemImage;
import com.sirmeows.fluffyinventoryservice.exception.ItemNotFoundException;
import com.sirmeows.fluffyinventoryservice.repository.ItemImageRepository;
import com.sirmeows.fluffyinventoryservice.security.AuthUser;
import com.sirmeows.fluffyinventoryservice.util.ImageSanitizationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class ItemImageService {

    private static final String STORED_CONTENT_TYPE = "image/png";

    private final ItemService itemService;
    private final ItemImageRepository itemImageRepository;

    /**
     * Stores (or replaces) the image for an item the caller may modify
     * (ADMIN: any item; MERCHANT: own items). The upload is sanitized before persisting.
     *
     * @param itemId the item the image belongs to
     * @param caller the authenticated user from the JWT
     * @param file   the multipart upload
     * @throws com.sirmeows.fluffyinventoryservice.exception.ItemNotFoundException if the item
     *         doesn't exist or its existence shouldn't be revealed to the caller
     * @throws com.sirmeows.fluffyinventoryservice.exception.ItemAccessDeniedException if the
     *         item is visible to the caller but not theirs to modify
     * @throws com.sirmeows.fluffyinventoryservice.exception.InvalidImageException if the upload
     *         fails sanitization
     */
    @Transactional
    public void storeImage(UUID itemId, AuthUser caller, MultipartFile file) {
        itemService.getModifiableItem(itemId, caller);
        var pngData = ImageSanitizationUtil.sanitizeToPng(file);
        upsertImage(itemId, pngData);
        log.info("Stored image ({} bytes) for item {}", pngData.length, itemId);
    }

    /**
     * Returns the image if the caller may see the item. A missing image and an invisible
     * item are both reported as 404 so a PRIVATE item's existence isn't leaked.
     *
     * @param itemId the item whose image is requested
     * @param caller the authenticated user from the JWT, or null for anonymous callers
     * @return the stored image with its content type
     * @throws com.sirmeows.fluffyinventoryservice.exception.ItemNotFoundException if the item
     *         or its image doesn't exist, or the item isn't visible to the caller
     */
    public ItemImage getImageForVisibleItem(UUID itemId, AuthUser caller) {
        itemService.getVisibleItem(itemId, caller);
        return itemImageRepository.findByItemId(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    /**
     * Stores the image for a seeded item. Runs the same sanitization pipeline as user
     * uploads but skips the ownership check — only called from startup seeding, where
     * there is no authenticated caller.
     *
     * @param itemId the seeded item the image belongs to
     * @param imageBytes the raw image bytes (e.g. a bundled classpath JPEG)
     * @throws com.sirmeows.fluffyinventoryservice.exception.InvalidImageException if the
     *         bytes fail sanitization
     */
    @Transactional
    public void storeSeedImage(UUID itemId, byte[] imageBytes) {
        var pngData = ImageSanitizationUtil.sanitizeToPng(imageBytes);
        upsertImage(itemId, pngData);
        log.info("Stored seed image ({} bytes) for item {}", pngData.length, itemId);
    }

    /** Updates the item's existing image row or creates one (one image per item). */
    private void upsertImage(UUID itemId, byte[] pngData) {
        var image = itemImageRepository.findByItemId(itemId)
                .orElseGet(() -> ItemImage.builder().itemId(itemId).build());
        image.setContentType(STORED_CONTENT_TYPE);
        image.setData(pngData);
        itemImageRepository.save(image);
    }
}
