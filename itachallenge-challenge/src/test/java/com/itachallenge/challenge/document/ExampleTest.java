package com.itachallenge.challenge.document;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExampleTest {

    @Test
    void getIdExample() {
<<<<<<< HEAD
        UUID idExample = UUID.randomUUID();
        ExampleDocument example = new ExampleDocument(idExample, null);
        assertEquals(idExample, example.getIdExample());
    }
=======
    UUID idExample = UUID.randomUUID();
    ExampleDocument example = new ExampleDocument(idExample, null);
    assertEquals(idExample, example.getIdExample());
}
>>>>>>> faa6b865630adce6a47333b3e952261a233e6e39

    @Test
    void getExampleText() {
        String exampleText = "Example Text";
        ExampleDocument example = new ExampleDocument(null, exampleText);
        assertEquals(exampleText, example.getExampleText());
    }
}

