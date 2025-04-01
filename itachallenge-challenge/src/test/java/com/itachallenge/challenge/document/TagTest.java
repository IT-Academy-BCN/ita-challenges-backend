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

    @Test
    void equalsShouldReturnTrueForSameId() {
        UUID id = UUID.randomUUID();

        TagDocument tag1 = new TagDocument(id, "POO", "desc1");
        TagDocument tag2 = new TagDocument(id, "Another", "desc2");

        assertEquals(tag1, tag2);
        assertEquals(tag1.hashCode(), tag2.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentId() {
        TagDocument tag1 = new TagDocument(UUID.randomUUID(), "POO", "desc1");
        TagDocument tag2 = new TagDocument(UUID.randomUUID(), "POO", "desc1");

        assertNotEquals(tag1, tag2);
    }

    @Test
    void equalsShouldReturnFalseWhenComparingWithNull() {
        TagDocument tag = new TagDocument(UUID.randomUUID(), "POO", "desc");
        assertNotEquals(tag, null);
    }

    @Test
    void equalsShouldReturnFalseWhenComparingWithDifferentClass() {
        TagDocument tag = new TagDocument(UUID.randomUUID(), "POO", "desc");
        assertNotEquals(tag, "some string");
    }

    @Test
    void hashSetShouldContainOnlyOneElementWithSameId() {
        UUID id = UUID.randomUUID();

        TagDocument tag1 = new TagDocument(id, "POO", "desc1");
        TagDocument tag2 = new TagDocument(id, "POO", "desc1");

        Set<TagDocument> tags = new HashSet<>();
        tags.add(tag1);
        tags.add(tag2); // debería ser ignorado por equals/hashCode

        assertEquals(1, tags.size());
    }
}
