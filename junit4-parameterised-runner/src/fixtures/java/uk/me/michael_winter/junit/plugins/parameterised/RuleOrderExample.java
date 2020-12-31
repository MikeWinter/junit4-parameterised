package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"unused", "TestMethodWithIncorrectSignature"})
public class RuleOrderExample {
    private static int ruleFieldOrdinal = 0;
    private static int ruleMethodOrdinal = 0;

    private final AtomicInteger order = new AtomicInteger();

    @Rule
    public final TestRule rule = (base, description) -> new Statement() {
        @Override
        public void evaluate() {
            ruleFieldOrdinal = order.incrementAndGet();
        }
    };

    public static boolean ruleFieldWasInvokedFirst() {
        return ruleFieldOrdinal < ruleMethodOrdinal;
    }

    @Rule
    public TestRule getRule() {
        return (base, description) -> new Statement() {
            @Override
            public void evaluate() {
                ruleMethodOrdinal = order.incrementAndGet();
            }
        };
    }

    @Test
    public void aValidTest() {
        assert ruleFieldOrdinal > 0 && ruleMethodOrdinal > 0;
    }

    @Test
    public void aValidTest(int value) {
        assert ruleFieldOrdinal > 0 && ruleMethodOrdinal > 0;
    }
}
