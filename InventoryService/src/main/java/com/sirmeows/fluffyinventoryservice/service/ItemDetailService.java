package com.sirmeows.fluffyinventoryservice.service;

import com.sirmeows.fluffyinventoryservice.entity.ItemDetails;
import com.sirmeows.fluffyinventoryservice.repository.ItemDetailsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ItemDetailService {
    private ItemDetailsRepository itemDetailsRepository;

    public ItemDetails createItemDetails(ItemDetails details) {
        return  itemDetailsRepository.save(details);
    }
}
