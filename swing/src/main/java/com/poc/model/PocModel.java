package com.poc.model;

import com.poc.ValueModel;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PocModel {

    private final Map<ModelProperties, ValueModel<?>> model = new EnumMap<>(ModelProperties.class);
    private final HttpBinService httpBinService = new HttpBinService();
    private final EventEmitter eventEmitter;

    public PocModel(EventEmitter eventEmitter) {
        this.eventEmitter = eventEmitter;
        // Initialise every model property to an empty ValueModel
        for (var prop : ModelProperties.values()) {
            model.put(prop, new ValueModel<>(null));
        }
    }

    /**
     * Returns the {@link ValueModel} bound to the given property.
     * Callers that know the concrete type may cast the result.
     */
    public ValueModel<?> getValueModel(ModelProperties prop) {
        return model.get(prop);
    }

    public void action() throws IOException, InterruptedException {
        // Log every field and collect into a Map for the HTTP call
        var data = Arrays.stream(ModelProperties.values())
                .peek(val -> System.out.printf("%s: %s%n", val, model.get(val).getField()))
                .collect(Collectors.toMap(
                        Enum::name,
                        // String.valueOf(null) safely returns "null" instead of NPE
                        val -> String.valueOf(model.get(val).getField())
                ));

        var responseBody = httpBinService.post(data);
        eventEmitter.emit(responseBody.isEmpty() ? "Failed operation" : responseBody);
    }
}
