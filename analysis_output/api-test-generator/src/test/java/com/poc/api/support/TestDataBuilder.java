package com.poc.api.support;

import java.util.HashMap;
import java.util.Map;

/**
 * Builds realistic test data for the Allegro PoC POST endpoint.
 * Provides pre-configured user personas and fluent builder methods.
 */
public class TestDataBuilder {

    /**
     * Returns a complete set of test data for a male user named "Hans Müller".
     */
    public static Map<String, String> hansMueller() {
        Map<String, String> data = new HashMap<>();
        data.put("FIRST_NAME", "Hans");
        data.put("LAST_NAME", "Müller");
        data.put("DATE_OF_BIRTH", "01.01.1980");
        data.put("STREET", "Hauptstraße 1");
        data.put("ZIP", "80331");
        data.put("ORT", "München");
        data.put("IBAN", "DE89370400440532013000");
        data.put("BIC", "COBADEFFXXX");
        data.put("VALID_FROM", "01.01.2024");
        data.put("MALE", "true");
        data.put("FEMALE", "false");
        data.put("DIVERSE", "false");
        data.put("TEXT_AREA", "Test submission");
        return data;
    }

    /**
     * Returns a complete set of test data for a female user named "Maria Schmidt".
     */
    public static Map<String, String> mariaSchmidt() {
        Map<String, String> data = new HashMap<>();
        data.put("FIRST_NAME", "Maria");
        data.put("LAST_NAME", "Schmidt");
        data.put("DATE_OF_BIRTH", "15.06.1990");
        data.put("STREET", "Bahnhofstraße 5");
        data.put("ZIP", "10115");
        data.put("ORT", "Berlin");
        data.put("IBAN", "DE44500105175407324931");
        data.put("BIC", "BELADEBEXXX");
        data.put("VALID_FROM", "01.03.2024");
        data.put("MALE", "false");
        data.put("FEMALE", "true");
        data.put("DIVERSE", "false");
        data.put("TEXT_AREA", "Initial setup");
        return data;
    }

    /**
     * Returns a complete set of test data for a diverse user named "Alex Weber".
     */
    public static Map<String, String> alexWeber() {
        Map<String, String> data = new HashMap<>();
        data.put("FIRST_NAME", "Alex");
        data.put("LAST_NAME", "Weber");
        data.put("DATE_OF_BIRTH", "22.11.1995");
        data.put("STREET", "Schillerplatz 3");
        data.put("ZIP", "70173");
        data.put("ORT", "Stuttgart");
        data.put("IBAN", "DE91100000000123456789");
        data.put("BIC", "MARKDEFFXXX");
        data.put("VALID_FROM", "15.04.2024");
        data.put("MALE", "false");
        data.put("FEMALE", "false");
        data.put("DIVERSE", "true");
        data.put("TEXT_AREA", "User registration");
        return data;
    }

    /**
     * Returns an empty (but non-null) map representing minimal test data.
     */
    public static Map<String, String> empty() {
        return new HashMap<>();
    }

    /**
     * Returns a builder seeded with the given base data, allowing individual fields
     * to be overridden via {@link Builder#with(String, String)}.
     */
    public static Builder from(Map<String, String> baseData) {
        return new Builder(new HashMap<>(baseData));
    }

    /**
     * Fluent builder for constructing test data by starting from an existing map and
     * overriding individual fields.
     */
    public static class Builder {
        private final Map<String, String> data;

        private Builder(Map<String, String> data) {
            this.data = data;
        }

        public Builder with(String field, String value) {
            data.put(field, value);
            return this;
        }

        public Map<String, String> build() {
            return data;
        }
    }
}
