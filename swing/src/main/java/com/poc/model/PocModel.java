package com.poc.model;

import com.poc.ValueModel;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/**
 * Application model holding all UI-bound values.
 * Wraps an {@link EnumMap} keyed by {@link ModelProperties}.
 */
public class PocModel {

    private final Map<ModelProperties, ValueModel<?>> model = new EnumMap<>(ModelProperties.class);
    private final HttpBinService httpBinService = new HttpBinService();
    private final EventEmitter eventEmitter;

    public PocModel(EventEmitter eventEmitter) {
        for (var prop : ModelProperties.values()) {
            model.put(prop, new ValueModel<>(null));
        }
        this.eventEmitter = eventEmitter;
    }

    /** Returns the {@link ValueModel} for the given property. */
    public ValueModel<?> get(ModelProperties prop) {
        return model.get(prop);
    }

    public void action() throws IOException, InterruptedException {
        // Build a snapshot of the current model state
        var data = new java.util.HashMap<String, String>();
        for (var prop : ModelProperties.values()) {
            var raw = model.get(prop).getField();
            // String.valueOf handles null gracefully ("null")
            data.put(prop.toString(), String.valueOf(raw));
            System.out.println(prop + ": " + raw);
        }

        var responseBody = httpBinService.post(data);
        eventEmitter.emit(responseBody.isEmpty() ? "Failed operation" : responseBody);
    }
}
