package uk.me.michael_winter.junit.plugins.parameterised.runners;

import org.junit.Test;

import static org.junit.Assert.fail;

public class FailingTestExample {
    @Test
    public void aFailingTest() {
        fail("This test is expected to fail once");
    }
}
