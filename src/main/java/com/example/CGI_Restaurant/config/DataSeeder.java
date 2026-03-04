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
        Feature center = features.stream().filter(f -> f.getCode() == FeatureCodeEnum.CENTER).findFirst().orElseThrow();

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

        // 5) Tables – Sisesaal 11 lauda, Terrass 12 lauda, Privaatruum 1 pikk laud (12 kohta)
        // Igal laual on oma feature'd (laud ise on nt akna all); sobitamine broneerimisel läheb laua feature'te järgi.
        List<TableEntity> tables = new java.util.ArrayList<>();
        // Sisesaal: T1–T11 (erinevad suurused, kokku 11 lauda)
        tables.add(tableEntity("T1", 2, 1, 595.8, 432.7, 70, 60, 0, TableShapeEnum.RECT, zoneIndoor, plan, now));
        tables.add(tableEntity("T2", 2, 1, 98.4, 436.1, 70, 60, 0, TableShapeEnum.RECT, zoneIndoor, plan, now));
        tables.add(tableEntity("T3", 2, 1, 101.8, 283.8, 70, 60, 0, TableShapeEnum.CIRCLE, zoneIndoor, plan, now));
        tables.add(tableEntity("T4", 4, 2, 449.4, 437.1, 90, 80, 0, TableShapeEnum.RECT, zoneIndoor, plan, now));
        tables.add(tableEntity("T5", 4, 2, 586.0, 161.6, 90, 80, 0, TableShapeEnum.OVAL, zoneIndoor, plan, now));
        tables.add(tableEntity("T6", 4, 2, 406.5, 327.2, 90, 80, 0, TableShapeEnum.CIRCLE, zoneIndoor, plan, now));
        tables.add(tableEntity("T7", 6, 4, 238.3, 269.7, 110, 85, 0, TableShapeEnum.RECT, zoneIndoor, plan, now));
        tables.add(tableEntity("T8", 6, 4, 245.3, 157.2, 110, 85, 0, TableShapeEnum.RECT, zoneIndoor, plan, now));
        tables.add(tableEntity("T9", 4, 2, 582.4, 285.8, 90, 80, 0, TableShapeEnum.RECT, zoneIndoor, plan, now));
        tables.add(tableEntity("T10", 4, 2, 243.2, 438.8, 90, 80, 0, TableShapeEnum.CIRCLE, zoneIndoor, plan, now));
        tables.add(tableEntity("T11", 8, 6, 411.4, 194.1, 130, 95, 0, TableShapeEnum.RECT, zoneIndoor, plan, now));
        // Terrass: T12–T23 (12 lauda)
        tables.add(tableEntity("T12", 2, 1, 640.8, 102.8, 70, 60, 0, TableShapeEnum.CIRCLE, zoneTerrace, plan, now));
        tables.add(tableEntity("T13", 2, 1, 373.0, 105.3, 70, 60, 0, TableShapeEnum.RECT, zoneTerrace, plan, now));
        tables.add(tableEntity("T14", 2, 1, 642.7, 203.5, 70, 60, 0, TableShapeEnum.CIRCLE, zoneTerrace, plan, now));
        tables.add(tableEntity("T15", 4, 2, 627.8, 300.8, 90, 80, 0, TableShapeEnum.OVAL, zoneTerrace, plan, now));
        tables.add(tableEntity("T16", 4, 2, 363.3, 209.3, 90, 80, 0, TableShapeEnum.RECT, zoneTerrace, plan, now));
        tables.add(tableEntity("T17", 4, 2, 630.8, 416.6, 90, 80, 0, TableShapeEnum.RECT, zoneTerrace, plan, now));
        tables.add(tableEntity("T18", 4, 2, 135.0, 92.1, 90, 80, 0, TableShapeEnum.CIRCLE, zoneTerrace, plan, now));
        tables.add(tableEntity("T19", 6, 4, 356.5, 329.2, 110, 85, 0, TableShapeEnum.RECT, zoneTerrace, plan, now));
        tables.add(tableEntity("T20", 6, 4, 127.0, 207.0, 110, 85, 0, TableShapeEnum.RECT, zoneTerrace, plan, now));
        tables.add(tableEntity("T21", 2, 1, 142.2, 332.4, 70, 60, 0, TableShapeEnum.CIRCLE, zoneTerrace, plan, now));
        tables.add(tableEntity("T22", 4, 2, 363.4, 450.7, 90, 80, 0, TableShapeEnum.OVAL, zoneTerrace, plan, now));
        tables.add(tableEntity("T23", 8, 6, 109.9, 430.9, 130, 95, 0, TableShapeEnum.RECT, zoneTerrace, plan, now));
        // Privaatruum: T24 – üks pikk laud 12 inimesele
        tables.add(tableEntity("T24", 12, 8, 239.8, 319.3, 320, 80, 0, TableShapeEnum.RECT, zonePrivate, plan, now));
        tables = tableEntityRepository.saveAll(tables);
        log.info("Created {} tables (Sisesaal 11, Terrass 12, Privaatruum 1).", tables.size());

        // Adjacent tables for combined booking: T7–T8 (Sisesaal), T19–T20 (Terrass)
        TableEntity t7 = tables.stream().filter(t -> "T7".equals(t.getLabel())).findFirst().orElseThrow();
        TableEntity t8 = tables.stream().filter(t -> "T8".equals(t.getLabel())).findFirst().orElseThrow();
        TableEntity t19 = tables.stream().filter(t -> "T19".equals(t.getLabel())).findFirst().orElseThrow();
        TableEntity t20 = tables.stream().filter(t -> "T20".equals(t.getLabel())).findFirst().orElseThrow();
        t7.setAdjacentTables(new HashSet<>(Set.of(t8)));
        t8.setAdjacentTables(new HashSet<>(Set.of(t7)));
        t19.setAdjacentTables(new HashSet<>(Set.of(t20)));
        t20.setAdjacentTables(new HashSet<>(Set.of(t19)));
        tableEntityRepository.saveAll(List.of(t7, t8, t19, t20));

        // Laua feature'd (iga laud omab oma eelistusi, nt akna all, vaikne)
        setTableFeatures(tables, "T1", window);
        setTableFeatures(tables, "T2", quiet);
        setTableFeatures(tables, "T3", nearBar);
        setTableFeatures(tables, "T4", accessible);
        setTableFeatures(tables, "T5", window, quiet);
        setTableFeatures(tables, "T6", center);
        setTableFeatures(tables, "T7", nearBar);
        setTableFeatures(tables, "T8", nearBar);
        setTableFeatures(tables, "T9", window);
        setTableFeatures(tables, "T10", quiet);
        setTableFeatures(tables, "T11", center);
        setTableFeatures(tables, "T12", window);
        setTableFeatures(tables, "T13", accessible);
        setTableFeatures(tables, "T14", window);
        setTableFeatures(tables, "T15", window, accessible);
        setTableFeatures(tables, "T16", accessible);
        setTableFeatures(tables, "T17", window);
        setTableFeatures(tables, "T18", quiet);
        setTableFeatures(tables, "T19", accessible);
        setTableFeatures(tables, "T20", window);
        setTableFeatures(tables, "T21", window);
        setTableFeatures(tables, "T22", accessible);
        setTableFeatures(tables, "T23", window, accessible);
        setTableFeatures(tables, "T24", privacy, quiet);
        tableEntityRepository.saveAll(tables);
        log.info("Assigned table-level features (laud = akna all / vaikne / jms).");

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

        // 7) Demo customer
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
        bookingTableRepository.save(BookingTable.builder().booking(b2).tableEntity(tables.get(3)).createdAt(now).updatedAt(now).build());

        Booking b3 = booking("Liis Tamm", "liis@example.ee", today.plusHours(18), today.plusHours(18 + durationHours), 6, customer, now);
        b3 = bookingRepository.save(b3);
        bookingTableRepository.save(BookingTable.builder().booking(b3).tableEntity(t7).createdAt(now).updatedAt(now).build());

        Booking b4 = booking("Oliver Saar", "oliver@example.ee", today.plusHours(13), today.plusHours(13 + durationHours), 2, customer, now);
        b4 = bookingRepository.save(b4);
        bookingTableRepository.save(BookingTable.builder().booking(b4).tableEntity(tables.get(11)).createdAt(now).updatedAt(now).build());
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

    private static void setTableFeatures(List<TableEntity> tables, String label, Feature... featureArray) {
        TableEntity t = tables.stream().filter(tbl -> label.equals(tbl.getLabel())).findFirst().orElseThrow();
        t.setFeatures(new HashSet<>(List.of(featureArray)));
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
