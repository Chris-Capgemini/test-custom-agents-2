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

        this.view.button.addActionListener(_ -> {
            try {
                model.action();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        initializeBindings();
    }

    private void updateTextModel(DocumentEvent e, ModelProperties prop) {
        try {
            var content = e.getDocument().getText(0, e.getDocument().getLength());
            @SuppressWarnings("unchecked")
            var fieldModel = (ValueModel<String>) PocPresenter.this.model.model.get(prop);
            fieldModel.setField(content);
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void bind(JTextComponent source, ModelProperties prop) {
        @SuppressWarnings("unchecked")
        var fieldModel = (ValueModel<String>) PocPresenter.this.model.model.get(prop);
        fieldModel.setField(source.getText());
        source.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                System.out.println("I am in insert update. " + e.getDocument().getLength() + " chars");
                updateTextModel(e, prop);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                System.out.println("I am in remove update. " + e.getDocument().getLength() + " chars");
                updateTextModel(e, prop);
            }

            @Override
            public void changedUpdate(DocumentEvent e) { }
        });
    }

    private void bind(JRadioButton source, ModelProperties prop) {
        @SuppressWarnings("unchecked")
        var fieldModel = (ValueModel<Boolean>) PocPresenter.this.model.model.get(prop);
        fieldModel.setField(source.isSelected());
        source.addChangeListener(_ -> {
            fieldModel.setField(source.isSelected());
            System.out.println(source.isSelected());
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

