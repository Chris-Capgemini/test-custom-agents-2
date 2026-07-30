package com.poc.model;

import com.poc.ValueModel;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PocModel {

    public final Map<ModelProperties, ValueModel<?>> model = new EnumMap<>(ModelProperties.class);
    private final HttpBinService httpBinService = new HttpBinService();
    private final EventEmitter eventEmitter;

    public PocModel(EventEmitter eventEmitter) {
        model.put(ModelProperties.TEXT_AREA,    new ValueModel<>(null));
        model.put(ModelProperties.FIRST_NAME,   new ValueModel<>(null));
        model.put(ModelProperties.LAST_NAME,    new ValueModel<>(null));
        model.put(ModelProperties.DATE_OF_BIRTH, new ValueModel<>(null));
        model.put(ModelProperties.ZIP,          new ValueModel<>(null));
        model.put(ModelProperties.ORT,          new ValueModel<>(null));
        model.put(ModelProperties.STREET,       new ValueModel<>(null));
        model.put(ModelProperties.IBAN,         new ValueModel<>(null));
        model.put(ModelProperties.BIC,          new ValueModel<>(null));
        model.put(ModelProperties.VALID_FROM,   new ValueModel<>(null));
        model.put(ModelProperties.MALE,         new ValueModel<>(null));
        model.put(ModelProperties.FEMALE,       new ValueModel<>(null));
        model.put(ModelProperties.DIVERSE,      new ValueModel<>(null));
        this.eventEmitter = eventEmitter;
    }

    public void action() throws IOException, InterruptedException {
        Arrays.stream(ModelProperties.values())
                .forEach(val -> System.out.println(val + ": " + model.get(val).getField()));

        var data = Arrays.stream(ModelProperties.values())
                .collect(Collectors.toMap(
                        Enum::toString,
                        val -> {
                            var field = model.get(val).getField();
                            return field != null ? field.toString() : "";
                        }
                ));

        var responseBody = httpBinService.post(data);
        eventEmitter.emit(responseBody.isEmpty() ? "Failed operation" : responseBody);
    }
}
