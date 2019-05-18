package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Test;

public class FailingTestUsingExpectedParameterExample {
    @Test(expected = RuntimeException.class)
    public void testUsesExpectedParameter() {
    }
}
