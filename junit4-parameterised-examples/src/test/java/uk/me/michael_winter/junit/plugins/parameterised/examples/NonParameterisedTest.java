package uk.me.michael_winter.junit.plugins.parameterised.examples;

import org.junit.Test;
import org.junit.runner.RunWith;
import uk.me.michael_winter.junit.plugins.parameterised.ParameterisedRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ParameterisedRunner.class)
public class NonParameterisedTest {
    @Test
    public void shouldRunNonParameterisedTests() {
        assertThat(1).isGreaterThan(0);
    }
}
