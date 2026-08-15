package com.poc.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EventEmitter")
class EventEmitterTest {

    private EventEmitter emitter;
    private List<String> received;

    @BeforeEach
    void setUp() {
        emitter  = new EventEmitter();
        received = new ArrayList<>();
    }

    @Test
    @DisplayName("single subscriber receives emitted event")
    void singleSubscriberReceivesEvent() {
        emitter.subscribe(received::add);
        emitter.emit("hello");
        assertEquals(List.of("hello"), received);
    }

    @Test
    @DisplayName("multiple subscribers all receive the same event")
    void multipleSubscribersReceiveEvent() {
        var second = new ArrayList<String>();
        emitter.subscribe(received::add);
        emitter.subscribe(second::add);
        emitter.emit("broadcast");
        assertEquals(List.of("broadcast"), received);
        assertEquals(List.of("broadcast"), second);
    }

    @Test
    @DisplayName("events are delivered in subscription order")
    void eventsDeliveredInSubscriptionOrder() {
        var order = new ArrayList<Integer>();
        emitter.subscribe(e -> order.add(1));
        emitter.subscribe(e -> order.add(2));
        emitter.subscribe(e -> order.add(3));
        emitter.emit("tick");
        assertEquals(List.of(1, 2, 3), order);
    }

    @Test
    @DisplayName("no subscribers means emit is a no-op")
    void noSubscribersNoOp() {
        assertDoesNotThrow(() -> emitter.emit("nothing"));
    }

    @Test
    @DisplayName("emitting multiple events delivers each to the subscriber")
    void multipleEmits() {
        emitter.subscribe(received::add);
        emitter.emit("first");
        emitter.emit("second");
        assertEquals(List.of("first", "second"), received);
    }
}
