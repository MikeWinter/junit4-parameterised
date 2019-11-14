package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Test;

public class InvalidTestReturnTypeExample {
    @Test
    @SuppressWarnings("TestMethodWithIncorrectSignature")
    public int testReturnsAValue() {
        return 0;
    }
}
