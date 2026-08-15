package com.poc.presentation;

import com.poc.ValueModel;
import com.poc.model.EventEmitter;
import com.poc.model.ModelProperties;
import com.poc.model.PocModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.io.IOException;

public class PocPresenter {

    private final PocView view;
    private final PocModel model;

    public PocPresenter(PocView view, PocModel model, EventEmitter eventEmitter) {
        this.view = view;
        this.model = model;

        // Listen for model events and refresh the view
        eventEmitter.subscribe(eventData -> {
            System.out.println("Event data is : " + eventData);
            view.textArea.setText(eventData);
            view.firstName.setText("");
            view.name.setText("");
            view.dateOfBirth.setText("");
            view.zip.setText("");
            view.ort.setText("");
            view.street.setText("");
            view.iban.setText("");
            view.bic.setText("");
            view.validFrom.setText("");
            view.female.setSelected(true);
            view.male.setSelected(false);
            view.diverse.setSelected(false);
        });

        // Use 'ignored' instead of '_' — unnamed lambda params require Java 22+
        this.view.button.addActionListener(ignored -> {
            try {
                model.action();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        initializeBindings();
    }

    @SuppressWarnings("unchecked")
    private void bind(JTextComponent source, ModelProperties prop) {
        var valueModel = (ValueModel<String>) PocPresenter.this.model.get(prop);
        valueModel.setField(source.getText());
        source.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    var content = e.getDocument().getText(0, e.getDocument().getLength());
                    valueModel.setField(content);
                } catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    var content = e.getDocument().getText(0, e.getDocument().getLength());
                    valueModel.setField(content);
                } catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Attribute changes (e.g. bold/italic) do not affect the model value
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void bind(JRadioButton source, ModelProperties prop) {
        var valueModel = (ValueModel<Boolean>) PocPresenter.this.model.get(prop);
        valueModel.setField(source.isSelected());
        source.addChangeListener(evt -> {
            valueModel.setField(source.isSelected());
            System.out.println(prop + " selected: " + source.isSelected());
        });
    }

    private void initializeBindings() {
        bind(view.textArea, ModelProperties.TEXT_AREA);
        bind(view.firstName, ModelProperties.FIRST_NAME);
        bind(view.name, ModelProperties.LAST_NAME);
        bind(view.dateOfBirth, ModelProperties.DATE_OF_BIRTH);
        bind(view.zip, ModelProperties.ZIP);
        bind(view.ort, ModelProperties.ORT);
        bind(view.street, ModelProperties.STREET);
        bind(view.iban, ModelProperties.IBAN);
        bind(view.bic, ModelProperties.BIC);
        bind(view.validFrom, ModelProperties.VALID_FROM);
        bind(view.male, ModelProperties.MALE);
        bind(view.female, ModelProperties.FEMALE);
        bind(view.diverse, ModelProperties.DIVERSE);
    }
}
