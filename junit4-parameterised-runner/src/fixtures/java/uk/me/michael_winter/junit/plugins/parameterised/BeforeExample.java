package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"unused", "TestMethodWithIncorrectSignature"})
public class BeforeExample {
    private final static AtomicInteger setupCallCount = new AtomicInteger();
    private final static AtomicInteger anotherSetupCallCount = new AtomicInteger();

    static boolean hasBeenSetupForTest() {
        return setupCallCount.compareAndSet(1, 0)
                && anotherSetupCallCount.compareAndSet(1, 0);
    }

    @Before
    public void setup() {
        setupCallCount.incrementAndGet();
    }

    @Before
    public void anotherSetup() {
        anotherSetupCallCount.incrementAndGet();
    }

    @Test
    public void aValidTest() {
        assert setupCallCount.get() > 0;
    }

    @Test
    public void aValidTest(int value) {
        assert setupCallCount.get() > 0;
    }
}
