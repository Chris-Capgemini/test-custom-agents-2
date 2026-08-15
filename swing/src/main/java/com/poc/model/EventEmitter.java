package com.poc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple observable event bus for string events.
 * Listeners are notified in subscription order.
 */
public class EventEmitter {

    private final List<EventListener> listeners = new ArrayList<>();

    public void subscribe(EventListener listener) {
        listeners.add(listener);
    }

    public void emit(String eventData) {
        listeners.forEach(listener -> listener.onEvent(eventData));
    }
}
