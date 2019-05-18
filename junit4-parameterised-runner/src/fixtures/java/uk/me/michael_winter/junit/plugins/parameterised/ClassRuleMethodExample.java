package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ClassRuleMethodExample {
    @ClassRule
    public final static ExampleClassRule classRule = new ExampleClassRule();

    @ClassRule
    public final static ExampleClassRule anotherClassRule = new ExampleClassRule();

    public static boolean rulesHaveBeenProcessedOnce() {
        return classRule.hasBeenProcessedOnce() && anotherClassRule.hasBeenProcessedOnce();
    }

    @Test
    public void aValidTest() {
    }

    @Test
    public void anotherValidTest() {
    }

    public static class ExampleClassRule implements TestRule {
        private int applyCallCount = 0;

        @Override
        public Statement apply(Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    ++applyCallCount;
                    base.evaluate();
                }
            };
        }

        boolean hasBeenProcessedOnce() {
            return applyCallCount == 1;
        }
    }
}
