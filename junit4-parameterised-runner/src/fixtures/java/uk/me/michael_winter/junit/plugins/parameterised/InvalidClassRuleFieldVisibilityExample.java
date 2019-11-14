package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;

import static org.junit.Assert.fail;

public class InvalidClassRuleFieldVisibilityExample {
    @ClassRule
    protected static final TestRule rule = (base, description) -> new Statement() {
        @Override
        public void evaluate() {
            fail("Should not be invoked");
        }
    };

    @Test
    public void shouldNotBeExecuted() {
        fail("Should not be invoked");
    }
}
