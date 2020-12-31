package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Test;

@SuppressWarnings({"unused", "TestMethodWithIncorrectSignature"})
public class TestExample {
    @Test
    public void aValidTest() {
    }

    @Test
    public void anotherValidTest() {
    }

    @Test
    public void aValidTest(int value) {
    }

    @Test
    public void aValidTest(int first, Object second) {
    }

    @Test
    public void anotherValidTest(int value) {
    }

    @SuppressWarnings("unused")
    public void nonTestMethod() {
    }
}
