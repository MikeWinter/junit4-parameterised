package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Test;

public class InvalidStaticTestMethodExample {
    @Test
    @SuppressWarnings({"unused", "TestMethodWithIncorrectSignature"})
    public static void testIsStatic() {
    }
}
