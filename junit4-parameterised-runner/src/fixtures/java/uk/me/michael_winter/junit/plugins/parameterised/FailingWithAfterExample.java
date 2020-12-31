package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.After;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.fail;

@SuppressWarnings({"unused", "TestMethodWithIncorrectSignature"})
public class FailingWithAfterExample {
    private final static AtomicInteger teardownCallCount = new AtomicInteger();

    static boolean hasBeenTornDownForTest() {
        return teardownCallCount.compareAndSet(1, 0);
    }

    @After
    public void teardown() {
        teardownCallCount.incrementAndGet();
    }

    @Test
    public void aFailingTest() {
        fail("This test is expected to fail");
    }

    @Test
    public void aFailingTest(int value) {
        fail("This test is expected to fail");
    }
}
