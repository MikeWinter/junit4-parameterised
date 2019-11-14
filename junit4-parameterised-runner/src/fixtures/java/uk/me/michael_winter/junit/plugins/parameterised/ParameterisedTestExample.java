package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Test;
import uk.me.michael_winter.junit.plugins.parameterised.annotations.ParameterIntValues;

@SuppressWarnings({"unused", "TestMethodWithIncorrectSignature"})
public class ParameterisedTestExample {
    @Test
    public void aParameterisedTest(@ParameterIntValues(0) int value) {
    }

    @Test
    public void anotherParameterisedTest(@ParameterIntValues(0) int value) {
    }

    public void nonTestMethod() {
    }
}
