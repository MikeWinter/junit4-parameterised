package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Test;

public class InvalidTestReturnTypeExample {
    @Test
    public int testReturnsAValue() {
        return 0;
    }
}
