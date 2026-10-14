package com.miller.sentinel.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

/**
 * Read-only access to the framework configuration.
 *
 * <p>Lookup order for a key (first match wins):
 * <ol>
 *   <li>JVM system property, e.g. {@code -Dheadless=true}</li>
 *   <li>Environment variable: key upper-cased with dots replaced by underscores, e.g. {@code HEADLESS}</li>
 *   <li>{@code config.properties} on the test classpath</li>
 * </ol>
 */
public final class Config {

    private static final String FILE_NAME = "config.properties";
    private static final Properties FILE_PROPERTIES = load();

    private Config() {
    }

    public static String get(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null) {
            return systemValue;
        }
        String envValue = System.getenv(toEnvName(key));
        if (envValue != null) {
            return envValue;
        }
        String fileValue = FILE_PROPERTIES.getProperty(key);
        if (fileValue != null) {
            return fileValue;
        }
        throw new IllegalStateException("Missing configuration key: '" + key + "'");
    }

    public static boolean getBool(String key) {
        String value = get(key).trim();
        if (value.equalsIgnoreCase("true")) {
            return true;
        }
        if (value.equalsIgnoreCase("false")) {
            return false;
        }
        throw new IllegalStateException("Configuration key '" + key + "' is not a boolean: '" + value + "'");
    }

    public static int getInt(String key) {
        String value = get(key).trim();
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Configuration key '" + key + "' is not an integer: '" + value + "'", e);
        }
    }

    private static String toEnvName(String key) {
        return key.toUpperCase(Locale.ROOT).replace('.', '_');
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream(FILE_NAME)) {
            if (in == null) {
                throw new IllegalStateException(FILE_NAME + " not found on the classpath");
            }
            properties.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + FILE_NAME, e);
        }
        return properties;
    }
}
