package com.itachallenge.challenge.documents;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DetailTest {

    @Test
    public void testDetailClass() {
        // Init data
        String description = "Challenge description";
        List<Example> examples = Arrays.asList(
                new Example(UUID.fromString("0172b4b5-6a4f-4239-b5da-1638e7fb81f3"), "Input 1"),
                new Example(UUID.fromString("46177adc-8d9f-4975-9661-d8b80ebd29f8"), "Input 2")
        );
        String note = "Additional note";

        // Act
        Detail detail = Detail.builder()
                .description(description)
                .examples(examples)
                .note(note)
                .build();

        // Assert
        Assertions.assertEquals(description, detail.getDescription());
        Assertions.assertEquals(examples, detail.getExamples());
        Assertions.assertEquals(note, detail.getNote());
    }

    @Test
    public void testDetailDataMethods() {
        // Init data
        Detail detail = Detail.builder()
                .description("Descripción del detalle")
                .examples(new ArrayList<>())
                .note("Nota del detalle")
                .build();

        // toString()
        String expectedToString = "Detail(description=Descripción del detalle, examples=[], note=Nota del detalle)";
        Assertions.assertEquals(expectedToString, detail.toString());

        // getter y setter
        String newDescription = "Nueva descripción";
        detail.setDescription(newDescription);
        Assertions.assertEquals(newDescription, detail.getDescription());

        List<Example> newExamples = new ArrayList<>();
        detail.setExamples(newExamples);
        Assertions.assertEquals(newExamples, detail.getExamples());

        String newNote = "Nueva nota";
        detail.setNote(newNote);
        Assertions.assertEquals(newNote, detail.getNote());

        // equals()
        Detail equalDetail = Detail.builder()
                .description(newDescription)
                .examples(newExamples)
                .note(newNote)
                .build();
        Assertions.assertEquals(detail, equalDetail);

        // hashCode()
        Assertions.assertEquals(detail.hashCode(), equalDetail.hashCode());
    }

}
