package com.sirmeows.fluffyinventoryservice.config;

import com.sirmeows.fluffyinventoryservice.entity.Item;
import com.sirmeows.fluffyinventoryservice.entity.ItemDetails;
import com.sirmeows.fluffyinventoryservice.entity.Review;
import com.sirmeows.fluffyinventoryservice.service.ItemDetailService;
import com.sirmeows.fluffyinventoryservice.service.ItemService;
import com.sirmeows.fluffyinventoryservice.service.ReviewService;
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
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private static final int NUMBER_OF_ITEMS = 10 ;
    private final ItemService itemService;
    private final ReviewService reviewService;
    private final Faker faker;
    private static final RandomGenerator RG = RandomGenerator.of("L64X256MixRandom");

    @PostConstruct
    public void init() {
        log.info("Starting data initialization...");

        var items = Stream.generate(this::buildRandomItem)
                .limit(NUMBER_OF_ITEMS)
                .map(itemService::createItem)
                .toList();

        items.forEach(item -> {
            int n = RG.nextInt(1, 4);
            IntStream.range(0, n).forEach(i -> {
                reviewService.createReview(item.getId(), buildRandomReview());
            });
        });

        log.info("Created {} items with reviews. Finished data initialization...", NUMBER_OF_ITEMS);
    }

    private Review buildRandomReview() {
        var reviewData = ReviewData.random();

        return Review.builder()
                .reviewText(reviewData.text)
                .rating(reviewData.rating)
                .build();
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
        return ItemDetails.builder()
                .description("XX")
                .ageGroup(AgeGroup.random().label())
                .itemType("YY")
                .material("ZZ")
                .imgLink(randomUrl())
                .build();
    }

    private String randomName() {
        return faker.funnyName().name().toLowerCase(Locale.ROOT);
    }

    private URI randomUrl() {
        return URI.create(faker.internet().url());
    }

    private BigDecimal randomPrice() {
        return BigDecimal.valueOf(RG.nextDouble(50.0, 5000.0));
    }

    private int randomStock() {
        return RG.nextInt(1, 100);
    }

    private record AgeGroup(String label) {
        private static final List<AgeGroup> POOL = List.of(
                new AgeGroup("1+"), new AgeGroup("2+"), new AgeGroup("3+"),
                new AgeGroup("5+"), new AgeGroup("7+"), new AgeGroup("11+"),
                new AgeGroup("15+")
        );

        public static AgeGroup random() {
            return POOL.get(RG.nextInt(POOL.size()));
        }
    }

    private record ReviewData(String text, int rating) {
        static final List<ReviewData> REVIEW_DATA_POOL = List.of(
                new ReviewData("Exactly what I wanted.", 5),
                new ReviewData("Great quality for the price.", 5),
                new ReviewData("Pretty good, a few small issues.", 4),
                new ReviewData("Decent, but expected more.", 3),
                new ReviewData("Okay, does the job.", 3),
                new ReviewData("Not impressed, would not buy again.", 2),
                new ReviewData("Arrived damaged and feels cheap.", 1),
                new ReviewData("Kids loved it, super soft!", 5),
                new ReviewData("Color wasn’t as shown.", 2),
                new ReviewData("Average—nothing special.", 3),
                new ReviewData("The fluffiest plushie I've ever hugged.", 5),
                new ReviewData("So fluffy it feels like a cloud.", 5),
                new ReviewData("My cat stole this plushie, must be good.", 4),
                new ReviewData("Stitching came loose after a week.", 2),
                new ReviewData("Super cuddly, perfect bedtime buddy.", 5),
                new ReviewData("A bit smaller than expected.", 3),
                new ReviewData("Fabric sheds lint everywhere.", 1),
                new ReviewData("Bright colors and ultra-soft fur.", 5),
                new ReviewData("Packaging was terrible, product survived.", 3),
                new ReviewData("Soft but weird chemical smell at first.", 2),
                new ReviewData("My toddler won’t let go of this bear.", 5),
                new ReviewData("Great gift, recipient was thrilled.", 5),
                new ReviewData("Meh—seen better plush toys.", 2),
                new ReviewData("Fluffy plushies collection complete thanks to this one.", 5),
                new ReviewData("Could use more stuffing in the arms.", 3),
                new ReviewData("Seam on the ear ripped quickly.", 1),
                new ReviewData("Fast shipping, lovely plush.", 4),
                new ReviewData("Cuter in person than in photos.", 4),
                new ReviewData("Perfect squishiness level.", 5),
                new ReviewData("Overpriced for what you get.", 2),
                new ReviewData("Ultra-fluffy belly, 10/10 snuggle score.", 5),
                new ReviewData("The tag scratched my kid’s neck.", 2),
                new ReviewData("Great texture, but eyes are misaligned.", 3),
                new ReviewData("Feels durable, quality stitching.", 4),
                new ReviewData("Fluffy plushie nirvana achieved.", 5),
                new ReviewData("Not fluffy at all, quite rough.", 1),
                new ReviewData("Survived several washes and still soft.", 4),
                new ReviewData("My dog thinks it’s his new toy—held up well.", 4),
                new ReviewData("Comfort item during hospital stay, worked wonders.", 5),
                new ReviewData("Too many loose threads out of the box.", 2)
        );

        static ReviewData random() {
            return REVIEW_DATA_POOL.get(RG.nextInt(REVIEW_DATA_POOL.size()));
        }
    }
}
