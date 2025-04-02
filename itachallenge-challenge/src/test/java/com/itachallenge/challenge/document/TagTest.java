package com.itachallenge.challenge.document;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TagTest {

    @Test
    void getTestName() {
        String tagNameTest = "POO";
        TagDocument tag = new TagDocument(null, tagNameTest, null);
        assertEquals(tagNameTest, tag.getTagName());
    }

    @Test
    void getDescriptionTest() {
        String tagDescriptionTest = "Programació orientada a objectes";
        TagDocument tag = new TagDocument(null, "POO", tagDescriptionTest);
        assertEquals(tagDescriptionTest, tag.getTagDescription());
    }

    @Test
    void setTestName() {
        String firstTagName = "POO";
        String tagNameTest = "TEST";
        TagDocument tag = new TagDocument(null, firstTagName, null);
        tag.setTagName(tagNameTest);
        assertEquals(tagNameTest, tag.getTagName());
    }

    @Test
    void setDescriptionTest() {
        String firstTagDescription = "Programació orientada a objectes";
        String tagDescriptionTest = "TEST";
        TagDocument tag = new TagDocument(null, "POO", firstTagDescription);
        tag.setTagDescription(tagDescriptionTest);
        assertEquals(tagDescriptionTest, tag.getTagDescription());
    }

    @Test
    void fullConstructorTest() {
        UUID id = UUID.randomUUID();
        String name = "POO";
        String desc = "Programació orientada a objectes";

        TagDocument tag = new TagDocument(id, name, desc);

        assertEquals(id, tag.getIdTag());
        assertEquals(name, tag.getTagName());
        assertEquals(desc, tag.getTagDescription());
    }
    
}
