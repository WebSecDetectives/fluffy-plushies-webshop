package com.sirmeows.fluffyinventoryservice.service;

import com.sirmeows.fluffyinventoryservice.dto.ItemDetailsUpdateDto;
import com.sirmeows.fluffyinventoryservice.dto.ItemUpdateDto;
import com.sirmeows.fluffyinventoryservice.entity.Item;
import com.sirmeows.fluffyinventoryservice.entity.ItemDetails;
import com.sirmeows.fluffyinventoryservice.entity.Visibility;
import com.sirmeows.fluffyinventoryservice.exception.ItemAccessDeniedException;
import com.sirmeows.fluffyinventoryservice.exception.ItemNotFoundException;
import com.sirmeows.fluffyinventoryservice.repository.ItemRepository;
import com.sirmeows.fluffyinventoryservice.security.AuthUser;
import com.sirmeows.fluffyinventoryservice.security.Role;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class ItemService {
    private ItemRepository itemRepository;

    /**
     * Returns the items the caller is allowed to see:
     * ADMIN -> all; MERCHANT -> all PUBLIC plus their own (incl. PRIVATE); USER/anonymous -> PUBLIC only.
     */
    public List<Item> getVisibleItems(AuthUser caller) {
        if (isAdmin(caller)) {
            return itemRepository.findAll();
        }
        if (isMerchant(caller)) {
            return itemRepository.findByVisibilityOrMerchantId(Visibility.PUBLIC, caller.id());
        }
        return itemRepository.findByVisibility(Visibility.PUBLIC);
    }

    /**
     * Returns the item if the caller may see it. A PRIVATE item the caller cannot see is
     * reported as 404 (not 403) so its existence isn't leaked.
     */
    public Item getVisibleItem(UUID id, AuthUser caller) {
        var item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
        if (!canView(item, caller)) {
            throw new ItemNotFoundException(id);
        }
        return item;
    }

    /**
     * A merchant's own items (PUBLIC and PRIVATE). The merchantId is the authenticated
     * caller's id, supplied by the controller from the JWT.
     */
    public List<Item> getItemsByMerchant(UUID merchantId) {
        return itemRepository.findByMerchantId(merchantId);
    }

    public Item createItem(Item item, UUID merchantId) {
        item.setMerchantId(merchantId);
        log.info("Creating new item {} for merchant {}", item, merchantId);
        return itemRepository.save(item);
    }

    /**
     * Applies a partial update to an item the caller may modify (ADMIN: any;
     * MERCHANT: own only). Only non-null fields are applied; nested details are merged
     * onto the existing managed instance.
     */
    public Item updateItem(UUID id, ItemUpdateDto patch, AuthUser caller) {
        var item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));

        assertCanModify(item, caller);

        log.info("Updating item {} for {} {}", item, caller.role().name(), caller.id());

        if (patch.getName() != null) item.setName(patch.getName());
        if (patch.getPrice() != null) item.setPrice(patch.getPrice());
        if (patch.getStock() != null) item.setStock(patch.getStock());
        if (patch.getVisibility() != null) item.setVisibility(patch.getVisibility());
        if (patch.getDetails() != null) mergeDetails(item.getDetails(), patch.getDetails());

        return itemRepository.save(item);
    }

    public void deleteItem(UUID id, AuthUser caller) {
        var item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
        assertCanModify(item, caller);
        itemRepository.delete(item);
    }

    private boolean canView(Item item, AuthUser caller) {
        if (item.getVisibility() == Visibility.PUBLIC) {
            return true;
        }
        return isAdmin(caller) || isOwner(item, caller);
    }

    /**
     * Authorizes a modifying action. ADMIN may modify any item; a MERCHANT only their own.
     * A not-owned PRIVATE item is reported as 404 so its existence isn't leaked; a not-owned
     * but visible (PUBLIC) item is 403.
     */
    private void assertCanModify(Item item, AuthUser caller) {
        if (isAdmin(caller) || isOwner(item, caller)) {
            return;
        }
        if (item.getVisibility() == Visibility.PRIVATE) {
            throw new ItemNotFoundException(item.getId());
        }
        throw new ItemAccessDeniedException(item.getId());
    }

    /**
     * Merges non-null patch fields onto the item's EXISTING details instance. Mutating the
     * managed row in place (never replacing it or accepting a client id) keeps the same row,
     * avoids orphaning the old one, and prevents binding to a foreign details row.
     */
    private void mergeDetails(ItemDetails details, ItemDetailsUpdateDto patch) {
        if (details == null) {
            return;
        }
        if (patch.getDescription() != null) details.setDescription(patch.getDescription());
        if (patch.getAgeGroup() != null) details.setAgeGroup(patch.getAgeGroup());
        if (patch.getItemType() != null) details.setItemType(patch.getItemType());
        if (patch.getMaterial() != null) details.setMaterial(patch.getMaterial());
        if (patch.getImgUrl() != null) details.setImgUrl(patch.getImgUrl());
    }

    private boolean isOwner(Item item, AuthUser caller) {
        return caller != null && item.getMerchantId().equals(caller.id());
    }

    private boolean isAdmin(AuthUser caller) {
        return caller != null && caller.role() == Role.ADMIN;
    }

    private boolean isMerchant(AuthUser caller) {
        return caller != null && caller.role() == Role.MERCHANT;
    }
}
