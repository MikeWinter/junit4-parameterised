package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Test;

public class InvalidMultipleConstructorsExample {
    public InvalidMultipleConstructorsExample() {
    }

    @SuppressWarnings("unused")
    public InvalidMultipleConstructorsExample(Object arg) {
    }

    @Test
    public void aTest() {
    }
}
