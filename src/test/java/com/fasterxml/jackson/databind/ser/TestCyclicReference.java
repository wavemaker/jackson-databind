package com.fasterxml.jackson.databind.ser;


import java.io.IOException;
import java.io.StringWriter;

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
        private B b;
        private String value = "A";

        public B getB() {
            return b;
        }

        public void setB(final B b) {
            this.b = b;
        }
    }

    static class B {
        private C c;
        private String value = "B";

        public C getC() {
            return c;
        }

        public void setC(final C c) {
            this.c = c;
        }
    }

    static class C {
        private A a;
        private String value = "C";

        public A getA() {
            return a;
        }

        public void setA(final A a) {
            this.a = a;
        }
    }

    public void testCyclicReferenceDefaultCase() throws IOException {
        A a = new A();
        B b = new B();
        C c = new C();

        a.b = b; // cycle
        b.c = c;
        c.a = a;

        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            objectMapper.writeValue(writer, a);
            fail("Should fail on Cyclic Reference");
        } catch (JsonMappingException e) {
            verifyException(e, "Cyclic-reference leading to cycle, Object Reference Stack:A->B->C");
        }


    }

    public void testCyclicReferenceFailCase() throws IOException {
        A a = new A();
        B b = new B();
        C c = new C();

        a.b = b; // cycle
        b.c = c;
        c.a = a;

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_CYCLIC_REFERENCES, false);
        StringWriter stringWriter = new StringWriter();
        objectMapper.writeValue(stringWriter, a); // ignore as null
        assertEquals("{\"b\":{\"c\":{\"a\":null}}}", stringWriter.toString());
    }

}
