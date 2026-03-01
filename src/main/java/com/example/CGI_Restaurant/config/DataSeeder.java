package com.example.CGI_Restaurant.config;

import com.example.CGI_Restaurant.domain.entities.*;
import com.example.CGI_Restaurant.domain.createRequests.CreateMenuItemRequest;
import com.example.CGI_Restaurant.repositories.*;
import com.example.CGI_Restaurant.services.MenuItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fills the database with initial data when the application starts, if the database is empty.
 * Runs only when the default profile is active (or when "seed" is not disabled).
 * Creates: features, one restaurant with seating plan, zones, tables, menu items, a demo customer and sample bookings.
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements ApplicationRunner {

    private final FeatureRepository featureRepository;
    private final RestaurantRepository restaurantRepository;
    private final SeatingPlanRepository seatingPlanRepository;
    private final ZoneRepository zoneRepository;
    private final TableEntityRepository tableEntityRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingTableRepository bookingTableRepository;
    private final BookingPreferenceRepository bookingPreferenceRepository;
    private final PasswordEncoder passwordEncoder;
    private final MenuItemService menuItemService;

    @Value("${cgi.seed.enabled:true}")
    private boolean seedEnabled;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!seedEnabled) {
            log.debug("Database seeding is disabled (cgi.seed.enabled=false).");
            return;
        }
        if (restaurantRepository.count() > 0) {
            log.info("Data already present, skipping seed.");
            return;
        }
        log.info("Seeding database with initial data...");
        LocalDateTime now = LocalDateTime.now();

        // 1) Features (used by zones and booking preferences)
        List<Feature> features = List.of(
                feature(FeatureCodeEnum.WINDOW, "Akna all", now),
                feature(FeatureCodeEnum.QUIET, "Vaikne nurk / Privaatsus", now),
                feature(FeatureCodeEnum.PRIVACY, "Privaatne ruum", now),
                feature(FeatureCodeEnum.ACCESSIBLE, "Ligipääsetav", now),
                feature(FeatureCodeEnum.KIDS_NEARBY, "Laste mängunurga lähedal", now),
                feature(FeatureCodeEnum.NEAR_BAR, "Baari lähedal", now),
                feature(FeatureCodeEnum.CENTER, "Saali keskel", now)
        );
        features = featureRepository.saveAll(features);
        log.info("Created {} features.", features.size());

        // 2) Restaurant
        Restaurant restaurant = Restaurant.builder()
                .name("CGI Restoran")
                .timezone("Europe/Tallinn")
                .email("cgisuvepraktika@gmail.com")
                .phone("+372 612 3456")
                .address("A. H. Tammsaare tee 56, 11316 Tallinn")
                .createdAt(now)
                .updatedAt(now)
                .build();
        restaurant = restaurantRepository.save(restaurant);
        log.info("Created restaurant: {}", restaurant.getName());

        // 3) Seating plan
        SeatingPlan plan = SeatingPlan.builder()
                .name("Põhiplaan")
                .type(SeatingPlanTypeEnum.FLOOR_1)
                .width(800)
                .height(600)
                .active(true)
                .version(1)
                .restaurant(restaurant)
                .createdAt(now)
                .updatedAt(now)
                .build();
        plan = seatingPlanRepository.save(plan);
        log.info("Created seating plan: {}", plan.getName());

        // 4) Zones (Sisesaal, Terrass, Privaatruum)
        Feature window = features.stream().filter(f -> f.getCode() == FeatureCodeEnum.WINDOW).findFirst().orElseThrow();
        Feature quiet = features.stream().filter(f -> f.getCode() == FeatureCodeEnum.QUIET).findFirst().orElseThrow();
        Feature privacy = features.stream().filter(f -> f.getCode() == FeatureCodeEnum.PRIVACY).findFirst().orElseThrow();
        Feature accessible = features.stream().filter(f -> f.getCode() == FeatureCodeEnum.ACCESSIBLE).findFirst().orElseThrow();
        Feature kids = features.stream().filter(f -> f.getCode() == FeatureCodeEnum.KIDS_NEARBY).findFirst().orElseThrow();
        Feature nearBar = features.stream().filter(f -> f.getCode() == FeatureCodeEnum.NEAR_BAR).findFirst().orElseThrow();

        Zone zoneIndoor = Zone.builder()
                .name("Sisesaal")
                .type(ZoneTypeEnum.INDOOR)
                .color("#4a5568")
                .seatingPlan(plan)
                .features(Set.of(window, quiet, nearBar, accessible))
                .createdAt(now)
                .updatedAt(now)
                .build();
        zoneIndoor = zoneRepository.save(zoneIndoor);

        Zone zoneTerrace = Zone.builder()
                .name("Terrass")
                .type(ZoneTypeEnum.TERRACE)
                .color("#2d5016")
                .seatingPlan(plan)
                .features(Set.of(window, accessible))
                .createdAt(now)
                .updatedAt(now)
                .build();
        zoneTerrace = zoneRepository.save(zoneTerrace);

        Zone zonePrivate = Zone.builder()
                .name("Privaatruum")
                .type(ZoneTypeEnum.PRIVATE)
                .color("#553c9a")
                .seatingPlan(plan)
                .features(Set.of(privacy, quiet))
                .createdAt(now)
                .updatedAt(now)
                .build();
        zonePrivate = zoneRepository.save(zonePrivate);
        log.info("Created 3 zones: Sisesaal, Terrass, Privaatruum.");

        // 5) Tables (layout: a few 2-, 4-, 6- and 8-seaters in each zone)
        List<TableEntity> tables = new java.util.ArrayList<>();
        tables.add(tableEntity("T1", 2, 1, 50, 50, 80, 70, 0, TableShapeEnum.RECT, zoneIndoor, plan, now));
        tables.add(tableEntity("T2", 2, 1, 150, 50, 80, 70, 0, TableShapeEnum.RECT, zoneIndoor, plan, now));
        tables.add(tableEntity("T3", 4, 2, 50, 150, 100, 90, 0, TableShapeEnum.CIRCLE, zoneIndoor, plan, now));
        tables.add(tableEntity("T4", 4, 2, 170, 150, 100, 90, 0, TableShapeEnum.OVAL, zoneIndoor, plan, now));
        tables.add(tableEntity("T5", 6, 4, 290, 50, 120, 90, 0, TableShapeEnum.RECT, zoneIndoor, plan, now));
        tables.add(tableEntity("T6", 8, 6, 430, 80, 140, 100, 0, TableShapeEnum.RECT, zoneIndoor, plan, now));
        tables.add(tableEntity("T7", 2, 1, 50, 280, 80, 70, 0, TableShapeEnum.CIRCLE, zoneTerrace, plan, now));
        tables.add(tableEntity("T8", 4, 2, 150, 280, 100, 90, 0, TableShapeEnum.RECT, zoneTerrace, plan, now));
        tables.add(tableEntity("T9", 4, 2, 270, 280, 100, 90, 0, TableShapeEnum.OVAL, zoneTerrace, plan, now));
        tables.add(tableEntity("T10", 6, 4, 50, 400, 120, 90, 0, TableShapeEnum.RECT, zonePrivate, plan, now));
        tables.add(tableEntity("T11", 8, 6, 200, 380, 140, 100, 0, TableShapeEnum.RECT, zonePrivate, plan, now));
        tables = tableEntityRepository.saveAll(tables);
        log.info("Created {} tables.", tables.size());

        // Optional: mark T5 and T6 as adjacent (can be combined for large party)
        TableEntity t5 = tables.stream().filter(t -> "T5".equals(t.getLabel())).findFirst().orElseThrow();
        TableEntity t6 = tables.stream().filter(t -> "T6".equals(t.getLabel())).findFirst().orElseThrow();
        t5.setAdjacentTables(new HashSet<>(Set.of(t6)));
        t6.setAdjacentTables(new HashSet<>(Set.of(t5)));
        tableEntityRepository.saveAll(List.of(t5, t6));

        // 6) Menu items: TheMealDB (selected set) + local drinks
        List<TheMealDBSeedEntry> themealdbSelections = List.of(
                new TheMealDBSeedEntry("52772", new BigDecimal("12.50")),   // Teriyaki Chicken Casserole
                new TheMealDBSeedEntry("52812", new BigDecimal("14.00")),   // Beef Wellington
                new TheMealDBSeedEntry("52928", new BigDecimal("11.00")),   // Mediterranean Pasta
                new TheMealDBSeedEntry("52768", new BigDecimal("9.50")),    // Honey Teriyaki Salmon
                new TheMealDBSeedEntry("53016", new BigDecimal("8.00")),    // Chocolate Gateau
                new TheMealDBSeedEntry("52923", new BigDecimal("7.50")),    // Sticky Toffee Pudding
                new TheMealDBSeedEntry("52767", new BigDecimal("6.00")),    // Grilled Mac and Cheese
                new TheMealDBSeedEntry("52802", new BigDecimal("5.50")),    // Fish pie
                new TheMealDBSeedEntry("52773", new BigDecimal("10.50")),   // Spicy Arrabiata Penne
                new TheMealDBSeedEntry("52844", new BigDecimal("13.00")),   // Lasagne
                new TheMealDBSeedEntry("52977", new BigDecimal("11.50")),   // Coriander and Tuna Fishcakes
                new TheMealDBSeedEntry("52854", new BigDecimal("9.00")),    // French Lentils With Mushrooms
                new TheMealDBSeedEntry("52978", new BigDecimal("8.50"))     // Fish Stew
        );
        int menuCount = 0;
        for (TheMealDBSeedEntry entry : themealdbSelections) {
            try {
                menuItemService.addFromTheMealDB(restaurant.getId(), entry.mealId(), entry.priceEur());
                menuCount++;
            } catch (Exception e) {
                log.warn("Could not add TheMealDB meal {} to menu: {}", entry.mealId(), e.getMessage());
            }
        }
        // Drinks (no external API)
        List<CreateMenuItemRequest> drinks = List.of(
                createDrink("Kohvi", "Espresso, Americano, Latte, Cappuccino", new BigDecimal("3.50"), "Joogid"),
                createDrink("Mahl", "Apelsin, õun, grapefruit", new BigDecimal("3.00"), "Joogid"),
                createDrink("Vesi", "Vesi (klaas / pudel)", new BigDecimal("2.00"), "Joogid"),
                createDrink("Tee", "Must tee, roheline tee, piparmündi tee", new BigDecimal("2.50"), "Joogid"),
                createDrink("Külm kohv", "Iced latte, freddo", new BigDecimal("4.00"), "Joogid")
        );
        for (CreateMenuItemRequest drink : drinks) {
            drink.setRestaurantId(restaurant.getId());
            menuItemService.create(drink);
            menuCount++;
        }
        log.info("Created {} menu items (TheMealDB + drinks).", menuCount);

        // 7) Demo customer (if not exists)
        String customerEmail = "klient@test.com";
        User customer = userRepository.findByEmail(customerEmail).orElseGet(() -> {
            User u = User.builder()
                    .name("Demo Klient")
                    .email(customerEmail)
                    .passwordHash(passwordEncoder.encode("password"))
                    .role(UserRoleEnum.CUSTOMER)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            return userRepository.save(u);
        });
        log.info("Demo customer: {} (password: password)", customerEmail);

        // 8) Sample bookings (so that some tables appear occupied on the plan)
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
        int durationHours = 2;

        Booking b1 = booking("Maria Mets", "maria@example.ee", today.plusHours(12), today.plusHours(12 + durationHours), 2, customer, now);
        b1 = bookingRepository.save(b1);
        bookingTableRepository.save(BookingTable.builder().booking(b1).tableEntity(tables.get(0)).createdAt(now).updatedAt(now).build());

        Booking b2 = booking("Jüri Kask", "juri@example.ee", today.plusHours(14), today.plusHours(14 + durationHours), 4, customer, now);
        b2 = bookingRepository.save(b2);
        bookingTableRepository.save(BookingTable.builder().booking(b2).tableEntity(tables.get(2)).createdAt(now).updatedAt(now).build());

        Booking b3 = booking("Liis Tamm", "liis@example.ee", today.plusHours(18), today.plusHours(18 + durationHours), 6, customer, now);
        b3 = bookingRepository.save(b3);
        bookingTableRepository.save(BookingTable.builder().booking(b3).tableEntity(t5).createdAt(now).updatedAt(now).build());

        // One booking with preference (window seat)
        Booking b4 = booking("Oliver Saar", "oliver@example.ee", today.plusHours(13), today.plusHours(13 + durationHours), 2, customer, now);
        b4 = bookingRepository.save(b4);
        bookingTableRepository.save(BookingTable.builder().booking(b4).tableEntity(tables.get(6)).createdAt(now).updatedAt(now).build());
        bookingPreferenceRepository.save(BookingPreference.builder()
                .booking(b4).feature(window).priority(PreferencePriorityEnum.HIGH).createdAt(now).updatedAt(now).build());

        log.info("Created 4 sample bookings.");
        log.info("Database seed completed.");
    }

    private static Feature feature(FeatureCodeEnum code, String name, LocalDateTime now) {
        Feature f = new Feature();
        f.setCode(code);
        f.setName(name);
        f.setCreatedAt(now);
        f.setUpdatedAt(now);
        return f;
    }

    private static TableEntity tableEntity(String label, int capacity, int minParty, double x, double y, double w, double h, int rotation,
                                          TableShapeEnum shape, Zone zone, SeatingPlan plan, LocalDateTime now) {
        TableEntity t = TableEntity.builder()
                .label(label)
                .capacity(capacity)
                .minPartySize(minParty)
                .shape(shape)
                .x(x).y(y).width(w).height(h)
                .rotationDegree(rotation)
                .active(true)
                .zone(zone)
                .seatingPlan(plan)
                .createdAt(now)
                .updatedAt(now)
                .build();
        return t;
    }

    private record TheMealDBSeedEntry(String mealId, BigDecimal priceEur) {}

    private static CreateMenuItemRequest createDrink(String name, String description, BigDecimal price, String category) {
        CreateMenuItemRequest req = new CreateMenuItemRequest();
        req.setName(name);
        req.setDescription(description);
        req.setPriceEur(price);
        req.setCategory(category);
        return req;
    }

    private static Booking booking(String guestName, String guestEmail, LocalDateTime start, LocalDateTime end, int partySize, User user, LocalDateTime now) {
        return Booking.builder()
                .guestName(guestName)
                .guestEmail(guestEmail)
                .startAt(start)
                .endAt(end)
                .partySize(partySize)
                .status(BookingStatusEnum.CONFIRMED)
                .user(user)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
