package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Test;

public class ParameterisedTestExample {
    @Test
    @SuppressWarnings("unused")
    public void aParameterisedTest(@ParameterIntValues(0) int value) {
    }

    @Test
    @SuppressWarnings("unused")
    public void anotherParameterisedTest(@ParameterIntValues(0) int value) {
    }

    @SuppressWarnings("unused")
    public void nonTestMethod() {
    }
}
