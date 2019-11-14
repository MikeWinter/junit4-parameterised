package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Test;

import static org.junit.Assert.fail;

@SuppressWarnings({"unused", "InnerClassMayBeStatic"})
public class InvalidNonStaticInnerClassExample {
    public class TestClass {
        @Test
        public void aTest() {
            fail("This should not be invoked");
        }
    }
}
