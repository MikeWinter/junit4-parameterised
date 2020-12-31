package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"unused", "TestMethodWithIncorrectSignature"})
public class RuleFieldExample {
    private final static AtomicInteger ruleEvaluationCount = new AtomicInteger();
    private final static AtomicInteger anotherRuleEvaluationCount = new AtomicInteger();

    @Rule
    public final ExampleRule rule = new ExampleRule(ruleEvaluationCount);

    @Rule
    public final ExampleRule anotherRule = new ExampleRule(anotherRuleEvaluationCount);

    static boolean rulesHaveBeenProcessedForTest() {
        return ruleEvaluationCount.compareAndSet(1, 0)
                && anotherRuleEvaluationCount.compareAndSet(1, 0);
    }

    @Test
    public void aValidTest() {
        assert ruleEvaluationCount.get() > 0;
    }

    @Test
    public void aValidTest(int value) {
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
