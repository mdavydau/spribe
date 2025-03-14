package com.mdavydau.spribe.utils;

import com.mdavydau.spribe.entity.AccommodationType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class UnitRandomUtil {

    public static final Map<AccommodationType, List<String>> ACCOMMODATION_DESCRIPTIONS = new HashMap<>();

    public static void initAllDescriptions() {
        ACCOMMODATION_DESCRIPTIONS.put(AccommodationType.HOME, initHomeDescriptions());
        ACCOMMODATION_DESCRIPTIONS.put(AccommodationType.FLAT, initFlatDescriptions());
        ACCOMMODATION_DESCRIPTIONS.put(AccommodationType.APARTMENT, initApartmentDescriptions());
    }

    public static AccommodationType randomAccommodationType() {
        AccommodationType[] accommodationTypes = AccommodationType.values();
        int randomType = ThreadLocalRandom.current().nextInt(accommodationTypes.length);
        return accommodationTypes[randomType];
    }

    public static String randomAccommodationDescription(AccommodationType accommodationType) {
        List<String> descriptions = UnitRandomUtil.ACCOMMODATION_DESCRIPTIONS.get(accommodationType);
        int randomDescription = ThreadLocalRandom.current().nextInt(descriptions.size());
        return descriptions.get(randomDescription);
    }

    private static List<String> initHomeDescriptions() {
        List<String> homeDescriptions = new ArrayList<>();
        homeDescriptions.add("Charming %s-bedroom family home with spacious backyard and updated kitchen, perfect for growing families.");
        homeDescriptions.add("Cozy %s-bedroom cottage with fireplace, hardwood floors, and walking distance to downtown shops.");
        homeDescriptions.add("Modern %s-bedroom home featuring open floor plan, smart home technology, and two-car garage.");
        homeDescriptions.add("Renovated craftsman home with %s bedrooms, covered porch, and fenced yard in quiet neighborhood.");
        homeDescriptions.add("Sunny ranch-style home with %s bedrooms, large windows, updated appliances, and easy access to parks and schools.");
        return homeDescriptions;
    }

    private static List<String> initFlatDescriptions() {
        List<String> flatDescriptions = new ArrayList<>();
        flatDescriptions.add("Bright %s-bedroom flat in historic building with high ceilings and original moldings.");
        flatDescriptions.add("Compact studio flat with clever storage solutions and newly renovated bathroom.");
        flatDescriptions.add("Elegant %s-bedroom garden flat with private entrance and small patio area.");
        flatDescriptions.add("Contemporary flat featuring %s bedrooms, open concept design, built-in shelving, and city views.");
        flatDescriptions.add("Stylish %s-bedroom flat with exposed brick walls, updated kitchen, and in-unit laundry.");
        return flatDescriptions;
    }

    private static List<String> initApartmentDescriptions() {
        List<String> apartmentDescriptions = new ArrayList<>();
        apartmentDescriptions.add("Luxury %s-bedroom apartment with balcony, stainless steel appliances, and access to fitness center.");
        apartmentDescriptions.add("Affordable studio apartment in secure building with on-site parking and utilities included.");
        apartmentDescriptions.add("Spacious %s-bedroom apartment with large windows, walk-in closets, and community playground.");
        apartmentDescriptions.add("Modern %s-bedroom apartment featuring granite countertops, hardwood floors, and pet-friendly policy.");
        apartmentDescriptions.add("Penthouse apartment with %s bedrooms, panoramic views, chef's kitchen, and private rooftop terrace.");
        return apartmentDescriptions;
    }
}
