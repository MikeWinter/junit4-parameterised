package uk.me.michael_winter.junit.plugins.parameterised.runners;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.concurrent.atomic.AtomicInteger;

public class RuleFieldExample {
    private final static AtomicInteger ruleEvaluationCount = new AtomicInteger();
    private final static AtomicInteger anotherRuleEvaluationCount = new AtomicInteger();

    @Rule
    public final ExampleRule rule = new ExampleRule(ruleEvaluationCount);

    @Rule
    public final ExampleRule anotherRule = new ExampleRule(anotherRuleEvaluationCount);

    @BeforeClass
    public static void resetRules() {
        ruleEvaluationCount.set(0);
        anotherRuleEvaluationCount.set(0);
    }

    static boolean rulesHaveBeenProcessedForTest() {
        return ruleEvaluationCount.get() == 1 && anotherRuleEvaluationCount.get() == 1;
    }

    @Test
    public void aValidTest() {
        assert ruleEvaluationCount.get() > 0;
    }

    public static class ExampleRule implements TestRule {
        private final AtomicInteger evaluationCount;

        ExampleRule(AtomicInteger evaluationCount) {
            this.evaluationCount = evaluationCount;
        }

        @Override
        public Statement apply(Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    evaluationCount.incrementAndGet();
                    base.evaluate();
                }
            };
        }
    }
}
