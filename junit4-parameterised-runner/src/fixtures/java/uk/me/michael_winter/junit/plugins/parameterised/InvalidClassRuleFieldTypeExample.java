package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;

import static org.junit.Assert.fail;

public class InvalidClassRuleFieldTypeExample {
    @ClassRule
    public static final Object rule = (TestRule) (base, description) -> new Statement() {
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
