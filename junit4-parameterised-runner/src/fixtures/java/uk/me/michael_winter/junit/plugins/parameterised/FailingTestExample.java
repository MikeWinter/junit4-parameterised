package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Test;

import static org.junit.Assert.fail;

@SuppressWarnings({"unused", "TestMethodWithIncorrectSignature"})
public class FailingTestExample {
    @Test
    public void aFailingTest() {
        fail("This test is expected to fail once");
    }

    @Test
    public void aFailingTest(int value) {
        fail("This test is expected to fail once");
    }
}
