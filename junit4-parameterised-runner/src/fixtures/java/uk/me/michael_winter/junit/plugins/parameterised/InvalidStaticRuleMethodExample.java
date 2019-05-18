package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;

import static org.junit.Assert.fail;

public class InvalidStaticRuleMethodExample {
    @Rule
    public static TestRule getRule() {
        return (base, description) -> new Statement() {
            @Override
            public void evaluate() {
                fail("Should not be invoked");
            }
        };
    }

    @Test
    public void shouldNotBeExecuted() {
        fail("Should not be invoked");
    }
}
