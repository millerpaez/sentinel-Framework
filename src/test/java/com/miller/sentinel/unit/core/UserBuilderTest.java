package com.miller.sentinel.unit.core;

import com.miller.sentinel.app.data.UserBuilder;
import com.miller.sentinel.app.data.UserData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserBuilderTest {

    @Test
    void generatesUniqueEmailsForEachUser() {
        UserData first = UserBuilder.aUser().build();
        UserData second = UserBuilder.aUser().build();

        assertThat(first.email()).isNotEqualTo(second.email());
    }

    @Test
    void overridesOnlyTheRequestedFields() {
        UserData user = UserBuilder.aUser()
                .withPassword("Secret123")
                .withEmail("fixed@sentinel.test")
                .build();

        assertThat(user.password()).isEqualTo("Secret123");
        assertThat(user.email()).isEqualTo("fixed@sentinel.test");
        assertThat(user.name()).isNotBlank();
    }

    @Test
    void fillsEveryFieldRequiredByTheApi() {
        UserData user = UserBuilder.aUser().build();

        assertThat(user.toFormParams())
                .hasSize(17)
                .containsKeys("name", "email", "password", "title", "birth_date", "birth_month", "birth_year",
                        "firstname", "lastname", "company", "address1", "address2", "country", "zipcode",
                        "state", "city", "mobile_number")
                .allSatisfy((key, value) -> assertThat(value).as(key).isNotBlank());
    }

    @Test
    void usesACountryAcceptedByTheSite() {
        UserData user = UserBuilder.aUser().build();

        assertThat(UserBuilder.SUPPORTED_COUNTRIES).contains(user.country());
    }
}
