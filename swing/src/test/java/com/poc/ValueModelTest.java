package com.poc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ValueModel<T>")
class ValueModelTest {

    @Test
    @DisplayName("constructor stores initial value")
    void constructorStoresInitialValue() {
        var model = new ValueModel<>("hello");
        assertEquals("hello", model.getField());
    }

    @Test
    @DisplayName("constructor accepts null as initial value")
    void constructorAcceptsNull() {
        var model = new ValueModel<String>(null);
        assertNull(model.getField());
    }

    @Test
    @DisplayName("setField updates the stored value")
    void setFieldUpdatesValue() {
        var model = new ValueModel<>("initial");
        model.setField("updated");
        assertEquals("updated", model.getField());
    }

    @Test
    @DisplayName("setField to null clears the value")
    void setFieldToNull() {
        var model = new ValueModel<>("something");
        model.setField(null);
        assertNull(model.getField());
    }

    @Test
    @DisplayName("works with Boolean type parameter")
    void worksWithBoolean() {
        var model = new ValueModel<>(false);
        assertFalse(model.getField());
        model.setField(true);
        assertTrue(model.getField());
    }
}
