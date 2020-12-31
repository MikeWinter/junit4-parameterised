package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.After;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"unused", "TestMethodWithIncorrectSignature"})
public class AfterExample {
    private final static AtomicInteger teardownCallCount = new AtomicInteger();
    private final static AtomicInteger anotherTeardownCallCount =  new AtomicInteger();

    static boolean hasBeenTornDownForTest() {
        return teardownCallCount.compareAndSet(1, 0)
                && anotherTeardownCallCount.compareAndSet(1, 0);
    }

    @After
    public void teardown() {
        teardownCallCount.incrementAndGet();
    }

    @After
    public void anotherTeardown() {
        anotherTeardownCallCount.incrementAndGet();
    }

    @Test
    public void aValidTest() {
        assert teardownCallCount.get() == 0 && anotherTeardownCallCount.get() == 0;
    }

    @Test
    public void aValidTest(int value) {
        assert teardownCallCount.get() == 0 && anotherTeardownCallCount.get() == 0;
    }
}
