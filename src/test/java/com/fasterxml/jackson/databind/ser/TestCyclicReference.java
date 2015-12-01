package com.fasterxml.jackson.databind.ser;


import java.io.IOException;
import java.io.StringWriter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Test for verifying Cyclic references
 *
 * @author <a href="mailto:dilip.gundu@wavemaker.com">Dilip Kumar</a>
 * @since 14/5/15
 */
public class TestCyclicReference extends BaseMapTest {
    static class A {
        @JsonProperty
        private B b;
    }

    static class B {
        @JsonProperty
        private C c;
    }

    static class C {
        @JsonProperty
        private A a;
        @JsonProperty
        private D d;
    }

    static class D {
        @JsonProperty
        private A a;
        @JsonProperty
        private B b;
        @JsonProperty
        private C c;
    }



    public void testCyclicReferenceDefaultCase() throws IOException {
        A a = new A();
        B b = new B();
        C c = new C();

        a.b = b;
        b.c = c;
        c.a = a; // cycle

        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter stringWriter = new StringWriter();
        objectMapper.writeValue(stringWriter, a); // should ignore "b.c.a" as null
        assertEquals("{\"b\":{\"c\":{\"a\":null,\"d\":null}}}", stringWriter.toString());

    }

    public void testCyclicReferenceFailCase() throws IOException {
        A a = new A();
        B b = new B();
        C c = new C();

        a.b = b;
        b.c = c;
        c.a = a; // cycle

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_CYCLIC_REFERENCES, true);
        StringWriter writer = new StringWriter();
        try {
            objectMapper.writeValue(writer, a);
            fail("Should fail on Cyclic Reference");
        } catch (JsonMappingException e) {
            verifyException(e, "Cyclic-reference leading to cycle, Object Reference Stack:A->B->C->A");
        }
    }
    public void testCyclicReferenceDefaultCase2() throws IOException {
        A a = new A();
        B b = new B();
        C c = new C();
        D d = new D();

        a.b = b;
        b.c = c;
        c.d = d;
        d.b = b; // cycle


        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter stringWriter = new StringWriter();
        objectMapper.writeValue(stringWriter, a); // should ignore "b.c.d.b" as null
        assertEquals("{\"b\":{\"c\":{\"a\":null,\"d\":{\"a\":null,\"b\":null,\"c\":null}}}}", stringWriter.toString());
    }

    public void testCyclicReferenceFailCase2() throws IOException {
        A a = new A();
        B b = new B();
        C c = new C();
        D d = new D();

        a.b = b;
        b.c = c;
        c.d = d;
        d.b = b; // cycle

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_CYCLIC_REFERENCES, true);
        StringWriter writer = new StringWriter();
        try {
            objectMapper.writeValue(writer, a);
            fail("Should fail on Cyclic Reference");
        } catch (JsonMappingException e) {
            verifyException(e, "Cyclic-reference leading to cycle, Object Reference Stack:A->B->C->D->B");
        }
    }

}
