package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static org.junit.Assert.fail;

public class InvalidRuleMethodVisibilityExample {
    @Rule
    protected TestRule getRule() {
        return new ValidRule();
    }

    @Test
    public void shouldNotBeExecuted() {
        fail("Should not be invoked");
    }

    private static class ValidRule implements TestRule {
        @Override
        public Statement apply(Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() {
                    fail("Should not be invoked");
                }
            };
        }
    }
}
