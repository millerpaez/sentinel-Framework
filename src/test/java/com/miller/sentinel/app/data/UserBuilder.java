package com.miller.sentinel.app.data;

import net.datafaker.Faker;

import java.util.List;
import java.util.UUID;

/**
 * Builds {@link UserData} with random, valid defaults. Override only what a test cares about.
 */
public class UserBuilder {

    /** Options of the "Country" select on the site's signup form. */
    public static final List<String> SUPPORTED_COUNTRIES = List.of(
            "India", "United States", "Canada", "Australia", "Israel", "New Zealand", "Singapore");

    private static final List<String> TITLES = List.of("Mr", "Mrs");

    private final Faker faker = new Faker();

    private String firstname = faker.name().firstName();
    private String lastname = faker.name().lastName();
    private String name = firstname + " " + lastname;
    // UUID fragment: unique across parallel threads and across runs, so scenarios never share an account
    private String email = "sentinel-" + UUID.randomUUID().toString().substring(0, 12) + "@sentinel.test";
    private String password = faker.credentials().password(10, 16, true, false, true);
    private String country = faker.options().nextElement(SUPPORTED_COUNTRIES);

    public static UserBuilder aUser() {
        return new UserBuilder();
    }

    public UserBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder withCountry(String country) {
        this.country = country;
        return this;
    }

    public UserData build() {
        return new UserData(
                name,
                email,
                password,
                faker.options().nextElement(TITLES),
                String.valueOf(faker.number().numberBetween(1, 29)),
                String.valueOf(faker.number().numberBetween(1, 13)),
                String.valueOf(faker.number().numberBetween(1950, 2004)),
                firstname,
                lastname,
                faker.company().name(),
                faker.address().streetAddress(),
                faker.address().secondaryAddress(),
                country,
                faker.address().zipCode(),
                faker.address().state(),
                faker.address().city(),
                faker.numerify("3#########"));
    }
}
