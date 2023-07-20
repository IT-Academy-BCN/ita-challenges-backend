package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.exception.ConverterException;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConverterAbstractTest {

    @Test
    void testConvertObjectsWithSameFields() throws ConverterException {
        ConverterAbstract<SourceObject, TargetObject> converter = new ConverterAbstract<>(SourceObject.class, TargetObject.class);
        SourceObject source = new SourceObject();
        source.setField1("Hello");
        source.setField2("Bye");

        TargetObject target = converter.convert(source);

        assertEquals(source.getField1(), target.getField1());
        assertEquals(source.getField2(), target.getField2());
    }

    @Test
    void testConvertObjectsWithDifferentFields() throws ConverterException {
        ConverterAbstract<SourceObject, TargetObjectDifferentField> converter = new ConverterAbstract<>(SourceObject.class, TargetObjectDifferentField.class);
        SourceObject source = new SourceObject();
        source.setField1("Hello");
        source.setField2("Bye");

        TargetObjectDifferentField target = converter.convert(source);

        assertNotEquals(source.getField1(), target.getField_1());
        assertNotEquals(source.getField2(), target.getField2());
    }

    @Test
    void testConvertObjectWithAdditionalFieldsInTarget() throws ConverterException {
        ConverterAbstract<SourceObject, TargetObjectExtraField> converter = new ConverterAbstract<>(SourceObject.class, TargetObjectExtraField.class);
        SourceObject source = new SourceObject();
        source.setField1("Hello");
        source.setField2("Bye");

        TargetObjectExtraField target = converter.convert(source);

        assertEquals(source.getField1(), target.getField1());
        assertEquals(source.getField2(), target.getField2());
        assertNull(target.getExtraField());
    }


    @Getter
    @Setter
    public static class SourceObject {
        private String field1;
        private String field2;
    }

    @Getter
    @Setter
    public static class TargetObject {
        private String field1;
        private String field2;
    }

    @Getter
    @Setter
    public static class TargetObjectDifferentField {
        private String field_1;
        private int field2;
    }

    @Getter
    @Setter
    public static class TargetObjectExtraField {
        private String field1;
        private String field2;
        private String extraField;
    }

}