package com.sirmeows.fluffyinventoryservice.config;

import com.sirmeows.fluffyinventoryservice.entity.Item;
import com.sirmeows.fluffyinventoryservice.entity.ItemDetails;
import com.sirmeows.fluffyinventoryservice.service.ItemDetailService;
import com.sirmeows.fluffyinventoryservice.service.ItemService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private static final int NUMBER_OF_ITEMS = 10 ;
    private final ItemService itemService;
    private final ItemDetailService itemDetailService;
    private final Faker faker;
    private static final RandomGenerator random = RandomGenerator.of("L64X256MixRandom");


    @PostConstruct
    public void init() {
        log.info("Starting data initialization...");

        Stream.generate(this::buildRandomItem)
                .limit(NUMBER_OF_ITEMS)
                .forEach(itemService::createItem);

        log.info("Created {} items. Finished data initialization...", NUMBER_OF_ITEMS);
    }

    private Item buildRandomItem() {
        return Item.builder()
                .name(randomName())
                .price(randomPrice())
                .stock(randomStock())
                .details(createRandomDetails())
                .build();
    }

    private ItemDetails createRandomDetails() {
        var details = ItemDetails.builder()
                .description("XX")
                .ageGroup(AgeGroup.random().label())
                .itemType("YY")
                .material("ZZ")
                .imgLink(randomUrl())
                .build();
        return itemDetailService.createItemDetails(details);
    }

    private String randomName() {
        return faker.funnyName().name().toLowerCase(Locale.ROOT);
    }

    private URI randomUrl() {
        return URI.create(faker.internet().url());
    }

    private BigDecimal randomPrice() {
        return BigDecimal.valueOf(random.nextDouble(50.0, 5000.0));
    }

    private int randomStock() {
        return random.nextInt(1, 100);
    }

    private record AgeGroup(String label) {
        private static final List<AgeGroup> POOL = List.of(
                new AgeGroup("1+"), new AgeGroup("2+"), new AgeGroup("3+"),
                new AgeGroup("5+"), new AgeGroup("7+"), new AgeGroup("11+"),
                new AgeGroup("15+")
        );
        private static final RandomGenerator RG = RandomGenerator.of("L64X256MixRandom");

        public static AgeGroup random() {
            return POOL.get(RG.nextInt(POOL.size()));
        }
    }
}
