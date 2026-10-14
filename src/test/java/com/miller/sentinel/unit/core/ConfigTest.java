package com.miller.sentinel.unit.core;

import com.miller.sentinel.core.config.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConfigTest {

    private static final String MISSING_KEY = "sentinel.missing.key";

    @AfterEach
    void clearSystemProperties() {
        System.clearProperty("base.url");
        System.clearProperty("timeout.seconds");
        System.clearProperty("headless");
        System.clearProperty("home");
    }

    @Test
    void readsValueFromPropertiesFile() {
        assertThat(Config.get("base.url")).isEqualTo("https://automationexercise.com");
    }

    @Test
    void systemPropertyOverridesPropertiesFile() {
        System.setProperty("base.url", "https://override.example");

        assertThat(Config.get("base.url")).isEqualTo("https://override.example");
    }

    @Test
    void environmentVariableIsUsedWhenNoSystemPropertyIsSet() {
        // HOME exists on macOS and on the CI runner; key "home" maps to env var HOME
        assertThat(Config.get("home")).isEqualTo(System.getenv("HOME"));
    }

    @Test
    void systemPropertyOverridesEnvironmentVariable() {
        System.setProperty("home", "/from/system/property");

        assertThat(Config.get("home")).isEqualTo("/from/system/property");
    }

    @Test
    void convertsIntegerValues() {
        System.setProperty("timeout.seconds", " 25 ");

        assertThat(Config.getInt("timeout.seconds")).isEqualTo(25);
    }

    @Test
    void convertsBooleanValuesIgnoringCase() {
        System.setProperty("headless", "TRUE");
        assertThat(Config.getBool("headless")).isTrue();

        System.setProperty("headless", "false");
        assertThat(Config.getBool("headless")).isFalse();
    }

    @Test
    void rejectsValuesThatAreNotIntegers() {
        System.setProperty("timeout.seconds", "ten");

        assertThatThrownBy(() -> Config.getInt("timeout.seconds"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("timeout.seconds");
    }

    @Test
    void rejectsValuesThatAreNotBooleans() {
        System.setProperty("headless", "yes");

        assertThatThrownBy(() -> Config.getBool("headless"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("headless");
    }

    @Test
    void missingKeyThrowsWithKeyName() {
        assertThatThrownBy(() -> Config.get(MISSING_KEY))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(MISSING_KEY);
    }
}
