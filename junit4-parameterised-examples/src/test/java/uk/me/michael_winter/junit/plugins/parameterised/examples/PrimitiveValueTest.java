package uk.me.michael_winter.junit.plugins.parameterised.examples;

import org.junit.Test;
import org.junit.runner.RunWith;
import uk.me.michael_winter.junit.plugins.parameterised.ParameterisedRunner;
import uk.me.michael_winter.junit.plugins.parameterised.annotations.ParameterIntValues;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ParameterisedRunner.class)
public class PrimitiveValueTest {
    public PrimitiveValueTest() {
        throw new RuntimeException();
    }
    @Test
    public void shouldAcceptPositiveIntegersLessThanFive(
            @ParameterIntValues({1, 2, 3, 4}) int value) {
        assertThat(value).isBetween(1, 4);
    }
}
