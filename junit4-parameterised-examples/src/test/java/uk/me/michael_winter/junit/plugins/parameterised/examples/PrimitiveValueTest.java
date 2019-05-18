package uk.me.michael_winter.junit.plugins.parameterised.examples;

import org.junit.Test;
import org.junit.runner.RunWith;
import uk.me.michael_winter.junit.plugins.parameterised.ParameterIntValues;
import uk.me.michael_winter.junit.plugins.parameterised.ParameterisedRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ParameterisedRunner.class)
public class PrimitiveValueTest {
    @Test
    public void shouldAcceptPositiveIntegersLessThanFive(
            @ParameterIntValues({1, 2, 3, 4}) int value) {
        assertThat(value).isBetween(1, 4);
    }
}
